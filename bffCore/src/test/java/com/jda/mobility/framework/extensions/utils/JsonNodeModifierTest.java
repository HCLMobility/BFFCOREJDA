package com.jda.mobility.framework.extensions.utils;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jda.mobility.framework.extensions.model.OnFailure;
import com.jda.mobility.framework.extensions.model.OnSuccess;
import com.jda.mobility.framework.extensions.model.Parameter;
import com.jda.mobility.framework.extensions.model.ProductApiInvokeRequest;
import com.jda.mobility.framework.extensions.model.RequestParam;

@RunWith(SpringJUnit4ClassRunner.class)
public class JsonNodeModifierTest {

	JsonNodeModifier jsonNodeModifier = new JsonNodeModifier();
	ProductApiInvokeRequest productApiInvokeRequest = new ProductApiInvokeRequest();
	JsonNode node, node1, nodeToBeInserted;
	ObjectNode parentNode, currentParent;
	List<RequestParam> paramList = new ArrayList<RequestParam>();
	Map<String, JsonNode> apiResponseMap;

	@Before
	public void init() throws JsonMappingException, JsonProcessingException {

		productApiInvokeRequest.setRegistryApi(UUID.randomUUID());
		productApiInvokeRequest.setRegistry(UUID.randomUUID());
		productApiInvokeRequest.setSelectedItem("");
		productApiInvokeRequest.setSelectedParam("");
		productApiInvokeRequest.setSelectedParamType("");
		productApiInvokeRequest.setSelectedParamValue("");
		productApiInvokeRequest.setSelectedResponse("");
		productApiInvokeRequest.setOnSuccess(new OnSuccess());
		productApiInvokeRequest.setOnFailure(new OnFailure());
		productApiInvokeRequest.setSelectedResponseValue("");
		productApiInvokeRequest.setRequestEndpoint("/WarehouseId/workAreas");
		productApiInvokeRequest.setRequestMethod("GET");
		RequestParam requestParam = new RequestParam();
		requestParam.setType("String");
		requestParam.setValue("test");
		requestParam.setAdditionalProperty("property", "demo");
		requestParam.setParameter(new Parameter());
		paramList.add(requestParam);
		paramList.add(requestParam);
		StringBuilder sb = new StringBuilder();
		sb.append("{\r\n" + "	\"label\": null,\r\n" + "	\"hideLabel\": true,\r\n" + "	\"mask\": false,\r\n"
				+ "	\"tableView\": true,\r\n" + "	\"alwaysEnabled\": false,\r\n" + "	\"type\": \"header\",\r\n"
				+ "	\"input\": true,\r\n" + "	\"key\": \"header\",\r\n" + "	\"components\": [{\r\n"
				+ "		\"key\": \"textField1\",\r\n" + "		\"alignment\": \"left\",\r\n"
				+ "		\"style\": {\r\n" + "			\"style\": \"primary\",\r\n"
				+ "			\"fontType\": \"small\",\r\n" + "			\"fontSize\": \"12\",\r\n"
				+ "			\"fontColor\": \"Secondary\",\r\n" + "			\"backgroundColor\": \"Primary\",\r\n"
				+ "			\"fontWeight\": \"bold\",\r\n" + "			\"width\": \"20\",\r\n"
				+ "			\"height\": \"20\",\r\n" + "			\"Padding\": \"0,0,0,20\",\r\n"
				+ "			\"Margin\": \"0,0,0,20\"\r\n" + "		},\r\n" + "		\"icon\": false,\r\n"
				+ "		\"autoCorrect\": false,\r\n" + "		\"capitalization\": false,\r\n"
				+ "		\"type\": \"textfield\",\r\n" + "		\"input\": true,\r\n"
				+ "		\"defaultValue\": \"\",\r\n" + "		\"tableView\": true,\r\n" + "		\"label\": {\r\n"
				+ "			\"locale\": \"en\",\r\n" + "			\"rbkey\": \"name2\",\r\n"
				+ "			\"rbvalue\": \"name2\",\r\n" + "			\"type\": \"ADMIN_UI\",\r\n"
				+ "			\"uid\": \"04e87b2b-2124-4996-98a1-fd8ac70bbbd7\"\r\n" + "		},\r\n"
				+ "		\"validate\": {\r\n" + "			\"min\": \"\",\r\n" + "			\"max\": \"\",\r\n"
				+ "			\"integer\": \"\"\r\n" + "		},\r\n" + "		\"fieldDependency\": {\r\n"
				+ "			\"show\": {\r\n" + "				\"condition\": \"\"\r\n" + "			},\r\n"
				+ "			\"hide\": {\r\n" + "				\"condition\": \"\"\r\n" + "			},\r\n"
				+ "			\"enable\": {\r\n" + "				\"condition\": \"\"\r\n" + "			},\r\n"
				+ "			\"disable\": {\r\n" + "				\"condition\": \"\"\r\n" + "			},\r\n"
				+ "			\"setValue\": [],\r\n" + "			\"setRequired\": {\r\n"
				+ "				\"condition\": \"\"\r\n" + "			},\r\n" + "			\"hidden\": false,\r\n"
				+ "			\"disabled\": false,\r\n" + "			\"required\": false\r\n" + "		},\r\n"
				+ "		\"events\": [{\r\n" + "			\"event\": \"oncomplete\",\r\n" + "			\"action\": {}\r\n"
				+ "		}, {\r\n" + "			\"event\": \"onfocus\",\r\n" + "			\"action\": {\r\n"
				+ "				\"actionType\": \"INVOKE_API\",\r\n" + "				\"properties\": {\r\n"
				+ "					\"context\": \"\",\r\n"
				+ "					\"registry\": \"51eccda0-f3ca-4f81-a390-38d1f87e96f4\",\r\n"
				+ "					\"registryApi\": \"0c0ce506-96de-4061-a594-0552a5dc0fb2\",\r\n"
				+ "					\"selectedParamType\": \"REQUEST BODY\",\r\n"
				+ "					\"selectedParam\": \"CONTEXTVALUE\",\r\n"
				+ "					\"selectedParamValue\": \"\",\r\n"
				+ "					\"selectedResponse\": \"\",\r\n"
				+ "					\"selectedResponseValue\": \"\",\r\n" + "					\"rawValue\": \"\",\r\n"
				+ "					\"onSuccess\": {\r\n" + "						\"actionType\": \"\"\r\n"
				+ "					},\r\n" + "					\"onFailure\": {\r\n"
				+ "						\"actionType\": \"\"\r\n" + "					},\r\n"
				+ "					\"regName\": \"nasiU\",\r\n" + "					\"version\": \"1.0\",\r\n"
				+ "					\"basePath\": \"/ws/auth\",\r\n"
				+ "					\"contextPath\": \"localhost\",\r\n"
				+ "					\"requestEndpoint\": \"/codes\",\r\n"
				+ "					\"requestMethod\": \"POST\",\r\n" + "					\"requestParam\": [{\r\n"
				+ "						\"parameter\": {\r\n"
				+ "							\"propertyName\": \"codeValue\",\r\n"
				+ "							\"propertyType\": \"string\"\r\n" + "						},\r\n"
				+ "						\"type\": \"REQUEST BODY\",\r\n"
				+ "						\"value\": \"CONTEXTVALUE.Tessss\"\r\n" + "					}],\r\n"
				+ "					\"responseSchema\": []\r\n" + "				}\r\n" + "			}\r\n"
				+ "		}, {\r\n" + "			\"event\": \"onblur\",\r\n" + "			\"action\": {}\r\n"
				+ "		}, {\r\n" + "			\"event\": \"onchange\",\r\n" + "			\"action\": {}\r\n"
				+ "		}],\r\n" + "		\"fieldId\": \"\",\r\n" + "		\"placeholder\": {\r\n"
				+ "			\"locale\": \"en\",\r\n" + "			\"rbkey\": \"Key201032\",\r\n"
				+ "			\"rbvalue\": \"Field2\",\r\n" + "			\"type\": \"ADMIN_UI\",\r\n"
				+ "			\"uid\": \"0a91724c-62ca-4e79-ae04-601ad5e21343\"\r\n" + "		},\r\n"
				+ "		\"description\": {\r\n" + "			\"locale\": \"en\",\r\n"
				+ "			\"rbkey\": \"name6\",\r\n" + "			\"rbvalue\": \"name6\",\r\n"
				+ "			\"type\": \"ADMIN_UI\",\r\n"
				+ "			\"uid\": \"080a30b5-b793-4883-a053-c81fd33b4027\"\r\n" + "		}\r\n" + "	}]\r\n" + "}");

		productApiInvokeRequest.setRequestParam(paramList);
		ObjectMapper mapper = new ObjectMapper();
		node = mapper.convertValue(productApiInvokeRequest, JsonNode.class);
		node1 = mapper.readTree(sb.toString());

	}

	@Before
	public void setUp() {
		final Date date = Mockito.mock(Date.class);
		Mockito.when(date.getTime()).thenReturn(30L);

	}

	@Test
	public void testFieldPath() {

		Assert.assertNotEquals(jsonNodeModifier.fieldPath(node1), node1);

	}

	@Test
	public void testCopyField() {

		node = jsonNodeModifier.copyField(node, "type", "/requestParam/type", "/requestParam/type");
		assertEquals(0, node.get("layer").asInt());
	}

	@Test
	public void testRemoveField() {

		node = jsonNodeModifier.removeField("type", node, "/requestParam/type");
		assertEquals(0, node.get("layer").asInt());
	}

	@Test
	public void testFindPath() {
		Assert.assertEquals("/requestMethod", jsonNodeModifier.findPath("requestMethod", node).get(0));
	}

	@Test
	public void testInsertField() {
		Assert.assertEquals(jsonNodeModifier.insertField(node1, "/components/style", node, "clone"), node1);
	}

	@Test
	public void testAppendString() {
		String str=jsonNodeModifier.appendString("/components/style", "/components/style", "clone");
		Assert.assertEquals("/components/style/components/style", str);
	}

	@Test
	public void testRequestModification() {
		node = jsonNodeModifier.requestModification(node, "clone");
		assertEquals(0, node.get("layer").asInt());
	}

	@Test
	public void testCreateObject() {
		node =jsonNodeModifier.createObject("201", "clone", "clone");
		assertTrue(node.has("201"));
	}

	@Test
	public void testGetJSONNodeValue() {
		String response =jsonNodeModifier.getJSONNodeValue(node, "/components/style");
		Assert.assertTrue(response.isBlank());
	}

	@Test
	public void testPrePostAppend() {
		node =jsonNodeModifier.prePostAppend(node, "/components/style", "clone", "/components/style");
		Assert.assertEquals(0, node.get("layer").asInt());
	}

	@Test
	public void doubleSum() {
		double response=jsonNodeModifier.doubleSum(20.2, 23.5);
		Assert.assertEquals(43.7, response, .1);
	}

	@Test
	public void longSum() {
		double response=jsonNodeModifier.longSum(230, 220);
		Assert.assertEquals(450.0, response, .1);
	}

	@Test
	public void longSubtract() {

		double response=jsonNodeModifier.longSubtract(250, 256);
		Assert.assertEquals(-6.0, response, .1);
	}

	@Test
	public void longDivide() {
		double response=jsonNodeModifier.longDivide(245, 500);
		Assert.assertEquals(-255.0, response, .1);
	}

	@Test
	public void longMultiply() {
		double response=jsonNodeModifier.longMultiply(365, 450);
		Assert.assertEquals(164250.0, response, .1);
	}

	@Test
	public void doubleSubtract() {
		double response=jsonNodeModifier.doubleSubtract(450, 456);
		Assert.assertEquals(-6.0, response, .1);
	}

	@Test
	public void doubleMultiply() {
		double response=jsonNodeModifier.doubleMultiply(500, 562);
		Assert.assertEquals(281000.0, response, .1);
	}

	@Test
	public void doubleDivide() {
		double response=jsonNodeModifier.doubleDivide(453, 456);
		Assert.assertEquals(-3.0, response, .1);
	}

	@Test
	public void stripString() {
		String response=jsonNodeModifier.stripString(2, 5, "clone");
		Assert.assertEquals("one", response);
	}
	
	@Test
	public void errorFlagModifier() {
		boolean response=jsonNodeModifier.errorFlagModifier(node);
		Assert.assertFalse(response);
	}
	
	@Test
	public void jsonNodeConverter() {
		apiResponseMap = new HashMap<>();
		apiResponseMap.put("testNode", node);
		JsonNode response=jsonNodeModifier.jsonNodeConverter(apiResponseMap);
		Assert.assertTrue(response.get("testNode").isObject());
	}
	
	@Test
	public void encode() throws JsonProcessingException, UnsupportedEncodingException {
		String response=jsonNodeModifier.encode(node);
		Assert.assertTrue(response.length()>0);
	}
	
	
	@Test
	public void findEntitiesMatchingPattern() throws JsonProcessingException, UnsupportedEncodingException {
		
		String pattern = "/";
		String position = "3";
		String fieldName ="/requestParam/type";
		JsonNode response=jsonNodeModifier.findEntitiesMatchingPattern(node,pattern,position,fieldName);
		Assert.assertTrue(response.has("/requestParam/type"));
	}

}
