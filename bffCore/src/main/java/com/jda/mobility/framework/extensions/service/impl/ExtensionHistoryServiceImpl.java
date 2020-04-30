/**
 * 
 */
package com.jda.mobility.framework.extensions.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.MapDifference;
import com.google.common.collect.MapDifference.ValueDifference;
import com.google.common.collect.Maps;
import com.jda.mobility.framework.extensions.common.BffResponse;
import com.jda.mobility.framework.extensions.dto.ExtensionVarianceDto;
import com.jda.mobility.framework.extensions.dto.FieldObjDto;
import com.jda.mobility.framework.extensions.entity.Field;
import com.jda.mobility.framework.extensions.entity.Flow;
import com.jda.mobility.framework.extensions.entity.Form;
import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.model.FieldComponent;
import com.jda.mobility.framework.extensions.repository.FieldRepository;
import com.jda.mobility.framework.extensions.repository.FlowRepository;
import com.jda.mobility.framework.extensions.repository.FormRepository;
import com.jda.mobility.framework.extensions.service.ExtensionHistoryService;
import com.jda.mobility.framework.extensions.transformation.FieldComponentConverter;
import com.jda.mobility.framework.extensions.transformation.FormTransformation;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.ExtensionType;
import com.jda.mobility.framework.extensions.utils.BffMessageConstantsUtils.StatusCode;
import com.jda.mobility.framework.extensions.utils.BffResponseCode;

/**
 * The EndPoint implementations to extract extension history related
 * information.
 * 
 * @author HCL Technologies
 */
@Service
public class ExtensionHistoryServiceImpl implements ExtensionHistoryService {

	private static final Logger LOGGER = LogManager.getLogger(ExtensionHistoryServiceImpl.class);
	@Autowired
	private FlowRepository flowRepo;
	@Autowired
	private FormRepository formRepo;
	@Autowired
	private FieldRepository fieldRepo;
	@Autowired
	private BffResponse bffResponse;
	@Autowired
	private FormTransformation formTransformation;
	@Autowired
	private FieldComponentConverter fieldComponentConverter;
	private static final String FIELD_MSG = "Field : ";
	
	/**Fetches extension history between two objects as left and right value
	 * 
	 * @param extendedObjectId
	 * @param parentObjectId
	 * @param extensionType
	 * @return BffCoreResponse
	 */
	@Override
	public BffCoreResponse fetchExtensionHistory(UUID extendedObjectId, UUID parentObjectId,
			ExtensionType extensionType) {
		BffCoreResponse bffCoreResponse = null;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			TypeReference<HashMap<String, Object>> type = new TypeReference<>() {
			};
			Map<String, Object> leftMap = null;
			Map<String, Object> rightMap = null;
			switch (extensionType) {
			case FLOW:
				Flow extendedFlow = flowRepo.findById(extendedObjectId).orElseThrow();
				Flow parentFlow = null;
				if (parentObjectId != null) {
					parentFlow = flowRepo.findById(parentObjectId).orElseThrow();
				} else {
					parentFlow = flowRepo.findById(extendedFlow.getExtendedFromFlowId()).orElseThrow();
				}
				String extendedFlowStr = objectMapper.writeValueAsString(extendedFlow);
				String parentFlowStr = objectMapper.writeValueAsString(parentFlow);

				leftMap = objectMapper.readValue(parentFlowStr, type);
				rightMap = objectMapper.readValue(extendedFlowStr, type);

				break;
			case FORM:
				Form extendedForm = formRepo.findById(extendedObjectId).orElseThrow();
				Form parentForm = null;
				if (parentObjectId != null) {
					parentForm = formRepo.findById(parentObjectId).orElseThrow();
				} else {
					parentForm = formRepo.findById(extendedForm.getExtendedFromFormId()).orElseThrow();
				}
				String extendedFormStr = objectMapper.writeValueAsString(extendedForm);
				String parentFormStr = objectMapper.writeValueAsString(parentForm);

				leftMap = objectMapper.readValue(parentFormStr, type);
				rightMap = objectMapper.readValue(extendedFormStr, type);

				break;
			case FIELD:
				Field extendedFieldObj = fieldRepo.findById(extendedObjectId).orElseThrow();
				Field parentFieldObj = null;
				if (parentObjectId != null) {
					parentFieldObj = fieldRepo.findById(parentObjectId).orElseThrow();
				} else if (extendedFieldObj.getExtendedFromFieldId() != null) {
					 Optional<Field> parentField = fieldRepo.findById(extendedFieldObj.getExtendedFromFieldId());
					 if(parentField.isPresent()) {
						 parentFieldObj = parentField.get();
					 }else {
						// comparison for new field is disallowed.
							return bffResponse.response(null, BffResponseCode.COMPARE_MISSING_PARENT_API_CODE,
									BffResponseCode.COMPARE_MISSING_PARENT_USER_CODE, StatusCode.OK,
									FIELD_MSG.concat(extendedObjectId.toString()), null);
					 }
				} else {
					// comparison for new field is disallowed.
					return bffResponse.response(null, BffResponseCode.COMPARE_DIS_ALLOWED_API_CODE,
							BffResponseCode.COMPARE_DIS_ALLOWED_USER_CODE, StatusCode.OK,
						    FIELD_MSG.concat(extendedObjectId.toString()),
							FIELD_MSG.concat(extendedObjectId.toString()));
				}

				leftMap = fetchObjectMap(objectMapper, type, parentFieldObj);
				rightMap = fetchObjectMap(objectMapper, type, extendedFieldObj);
				break;
			}

			bffCoreResponse = buildComponentVariance(extendedObjectId, objectMapper, leftMap, rightMap);

		} catch (DataAccessException exp) {
			LOGGER.error(BffAdminConstantsUtils.DB_EXP_MSG, extendedObjectId);
			LOGGER.error(BffAdminConstantsUtils.DB_EXP_MSG, extensionType);
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.DB_ERR_FLOW_API_FLOW_EXTENDED_FETCH,
							BffResponseCode.DB_ERR_FLOW_USER_FLOW_EXTENDED_FETCH),
					StatusCode.INTERNALSERVERERROR, extendedObjectId.toString(),
					BffAdminConstantsUtils.EMPTY_SPACES);

		} catch (Exception exp) {
			LOGGER.error(BffAdminConstantsUtils.APP_EXP_MSG, extendedObjectId);
			LOGGER.error(BffAdminConstantsUtils.DB_EXP_MSG, extensionType);
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_FLOW_API_FLOW_EXCEPTION_EXTENDED_FETCH,
							BffResponseCode.ERR_FLOW_USER_FLOW_EXCEPTION_EXTENDED_FETCH),
					StatusCode.BADREQUEST, extendedObjectId.toString(),
					BffAdminConstantsUtils.EMPTY_SPACES);
		}
		return bffCoreResponse;
	}

	/**Read Objects and prepare as Map for comparison
	 * 
	 * @param objectMapper
	 * @param type
	 * @param fieldObj
	 * @return Map<String, Object>
	 * @throws IOException
	 */
	private Map<String, Object> fetchObjectMap(ObjectMapper objectMapper, TypeReference<HashMap<String, Object>> type,
			Field fieldObj) throws IOException {
		FieldObjDto extendedFieldObjDto = formTransformation.createFieldObjDto(fieldObj, null);
		FieldComponent fieldComponent = fieldComponentConverter.createFieldComponent(extendedFieldObjDto);
		String extendedFieldStr = objectMapper.writeValueAsString(fieldComponent);
		return objectMapper.readValue(extendedFieldStr, type);
	}

	/**Prepare the left and right value map to show differences
	 * 
	 * @param extendedObjectId
	 * @param objectMapper
	 * @param leftMap
	 * @param rightMap
	 * @return BffCoreResponse
	 * @throws IOException
	 */
	private BffCoreResponse buildComponentVariance(UUID extendedObjectId, ObjectMapper objectMapper,
			Map<String, Object> leftMap, Map<String, Object> rightMap) throws IOException {
		List<ExtensionVarianceDto> diffList = null;
		diffList = fetchObjectDifference(objectMapper, leftMap, rightMap);
		return bffResponse.response(diffList, BffResponseCode.FLOW_SUCCESS_CODE_FLOW_EXTENDED_FETCH,
				BffResponseCode.FLOW_USER_CODE_FLOW_EXTENDED_FETCH, StatusCode.OK,
				extendedObjectId.toString(), BffAdminConstantsUtils.EMPTY_SPACES);
	}

	/**Build the left and right value map 
	 * 
	 * @param objectMapper
	 * @param leftMap
	 * @param rightMap
	 * @return List<ExtensionVarianceDto>
	 * @throws IOException
	 */
	private List<ExtensionVarianceDto> fetchObjectDifference(ObjectMapper objectMapper, Map<String, Object> leftMap,
			Map<String, Object> rightMap) throws IOException {
		List<ExtensionVarianceDto> diffList = new ArrayList<>();
		MapDifference<String, Object> difference = Maps.difference(leftMap, rightMap);
		if (!difference.entriesDiffering().keySet().isEmpty()) {

			for (Entry<String, ValueDifference<Object>> entry : difference.entriesDiffering().entrySet()) {
				if (entry.getValue() != null) {
					String left = objectMapper.writeValueAsString(entry.getValue().leftValue());
					String right = objectMapper.writeValueAsString(entry.getValue().rightValue());
					ExtensionVarianceDto diffDto = new ExtensionVarianceDto(entry.getKey(),
							(JsonNode) objectMapper.readTree(left), (JsonNode) objectMapper.readTree(right));
					diffList.add(diffDto);
				}
			}
		}
		if (!CollectionUtils.isEmpty(difference.entriesOnlyOnRight())) {

			for (Entry<String, Object> entry : difference.entriesOnlyOnRight().entrySet()) {
				if (entry.getValue() != null) {
					String left = objectMapper.writeValueAsString(BffAdminConstantsUtils.EMPTY_SPACES);
					String right = objectMapper.writeValueAsString(entry.getValue());
					ExtensionVarianceDto diffDto = new ExtensionVarianceDto(entry.getKey(),
							(JsonNode) objectMapper.readTree(left), (JsonNode) objectMapper.readTree(right));
					diffList.add(diffDto);
				}
			}
		}
		if (!CollectionUtils.isEmpty(difference.entriesOnlyOnLeft().keySet())) {

			for (Entry<String, Object> entry : difference.entriesOnlyOnLeft().entrySet()) {
				if (entry.getValue() != null) {
					String left = objectMapper.writeValueAsString(entry.getValue());
					String right = objectMapper.writeValueAsString(BffAdminConstantsUtils.EMPTY_SPACES);
					ExtensionVarianceDto diffDto = new ExtensionVarianceDto(entry.getKey(),
							(JsonNode) objectMapper.readTree(left), (JsonNode) objectMapper.readTree(right));
					diffList.add(diffDto);
				}
			}
		}
		return diffList;
	}
}