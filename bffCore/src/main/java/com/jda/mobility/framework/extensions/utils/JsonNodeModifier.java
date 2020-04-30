package com.jda.mobility.framework.extensions.utils;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jda.mobility.framework.extensions.model.ParserObject;

@Component
public class JsonNodeModifier {

	private static final String TILDE_DELIMITER = "~";
	private static final String PICKLIST_JSON_ATTRIBUTE = "pickIds";
	private static final String ROOT_SLASH = "/";
	private static final String RESPONSE_ID = "responseId";
	private static final String TIME_STAMP = "timestamp";

	/**
	 * @param jsonNode The JSON from where the errors needs to be checked
	 * @return boolean Return true if the JSON contains an error structure
	 */
	public boolean errorFlagModifier(JsonNode jsonNode) {
		boolean isError = false;
		isError = (!jsonNode.findPath(TIME_STAMP).isEmpty() || !jsonNode.findPath(RESPONSE_ID).isEmpty());
		return isError;
	}

	/**
	 * @param apiResponseMap Converts a Map of JSONs to a single JSON with keys
	 * @return JsonNode Return the combined JSON of all the JSONs in the map
	 */
	public JsonNode jsonNodeConverter(Map<String, JsonNode> apiResponseMap) {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.convertValue(apiResponseMap, JsonNode.class);
	}

	/**
	 * @param toBeEncoded JSON to be Base64 encoded
	 * @return String Base64 encoded String representation of the JSON
	 */
	public String encode(JsonNode toBeEncoded) throws JsonProcessingException, UnsupportedEncodingException {
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(toBeEncoded);
		json = json.trim().replaceAll("\\s", "");
		return Base64.getEncoder().encodeToString(json.getBytes("utf-8"));
	}

	/**
	 * This method parses the Json and stores the fieldNames as keys with values as
	 * list of path the fieldNames are present in
	 * 
	 * @param jsonNode The JSON for determining the field path
	 * @return Map&lt;String, List&lt;String&gt;&gt; Map of all the field paths for each field name
	 * 
	 */
	public Map<String, List<String>> fieldPath(JsonNode jsonNode) {
		List<ParserObject> parserObjectList = new ArrayList<>();

		if (jsonNode.getNodeType() == JsonNodeType.OBJECT) {
			getValueNode(jsonNode, parserObjectList, ROOT_SLASH, BffAdminConstantsUtils.EMPTY_SPACES);
		}
		Map<String, List<String>> jsonPath = new HashMap<>();
		for (ParserObject parserObject : parserObjectList) {
			String fieldName = parserObject.getFieldName();
			String path = parserObject.getPath();
			if (jsonPath.containsKey(fieldName)) {
				jsonPath.get(fieldName).add(path);
			} else {
				List<String> pathList = new ArrayList<>();
				pathList.add(path);
				jsonPath.put(fieldName, pathList);
			}
		}
		return jsonPath;
	}

	/**
	 * @param fieldNameToBeAdded The field name to which the count needs to be added
	 * @param fieldNameToBeFetched The field name from which the count needs to be fetched
	 * @param jsonNode JSON to add the count of the number of nodes in an array
	 * @return JsonNode JSON enriched with the count of the nummber of nodes
	 */
	public JsonNode addCount(String fieldNameToBeAdded, String fieldNameToBeFetched, JsonNode jsonNode) {

		ArrayNode arrayNode = (ArrayNode) jsonNode.findPath(fieldNameToBeFetched);
		ObjectNode objectNode = (ObjectNode) jsonNode;
		objectNode.set(fieldNameToBeAdded, JsonNodeFactory.instance.numberNode(arrayNode.size()));
		return objectNode;
	}

	/**
	 * @param fieldNameToBeSubractedFrom The field name which is the left operand of the subtraction
	 * @param fieldNameToBeSubracted The field name which is the right operand of the subtraction
	 * @param jsonNode JSON to which the subtracted value needs to be added
	 * @param fieldNameToBeAdded The field name to which the subtracted value needs to be added
	 * @return JsonNode JSON enriched with the subtracted value of inputs supplied
	 */
	public JsonNode addLimit(String fieldNameToBeSubractedFrom, String fieldNameToBeSubracted, JsonNode jsonNode,
			String fieldNameToBeAdded) {

		int operand1 = jsonNode.findPath(fieldNameToBeSubractedFrom).asInt();
		int operand2 = jsonNode.findPath(fieldNameToBeSubracted).asInt();
		ObjectNode objectNode = (ObjectNode) jsonNode;
		objectNode.set(fieldNameToBeAdded, JsonNodeFactory.instance.numberNode(operand1 - operand2));
		return objectNode;
	}

	/**
	 * @param jsonNode JSON with filtered results matching pattern
	 * @param pattern The filter pattern
	 * @param position The filter type
	 * @param fieldName The field name that needs to be filtered
	 * @return JsonNode JSON with the filtered results
	 */
	public JsonNode findEntitiesMatchingPattern(JsonNode jsonNode, String pattern, String position, String fieldName) {

		jsonNode = jsonNode.findPath(fieldName);

		ArrayNode resultArrayNode = JsonNodeFactory.instance.arrayNode();

		if (jsonNode.isArray()) {
			ArrayNode arrayNode = (ArrayNode) jsonNode;

			for (int i = 0; i < arrayNode.size(); i++) {
				if (position.contains("start") && arrayNode.get(i).textValue().startsWith(pattern)) {
					resultArrayNode.add(arrayNode.get(i));
				}
				if (position.contains("end") && arrayNode.get(i).textValue().endsWith(pattern)) {
					resultArrayNode.add(arrayNode.get(i));
				}
				if (position.contains("contain") && arrayNode.get(i).textValue().contains(pattern)) {
					resultArrayNode.add(arrayNode.get(i));
				}
			}
		}
		ObjectNode resultNode = JsonNodeFactory.instance.objectNode();
		resultNode.set(fieldName, resultArrayNode);

		return resultNode;
	}

	/**
	 * @param fieldName The node name of that needs to be removed
	 * @param jsonNode The JSON from which the field and path needs to be removed
	 * @param fieldPath The node path of the field that needs to be removed
	 * @return JsonNode JSON result after removal of field name at field path
	 */
	public JsonNode removeField(String fieldName, JsonNode jsonNode, String fieldPath) {
		ObjectNode objectNode = (ObjectNode) jsonNode;
		removeNode(fieldName, objectNode, null, BffAdminConstantsUtils.EMPTY_SPACES, fieldPath, false);
		return objectNode;
	}

	private void removeNode(String fieldName, ObjectNode parentNode, ObjectNode currentParent, String path,
			String fieldPath, boolean isDeleted) {
		Iterator<Map.Entry<String, JsonNode>> it = null;

		if (currentParent != null) {
			it = currentParent.fields();
		} else {
			it = parentNode.fields();
		}
		while (it.hasNext()) {
			if (isDeleted) {
				return;
			}
			Map.Entry<String, JsonNode> field = it.next();
			if (field.getValue().getNodeType() == JsonNodeType.OBJECT) {
				String path1 = path + ROOT_SLASH + field.getKey();
				ObjectNode objectNode1 = (ObjectNode) field.getValue();
				if (path1.equals(fieldPath) && field.getKey().equals(fieldName) && (currentParent != null)) {
					currentParent.remove(fieldName);
					return;
				}
				removeNode(fieldName, parentNode, objectNode1, path1, fieldPath, isDeleted);
			} else if (field.getValue().isArray()) {
				boolean isObject = false;
				List<JsonNode> objlist = new ArrayList<>();
				for (JsonNode jsonNode1 : field.getValue()) {
					objlist.add(jsonNode1);
				}
				for (JsonNode currentNode : objlist) {
					if (currentNode.getNodeType().equals(JsonNodeType.OBJECT) || currentNode.isArray()) {
						String path2 = path + ROOT_SLASH + field.getKey();
						ObjectNode objectNode2 = (ObjectNode) currentNode;
						if (path2.equals(fieldPath) && field.getKey().equals(fieldName) && (currentParent != null)) {
							currentParent.remove(fieldName);
							return;
						}
						removeNode(fieldName, parentNode, objectNode2, path2, fieldPath, isDeleted);
					}
				}
				if (!isObject) {
					String path3 = path + ROOT_SLASH + field.getKey();
					if (path3.equals(fieldPath) && field.getKey().equals(fieldName) && (parentNode != null)) {
						parentNode.remove(fieldName);
						return;
					}
				}
			} else {
				if (path.equals(BffAdminConstantsUtils.EMPTY_SPACES)) {
					String path4 = ROOT_SLASH + field.getKey();
					if (path4.equals(fieldPath) && field.getKey().equals(fieldName) && (parentNode != null)) {
						parentNode.remove(fieldName);
						return;
					}
				} else {
					String path2 = path + ROOT_SLASH + field.getKey();
					if (path2.equals(fieldPath) && field.getKey().equals(fieldName) && (currentParent != null)) {
						currentParent.remove(fieldName);
						return;
					}
				}
			}
		}
	}

	/**
	 * @param fieldName The node name of the field for which to find the paths
	 * @return List&lt;String&gt; List of all field paths having the field with fieldName in JSON
	 */
	public List<String> findPath(String fieldName, JsonNode jsonNode) {
		List<String> path = null;
		Map<String, List<String>> map = fieldPath(jsonNode);
		if (map.containsKey(fieldName)) {
			return map.get(fieldName);
		}
		return path;
	}

	/**
	 * @param parentNode The parent JSON
	 * @param fieldPath The path where the field needs to be inserted
	 * @param nodeToBeInserted The node that needs to be inserted
	 * @param fieldNameToBeInserted The name of the field that needs to be inserted at fieldPath
	 * @return JsonNode JSON result after insertion of field name at field path
	 */
	public JsonNode insertField(JsonNode parentNode, String fieldPath, JsonNode nodeToBeInserted,
			String fieldNameToBeInserted) {
		ObjectNode objectNode = (ObjectNode) parentNode;
		insertNode(objectNode, null, BffAdminConstantsUtils.EMPTY_SPACES, fieldPath, false, nodeToBeInserted,
				fieldNameToBeInserted);
		return objectNode;
	}

	/**
	 * @param jsonNode The JSON where the field needs to be copied
	 * @param fieldName The name of the field that needs to be copied
	 * @param nodePath The node path from where the field needs to be copied
	 * @param newNodePath The node path to which the field needs to be copied
	 * @return JsonNode JSON result after copying the field at new node path
	 */
	public JsonNode copyField(JsonNode jsonNode, String fieldName, String nodePath, String newNodePath) {

		JsonNode sourceJsonNode = jsonNode.at(nodePath);

		if (newNodePath.equals(ROOT_SLASH)) {
			((ObjectNode) jsonNode).set(fieldName, sourceJsonNode);
			return jsonNode;
		}
		if (!sourceJsonNode.isObject() && !sourceJsonNode.isArray()) {
			sourceJsonNode = createJsonNode("text", sourceJsonNode.asText());
		}
		jsonNode = insertField(jsonNode, nodePath, sourceJsonNode, newNodePath);
		return jsonNode;
	}

	public JsonNode requestModification(JsonNode jsonNode) {
		ObjectNode objectNode = (ObjectNode) jsonNode;
		String[] values = objectNode.at("/confirmations").get(0).at("/destinations").get(0).at("/pickIds").asText()
				.split(TILDE_DELIMITER);
		ObjectNode objectNode1 = JsonNodeFactory.instance.objectNode();
		for (int i = 0; i < values.length; i++) {
			if (i == 0) {
				objectNode1.set(PICKLIST_JSON_ATTRIBUTE, JsonNodeFactory.instance.arrayNode().add(values[i]));
				if (values[i].equals(BffAdminConstantsUtils.EMPTY_SPACES)) {
					objectNode1.set(PICKLIST_JSON_ATTRIBUTE, JsonNodeFactory.instance.arrayNode());
				}
			} else {
				ArrayNode array = (ArrayNode) objectNode1.get(PICKLIST_JSON_ATTRIBUTE);
				array.add(values[i]);
				objectNode1.set(PICKLIST_JSON_ATTRIBUTE, array);
				if (values[i].equals(BffAdminConstantsUtils.EMPTY_SPACES)) {
					objectNode1.set(PICKLIST_JSON_ATTRIBUTE, JsonNodeFactory.instance.arrayNode());
				}
			}
		}
		JsonNode jsonNode1 = objectNode1;
		insertField(jsonNode1.at("/pickIds"), "/confirmations/destinations/pickIds", jsonNode1, PICKLIST_JSON_ATTRIBUTE);
		return jsonNode;
	}

	private void insertNode(ObjectNode parentNode, ObjectNode currentParent, String path, String fieldPath,
			boolean isInserted, JsonNode nodeToBeInserted, String fieldNameToBeInserted) {
		Iterator<Map.Entry<String, JsonNode>> it = null;
		if (currentParent != null) {
			it = currentParent.fields();
		} else {
			it = parentNode.fields();
		}
		while (it.hasNext()) {
			if (isInserted) {
				return;
			}
			Map.Entry<String, JsonNode> field = it.next();
			if (field.getValue().getNodeType() == JsonNodeType.OBJECT) {
				String path1 = path + ROOT_SLASH + field.getKey();
				ObjectNode objectNode1 = (ObjectNode) field.getValue();
				if (path1.equals(fieldPath) && objectNode1 != null) {
					objectNode1.set(fieldNameToBeInserted, nodeToBeInserted);
					return;

				}
				insertNode(parentNode, objectNode1, path1, fieldPath, isInserted, nodeToBeInserted,
						fieldNameToBeInserted);
			} else if (field.getValue().isArray()) {
				boolean isObject = false;
				ArrayNode arrayNode = (ArrayNode) field.getValue();

				for (JsonNode currentNode : arrayNode) {
					if (currentNode.getNodeType().equals(JsonNodeType.OBJECT) || currentNode.isArray()) {
						String path2 = path + ROOT_SLASH + field.getKey();
						ObjectNode objectNode2 = (ObjectNode) currentNode;
						if (path2.equals(fieldPath) && parentNode != null) {
							objectNode2.set(fieldNameToBeInserted, nodeToBeInserted);
							parentNode.set(field.getKey(), arrayNode);
							return;
						}
						insertNode(parentNode, objectNode2, path2, fieldPath, isInserted, nodeToBeInserted,
								fieldNameToBeInserted);
					}
				}
				if (isObject) {
					String path3 = path + ROOT_SLASH + field.getKey();
					if (path3.equals(fieldPath) && parentNode != null) {
						parentNode.set(fieldNameToBeInserted, nodeToBeInserted);
						return;
					}
				}
			} else {
				if (path.equals(BffAdminConstantsUtils.EMPTY_SPACES)) {
					String path4 = ROOT_SLASH;
					if (path4.equals(fieldPath) && field.getKey().equalsIgnoreCase(fieldNameToBeInserted)
							&& parentNode != null) {
						parentNode.set(fieldNameToBeInserted, nodeToBeInserted);
						return;
					}
				} else {
					String path2 = path + ROOT_SLASH + field.getKey();
					if (path2.equals(fieldPath) && currentParent != null) {
						currentParent.set(fieldNameToBeInserted, nodeToBeInserted);
						return;
					}
				}
			}
		}
	}
	
	/**
	 * @param actual The actual String to which a string needs to prepended or appended
	 * @param toBeAppended The String that needs to prepended or appended
	 * @param preOrPost the operation type - PREPEND or APPEND
	 * @return String The resultant string after the PREPEND or APPEND operation
	 */
	public String appendString(String actual, String toBeAppended, String preOrPost) {
		if (preOrPost.equalsIgnoreCase(BffAdminConstantsUtils.PREPEND)) {
			return toBeAppended + actual;
		}
		return actual + toBeAppended;
	}

	/**
	 * @param inputFormat The input date format
	 * @param outputFormat The output date format desired
	 * @param date The date whose format needs to be changed from input to output format
	 * @return String The resultant date after converting the format
	 * @throws ParseException
	 */
	public String dateFormatting(String inputFormat, String outputFormat, String date) throws ParseException {
		SimpleDateFormat inputDateFormat = new SimpleDateFormat(inputFormat);
		SimpleDateFormat outputDateFormat = new SimpleDateFormat(outputFormat);
		return outputDateFormat.format(inputDateFormat.parse(date));
	}

	public double doubleSum(double existingValue, double valueToBeAdded) {
		return existingValue + valueToBeAdded;
	}

	public long longSum(long existingValue, long valueToBeAdded) {
		return existingValue + valueToBeAdded;
	}

	public long longSubtract(long existingValue, long valueToBeSubtracted) {
		return existingValue - valueToBeSubtracted;
	}

	public long longDivide(long existingValue, long valueToBeDivided) {
		return existingValue - valueToBeDivided;
	}

	public long longMultiply(long existingValue, long valueToBeMultiplied) {
		return existingValue * valueToBeMultiplied;
	}

	public double doubleSubtract(double existingValue, double valueToBeSubtracted) {
		return existingValue - valueToBeSubtracted;
	}

	public double doubleMultiply(double existingValue, double valueToBeMultiplied) {
		return existingValue * valueToBeMultiplied;
	}

	public double doubleDivide(double existingValue, double valueToBeDivided) {
		return existingValue - valueToBeDivided;
	}

	public String stripString(int startIndex, int endIndex, String toBeStripped) {
		return toBeStripped.substring(startIndex, endIndex);
	}

	public JsonNode requestModification(JsonNode jsonNode, String attributeName) {
		ObjectNode objectNode = (ObjectNode) jsonNode;
		String[] values = objectNode.at(ROOT_SLASH + attributeName).asText().split(TILDE_DELIMITER);
		if (values.length == 0) {
			objectNode.set(attributeName, JsonNodeFactory.instance.arrayNode());
			return objectNode;
		}
		for (int i = 0; i < values.length; i++) {
			if (i == 0) {

				objectNode.set(attributeName, JsonNodeFactory.instance.arrayNode().add(values[i]));
				if (values[i].equals(BffAdminConstantsUtils.EMPTY_SPACES)) {
					objectNode.set(attributeName, JsonNodeFactory.instance.arrayNode());
				}
			} else {
				ArrayNode array = (ArrayNode) objectNode.get(attributeName);
				array.add(values[i]);
				objectNode.set(attributeName, array);
				if (values[i].equals(BffAdminConstantsUtils.EMPTY_SPACES)) {
					objectNode.set(attributeName, JsonNodeFactory.instance.arrayNode());
				}
			}
		}

		return objectNode;
	}

	private void getValueNode(JsonNode jsonNode, List<ParserObject> parserObjectList, String parent, String path) {
		Iterator<Map.Entry<String, JsonNode>> it = jsonNode.fields();
		while (it.hasNext()) {
			Map.Entry<String, JsonNode> field = it.next();
			if (field.getValue().getNodeType() == JsonNodeType.OBJECT) {
				String path1 = path + ROOT_SLASH + field.getKey();
				parserObjectList.add(new ParserObject(field.getKey(), field.getValue(),
						field.getValue().getNodeType().toString(), parent, path1));
				getValueNode(field.getValue(), parserObjectList, field.getKey(), path1);
			} else if (field.getValue().isArray()) {
				ifGetValueArray(parserObjectList, parent, path, field);
			} else {
				if (path.equals(BffAdminConstantsUtils.EMPTY_SPACES)) {
					parserObjectList.add(new ParserObject(field.getKey(), field.getValue(),
							field.getValue().getNodeType().toString(), parent, ROOT_SLASH + field.getKey()));
				} else {
					parserObjectList.add(new ParserObject(field.getKey(), field.getValue(),
							field.getValue().getNodeType().toString(), parent, path + ROOT_SLASH + field.getKey()));
				}
			}
		}
	}


	private void ifGetValueArray(List<ParserObject> parserObjectList, String parent, String path,
			Map.Entry<String, JsonNode> field) {
		boolean isObject = false;
		List<JsonNode> objlist = new ArrayList<>();
		for (JsonNode jsonNode1 : field.getValue()) {
			objlist.add(jsonNode1);
		}
		for (JsonNode currentNode : objlist) {
			if (currentNode.getNodeType().equals(JsonNodeType.OBJECT) || currentNode.isArray()) {
				String path2 = path + ROOT_SLASH + field.getKey();
				parserObjectList.add(new ParserObject(currentNode.asText(), currentNode,
						currentNode.getNodeType().toString(), parent, path2));
				getValueNode(currentNode, parserObjectList, field.getKey(), path2);
				isObject = true;
			}

		}
		if (!isObject) {
			parserObjectList.add(new ParserObject(field.getKey(), field.getValue(),
					field.getValue().getNodeType().toString(), parent, path + ROOT_SLASH + field.getKey()));
		}
	}

	/**
	 * @param key The key of the new Request Object to be created
	 * @param value The value of the new Request Object to be created
	 * @param type The data type of the new Request Object to be created
	 * @return JsonNode The node created with the requested specification
	 */
	public JsonNode createObject(String key, String value, String type) {

		JsonNode jsonNode = JsonNodeFactory.instance.objectNode();

		String[] keys = key.split(BffAdminConstantsUtils.COMMA);
		String[] values = value.split(BffAdminConstantsUtils.COMMA);
		String[] types = type.split(BffAdminConstantsUtils.COMMA);
		ObjectNode obj = (ObjectNode) jsonNode;
		for (int i = 0; i < keys.length; i++) {
			obj.set(keys[i], createJsonNode(types[i], values[i]));
		}
		jsonNode = obj;

		return jsonNode;

	}

	/**
	 * @param inputNode The JSON to which a string needs to be prepended or appended
	 * @param toBeAppended The value to be pre/appended
	 * @param position The operation - PREPEND / APPEND
	 * @param path The JSON path where the value needs to be prepended / appended
	 * @return JsonNode The node created with the requested specification
	 */
	public JsonNode prePostAppend(JsonNode inputNode, String toBeAppended, String position, String path) {

		String actual = inputNode.at(path).asText();
		String[] sa = path.split(ROOT_SLASH);

		String propertyToBeChanged = sa[sa.length - 1];
		int pos = path.lastIndexOf(ROOT_SLASH);
		if (pos == 0)
			pos = 1;

		return insertField(inputNode, path.substring(0, pos),
				createJsonNode("text", appendString(actual, toBeAppended, position)), propertyToBeChanged);
	}

	/**
	 * @param inputFormat The input date format
	 * @param outputFormat The output date format
	 * @param inputNode The JSON which contains the date node
	 * @param path The path to the date node
	 * @return JsonNode The node transformed with the requested specification
	 */
	public JsonNode dateModifier(String inputFormat, String outputFormat, JsonNode inputNode, String path)
			throws ParseException {
		String s = inputNode.at(path).asText();
		String[] sa = path.split(ROOT_SLASH);
		String propertyToBeChanged = sa[sa.length - 1];
		String test = path.substring(0, path.lastIndexOf(ROOT_SLASH));

		return insertField(inputNode, test, createJsonNode("text", dateFormatting(inputFormat, outputFormat, s)),
				propertyToBeChanged);

	}

	public String getJSONNodeValue(JsonNode jsonNode, String path) {
		if (jsonNode == null) {
			return BffAdminConstantsUtils.WMS;
		}
		return jsonNode.at(path).asText();
	}

	public JsonNode getJSONNode(JsonNode jsonNode, String path, String position) {
		jsonNode = jsonNode.at(path).get(Integer.parseInt(position));
		return jsonNode;
	}

	/**
	 * @param type The data type of the node to be created
	 * @param value The value of the node to be created
	 * @return JsonNode The node created with the requested specification
	 */
	public JsonNode createJsonNode(String type, String value) {
		JsonNode jsonNode = JsonNodeFactory.instance.missingNode();
		if (type.equals("text")) {
			jsonNode = JsonNodeFactory.instance.textNode(value);
		}
		if (type.equals("decimal")) {
			jsonNode = JsonNodeFactory.instance.numberNode(Double.parseDouble(value));
		}
		if (type.equals("integer")) {
			jsonNode = JsonNodeFactory.instance.numberNode(Integer.parseInt(value));
		}

		return jsonNode;
	}
}
