package com.jda.mobility.framework.extensions.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.jda.mobility.framework.extensions.entity.Field;
import com.jda.mobility.framework.extensions.entity.Flow;
import com.jda.mobility.framework.extensions.entity.Form;
import com.jda.mobility.framework.extensions.exception.BffException;
import com.jda.mobility.framework.extensions.exception.DataBaseException;
import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.model.FieldComponent;
import com.jda.mobility.framework.extensions.repository.FieldRepository;
import com.jda.mobility.framework.extensions.repository.FlowRepository;
import com.jda.mobility.framework.extensions.repository.FormRepository;
import com.jda.mobility.framework.extensions.transformation.FieldComponentConverter;
import com.jda.mobility.framework.extensions.transformation.FormTransformation;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.ExtensionType;
import com.jda.mobility.framework.extensions.utils.BffMessageConstantsUtils.StatusCode;
import com.jda.mobility.framework.extensions.utils.BffCommonUtil;
import com.jda.mobility.framework.extensions.utils.BffResponseCode;

/**
 * The class ExtensionHistoryServiceImplTest.java
 * 
 * @author HCL Technologies Ltd.
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class ExtensionHistoryServiceImplTest extends AbstractPrepareTest {

	@InjectMocks
	private ExtensionHistoryServiceImpl exHistoryServiceImpl;

	@Mock
	private FlowRepository flowRepo;
	@Mock
	private FormRepository formRepo;
	@Mock
	private FieldRepository fieldRepo;
	@Spy
	private FormTransformation formTransformation = new FormTransformation();
	@Spy
	private FieldComponentConverter fieldComponentConverter = new FieldComponentConverter();

	@Test
	public void testFetchExtensionHistoryFlow() {
		String extendedObjectId = "9858fc5b-9881-45ff-bf5b-a43a5a20d308";
		String parentObjectId = "1d3114a7-e4b0-41b9-9357-02cc752b8a2d";
		when(flowRepo.findById(Mockito.any())).thenReturn(Optional.of(getExtendedFlow()));
		BffCoreResponse response = exHistoryServiceImpl.fetchExtensionHistory(UUID.fromString(extendedObjectId),
				UUID.fromString(parentObjectId), ExtensionType.FLOW);
		assertEquals(BffResponseCode.FLOW_SUCCESS_CODE_FLOW_EXTENDED_FETCH.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
		BffCoreResponse response1 = exHistoryServiceImpl.fetchExtensionHistory(UUID.fromString(extendedObjectId), null,
				ExtensionType.FLOW);
		assertEquals(BffResponseCode.FLOW_SUCCESS_CODE_FLOW_EXTENDED_FETCH.getCode(), response1.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testFetchExtensionHistoryForm() {
		String extendedObjectId = "9858fc5b-9881-45ff-bf5b-a43a5a20d308";
		String parentObjectId = "1d3114a7-e4b0-41b9-9357-02cc752b8a2d";
		when(formRepo.findById(Mockito.any())).thenReturn(Optional.of(getExtendedForm()));

		BffCoreResponse response = exHistoryServiceImpl.fetchExtensionHistory(UUID.fromString(extendedObjectId),
				UUID.fromString(parentObjectId), ExtensionType.FORM);
		assertEquals(BffResponseCode.FLOW_SUCCESS_CODE_FLOW_EXTENDED_FETCH.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
		BffCoreResponse response1 = exHistoryServiceImpl.fetchExtensionHistory(UUID.fromString(extendedObjectId), null,
				ExtensionType.FORM);
		assertEquals(BffResponseCode.FLOW_SUCCESS_CODE_FLOW_EXTENDED_FETCH.getCode(), response1.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testFetchExtensionHistoryFIELD() throws IOException {
		String extendedObjectId = "9858fc5b-9881-45ff-bf5b-a43a5a20d308";
		String parentObjectId = "1d3114a7-e4b0-41b9-9357-02cc752b8a2d";
		ExtensionType extensionType = ExtensionType.FIELD;
		FieldComponent fieldComponent = new FieldComponent();
		fieldComponent.setFieldId(UUID.randomUUID());
		Field parentField = new Field();
		parentField.setUid(UUID.randomUUID());
		ReflectionTestUtils.setField(fieldComponentConverter, "bffCommonUtil", mock(BffCommonUtil.class));
		when(fieldRepo.findById(UUID.fromString(parentObjectId))).thenReturn(Optional.of(parentField));
		when(fieldRepo.findById(UUID.fromString(extendedObjectId))).thenReturn(Optional.of(getExtendedField()));
		when(fieldRepo.findById(getExtendedField().getExtendedFromFieldId())).thenReturn(Optional.of(parentField));
		BffCoreResponse response = exHistoryServiceImpl.fetchExtensionHistory(UUID.fromString(extendedObjectId),
				UUID.fromString(parentObjectId), extensionType);
		assertEquals(BffResponseCode.FLOW_SUCCESS_CODE_FLOW_EXTENDED_FETCH.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
		when(fieldRepo.findById(UUID.fromString(parentObjectId))).thenReturn(Optional.of(new Field()));
		BffCoreResponse response1 = exHistoryServiceImpl.fetchExtensionHistory(UUID.fromString(extendedObjectId), null,
				extensionType);
		assertEquals(BffResponseCode.FLOW_SUCCESS_CODE_FLOW_EXTENDED_FETCH.getCode(), response1.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
		when(fieldRepo.findById(UUID.fromString(extendedObjectId))).thenReturn(Optional.of(new Field()));
		BffCoreResponse response2 = exHistoryServiceImpl.fetchExtensionHistory(UUID.fromString(extendedObjectId), null,
				extensionType);
		assertEquals(BffResponseCode.COMPARE_DIS_ALLOWED_API_CODE.getCode(), response2.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
	}
	
	@Test
	public void testFetchExtensionHistoryFIELD_Exception() throws IOException {
		String extendedObjectId = "9858fc5b-9881-45ff-bf5b-a43a5a20d308";
		ExtensionType extensionType = ExtensionType.FIELD;
		FieldComponent fieldComponent = new FieldComponent();
		fieldComponent.setFieldId(UUID.randomUUID());
		Field parentField = new Field();
		parentField.setUid(UUID.randomUUID());
		ReflectionTestUtils.setField(fieldComponentConverter, "bffCommonUtil", mock(BffCommonUtil.class));
		when(fieldRepo.findById(UUID.fromString(extendedObjectId))).thenReturn(Optional.of(getExtendedField()));
		BffCoreResponse response2 = exHistoryServiceImpl.fetchExtensionHistory(UUID.fromString(extendedObjectId), null,
				extensionType);
		assertEquals(BffResponseCode.COMPARE_MISSING_PARENT_API_CODE.getCode(), response2.getCode());
		assertEquals(StatusCode.OK.getValue(), response2.getHttpStatusCode());

	}

	@Test
	public void testFetchExtensionHistoryDataAccessException() {
		String extendedObjectId = "9858fc5b-9881-45ff-bf5b-a43a5a20d308";
		String parentObjectId = "1d3114a7-e4b0-41b9-9357-02cc752b8a2d";
		when(flowRepo.findById(UUID.fromString(parentObjectId))).thenReturn(Optional.of(new Flow()));
		when(flowRepo.findById(UUID.fromString(extendedObjectId)))
				.thenThrow(new DataBaseException("Flow retrieval failed"));
		BffCoreResponse response = exHistoryServiceImpl.fetchExtensionHistory(UUID.fromString(extendedObjectId),
				UUID.fromString(parentObjectId), ExtensionType.FLOW);
		assertEquals(BffResponseCode.DB_ERR_FLOW_API_FLOW_EXTENDED_FETCH.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());

	}

	@Test
	public void testFetchExtensionHistoryException() {
		String extendedObjectId = "9858fc5b-9881-45ff-bf5b-a43a5a20d308";
		String parentObjectId = "1d3114a7-e4b0-41b9-9357-02cc752b8a2d";
		when(flowRepo.findById(UUID.fromString(parentObjectId))).thenReturn(Optional.of(new Flow()));
		when(flowRepo.findById(UUID.fromString(extendedObjectId))).thenThrow(new BffException(BffAdminConstantsUtils.EXP_MSG));
		BffCoreResponse response = exHistoryServiceImpl.fetchExtensionHistory(UUID.fromString(extendedObjectId),
				UUID.fromString(parentObjectId), ExtensionType.FLOW);
		assertEquals(BffResponseCode.ERR_FLOW_API_FLOW_EXCEPTION_EXTENDED_FETCH.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	private Flow getExtendedFlow() {
		Flow flow = new Flow();
		flow.setExtendedFromFlowId(UUID.fromString("4da0d5a3-6946-4c0e-a0e3-c209c5056925"));
		return flow;
	}

	private Form getExtendedForm() {
		Form form = new Form();
		form.setExtendedFromFormId(UUID.fromString("3d5ab1f1-45e5-417e-9266-81d14068c6e0"));
		return form;
	}

	private Field getExtendedField() {
		Field field = new Field();
		field.setUid(UUID.randomUUID());
		field.setExtendedFromFieldId(UUID.fromString("1d3114a7-e4b0-41b9-9357-02cc752b8a2d"));
		return field;
	}

}
