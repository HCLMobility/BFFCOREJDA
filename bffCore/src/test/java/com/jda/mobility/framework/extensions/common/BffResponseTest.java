package com.jda.mobility.framework.extensions.common;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jda.mobility.framework.extensions.entity.ResourceBundle;
import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.repository.ResourceBundleRepository;
import com.jda.mobility.framework.extensions.utils.BffMessageConstantsUtils.StatusCode;
import com.jda.mobility.framework.extensions.utils.BffResponseCode;

@RunWith(SpringJUnit4ClassRunner.class)
public class BffResponseTest {

	@InjectMocks
	private BffResponse bffResponse;
	@Mock
	private ResourceBundleRepository resourceBundleRepo;
	@Mock
	private SessionDetails sessionDetails;

	@Test
	public void testResponse() {
		List<String> objList = new ArrayList<>();
		objList.add("PICK_PERMISSION");
		List<ResourceBundle> rbList = new ArrayList<>();
		ResourceBundle resourceBundle = new ResourceBundle();
		resourceBundle.setRbvalue("Flow creation success.");
		rbList.add(resourceBundle);
		when(resourceBundleRepo.findByLocaleAndRbkey(Mockito.any(), Mockito.any())).thenReturn(rbList);
		BffCoreResponse response = bffResponse.response(objList, BffResponseCode.FLOW_SUCCESS_CODE_CREATE_FLOW,
				BffResponseCode.FLOW_USER_CODE_CREATE_FLOW, StatusCode.CREATED);
		assertEquals(201, response.getHttpStatusCode());
		assertEquals(5003, response.getCode());
		assertEquals("Flow creation success.", response.getMessage());
	}

	@Test
	public void testResponseWithUserMessage() {
		List<String> objList = new ArrayList<>();
		objList.add("PICK_PERMISSION");
		List<ResourceBundle> rbList = new ArrayList<>();
		ResourceBundle resourceBundle = new ResourceBundle();
		resourceBundle.setRbvalue("Defaultflow and Homeflow retrival successful.");

		rbList.add(resourceBundle);
		when(resourceBundleRepo.findByLocaleAndRbkey(Mockito.any(), Mockito.any())).thenReturn(rbList);
		BffCoreResponse response = bffResponse.response(objList, BffResponseCode.PRODUCT_PREPARE_SUCCESS_CODE_DEFAULT_HOME_FLOW,
				BffResponseCode.PRODUCT_PREPARE_USER_CODE_DEFAULT_HOME_FLOW, StatusCode.OK, "Flow Created Successfully",
				null);
		assertEquals(200, response.getHttpStatusCode());
		assertEquals(5207,response.getCode());
		assertEquals("Defaultflow and Homeflow retrival successful.", response.getMessage());
	}

	@Test
	public void testErrResponse() {		
		List<ResourceBundle> rbList = new ArrayList<>();
		ResourceBundle resourceBundle = new ResourceBundle();
		resourceBundle.setRbvalue("Disable flow unsuccessful.");
		rbList.add(resourceBundle);
		when(resourceBundleRepo.findByLocaleAndRbkey(Mockito.any(), Mockito.any())).thenReturn(rbList);
		BffCoreResponse response = bffResponse.errResponse(List.of(BffResponseCode.ERR_FLOW_API_CHECK_CURRENT_FLOW_HOME, BffResponseCode.ERR_FLOW_USER_CHECK_CURRENT_FLOW_HOME),
				StatusCode.BADREQUEST);
		assertEquals(400,response.getHttpStatusCode());
		assertEquals(9027,response.getCode());
		assertEquals("Disable flow unsuccessful.", response.getMessage());
	}

	@Test
	public void testErrResponsewithUserMessage() {
		List<ResourceBundle> rbList = new ArrayList<>();
		ResourceBundle resourceBundle = new ResourceBundle();
		resourceBundle.setRbvalue("User validation is Unsuccessful.");
		rbList.add(resourceBundle);
		when(resourceBundleRepo.findByLocaleAndRbkey(Mockito.any(), Mockito.any())).thenReturn(rbList);
		BffCoreResponse response = bffResponse.errResponse(List.of(BffResponseCode.ERR_ACCESS_SERVICE_API_VALIDATE_USER, BffResponseCode.ERR_ACCESS_SERVICE_USER_VALIDATE_USER),
				StatusCode.INTERNALSERVERERROR, "Flow Creation Error", null);
		assertEquals(500, response.getHttpStatusCode());
		assertEquals(9259, response.getCode());
		assertEquals("User validation is Unsuccessful.", response.getMessage());
	}
	
	
	@Test
	public void testErrResponseIgnoreDelim() {		
		List<ResourceBundle> rbList = new ArrayList<>();
		ResourceBundle resourceBundle = new ResourceBundle();
		resourceBundle.setRbvalue("Disable flow unsuccessful.");
		rbList.add(resourceBundle);
		when(resourceBundleRepo.findByLocaleAndRbkey(Mockito.any(), Mockito.any())).thenReturn(rbList);
		BffCoreResponse response = bffResponse.errResponseIgnoreDelim(List.of(BffResponseCode.ERR_FLOW_API_CHECK_CURRENT_FLOW_HOME, BffResponseCode.ERR_FLOW_USER_CHECK_CURRENT_FLOW_HOME),
				StatusCode.BADREQUEST.getValue(), "Flow Creation Error", null);
		assertEquals(400,response.getHttpStatusCode());
		assertEquals(9027,response.getCode());
		assertEquals("Disable flow unsuccessful.", response.getMessage());
	}
	
	
	@Test
	public void testErrResponseTest() {		
		List<ResourceBundle> rbList = new ArrayList<>();
		ResourceBundle resourceBundle = new ResourceBundle();
		resourceBundle.setRbvalue("Disable flow unsuccessful.");
		rbList.add(resourceBundle);
		when(resourceBundleRepo.findByLocaleAndRbkey(Mockito.any(), Mockito.any())).thenReturn(rbList);
		BffCoreResponse response = bffResponse.errResponse(List.of(BffResponseCode.ERR_FLOW_API_CHECK_CURRENT_FLOW_HOME, BffResponseCode.ERR_FLOW_USER_CHECK_CURRENT_FLOW_HOME),
				StatusCode.BADREQUEST,"en-US","Flow Creation Error", null);
		assertEquals(400,response.getHttpStatusCode());
		assertEquals(9027,response.getCode());
		assertEquals("Disable flow unsuccessful.", response.getMessage());
	}
	
	@Test
	public void testErrResponseTest1() {		
		List<ResourceBundle> rbList = new ArrayList<>();
		ResourceBundle resourceBundle = new ResourceBundle();
		resourceBundle.setRbvalue("Disable flow unsuccessful.");
		rbList.add(resourceBundle);
		when(resourceBundleRepo.findByLocaleAndRbkey(Mockito.any(), Mockito.any())).thenReturn(rbList);
		BffCoreResponse response = bffResponse.errResponse(List.of(BffResponseCode.ERR_FLOW_API_CHECK_CURRENT_FLOW_HOME, BffResponseCode.ERR_FLOW_USER_CHECK_CURRENT_FLOW_HOME),
				StatusCode.BADREQUEST,"en-US");
		assertEquals(400,response.getHttpStatusCode());
		assertEquals(9027,response.getCode());
		assertEquals("Disable flow unsuccessful.", response.getMessage());
	}

}
