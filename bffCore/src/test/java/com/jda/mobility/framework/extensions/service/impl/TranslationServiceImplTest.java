
package com.jda.mobility.framework.extensions.service.impl;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jda.mobility.framework.extensions.dto.TranslationRequest;
import com.jda.mobility.framework.extensions.entity.ResourceBundle;
import com.jda.mobility.framework.extensions.exception.BffException;
import com.jda.mobility.framework.extensions.exception.DataBaseException;
import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.repository.ResourceBundleRepository;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils;
import com.jda.mobility.framework.extensions.utils.BffMessageConstantsUtils.StatusCode;
import com.jda.mobility.framework.extensions.utils.BffResponseCode;

/**
 * JUnit test class for TranslationServiceImpl
 * 
 * @author HCL Technologies
 */

@RunWith(SpringJUnit4ClassRunner.class)
public class TranslationServiceImplTest extends AbstractPrepareTest{
	@InjectMocks
	private TranslationServiceImpl translationServiceImpl;
	@Mock
	private ResourceBundleRepository resourceBundleRepo;	
	
	/**
	 * Test method for createResourceBundle
	 */
	@Test
	public void testCreateResourceBundle() {
		ResourceBundle rb = new ResourceBundle();		
		when(resourceBundleRepo.findByRbkeyAndLocale(Mockito.any(), Mockito.any())).thenReturn(rb);
		BffCoreResponse response1 = translationServiceImpl.createResourceBundle(getResourceBundleRequest());
		assertEquals(BffResponseCode.ERR_RESOURCE_BUNDLE_SERVICE_API_CREATE_CHECK.getCode(), response1.getCode());		
		when(resourceBundleRepo.findByRbkeyAndLocale(Mockito.any(), Mockito.any())).thenReturn(null);
		when(resourceBundleRepo.findByLocaleAndTypeOrderByRbkeyAscRbvalueAsc("Test", "Test")).thenReturn(getRbList());
		when(resourceBundleRepo.save(Mockito.any())).thenReturn(getResourceBundle());
		when(sessionDetails.getLocale()).thenReturn("en");
		BffCoreResponse response = translationServiceImpl.createResourceBundle(getResourceBundleRequest());
		assertEquals(BffResponseCode.RESOURCE_BUNDLE_SERVICE_SUCCESS_CODE_CREATE.getCode(), response.getCode());
		assertEquals(StatusCode.CREATED.getValue(), response.getHttpStatusCode());
	}
	
	@Test
	public void testCreateResourceBundleRbkeyEmpty() {
		BffCoreResponse response = translationServiceImpl.createResourceBundle(new TranslationRequest());
		assertEquals(BffResponseCode.ERR_RESOURCE_BUNDLE_SERVICE_API_CREATE_CHECK_RBKEY_EMPTY.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}
	
	/**
	 * Test method for createResourceBundle_DataAccessException
	 */
	@Test
	public void testCreateResourceBundleDataAccessException() {
		when(resourceBundleRepo.save(Mockito.any())).thenThrow(new DataBaseException("Resource bundle creation failed"));
		BffCoreResponse response = translationServiceImpl.createResourceBundle(getResourceBundleRequest());
		assertEquals(BffResponseCode.ERR_RESOURCE_BUNDLE_SERVICE_API_CREATE_DBEXCEPTION.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	/**
	 * Test method for createResourceBundle_Exception
	 */
	@Test
	public void testCreateResourceBundleException() {
		when(resourceBundleRepo.save(Mockito.any())).thenThrow(new BffException(BffAdminConstantsUtils.EXP_MSG));
		BffCoreResponse response = translationServiceImpl.createResourceBundle(getResourceBundleRequest());
		assertEquals(BffResponseCode.ERR_RESOURCE_BUNDLE_SERVICE_API_CREATE_EXCEPTION.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	/**
	 * Test method for getResourceBundles
	 */
	@Test
	public void testGetResourceBundles() {
		List<ResourceBundle> resourceBundleList = new ArrayList<>();
		ResourceBundle resourceBundle = new ResourceBundle();
		resourceBundle.setRbkey("5501");
		resourceBundleList.add(resourceBundle);
		when(resourceBundleRepo.findDistinctByTypeOrderByCreationDateDesc(Mockito.any())).thenReturn(resourceBundleList);
		when(resourceBundleRepo.findByLocaleAndTypeOrderByRbkeyAscRbvalueAsc(Mockito.any(), Mockito.any())).thenReturn(new ArrayList<>());
		translationServiceImpl.getResourceBundles( BffAdminConstantsUtils.AppCfgRequestType.INTERNAL.getType());
		when(sessionDetails.getLocale()).thenReturn("en");
		when(resourceBundleRepo.findByLocaleAndTypeOrderByRbkeyAscRbvalueAsc(Mockito.any(), Mockito.any())).thenReturn(getRbList());
		BffCoreResponse response = translationServiceImpl.getResourceBundles( BffAdminConstantsUtils.AppCfgRequestType.INTERNAL.getType());
		assertEquals(BffResponseCode.RESOURCE_BUNDLE_SERVICE_SUCCESS_CODE_FETCH.getCode(), response.getCode());	
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
	}	
	
	/**
	 * Test method for getResourceBundles_DataAccessException
	 */
	@Test
	public void testGetResourceBundlesDataAccessException() {
		when(resourceBundleRepo.findByLocaleAndTypeOrderByRbkeyAscRbvalueAsc(Mockito.any(), Mockito.any())).thenThrow(new DataBaseException("Resource bundle retrieval failed"));
		BffCoreResponse response = translationServiceImpl.getResourceBundles( BffAdminConstantsUtils.AppCfgRequestType.INTERNAL.getType());
		assertEquals(BffResponseCode.ERR_RESOURCE_BUNDLE_SERVICE_API_FETCH_DBEXCEPTION.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	/**
	 * Test method for getResourceBundles_Exception
	 */
	@Test
	public void testGetResourceBundlesException() {
		when(resourceBundleRepo.findByLocaleAndTypeOrderByRbkeyAscRbvalueAsc(Mockito.any(), Mockito.any())).thenThrow(new BffException(BffAdminConstantsUtils.EXP_MSG));
		BffCoreResponse response = translationServiceImpl.getResourceBundles(BffAdminConstantsUtils.AppCfgRequestType.INTERNAL.getType());
		assertEquals(BffResponseCode.ERR_RESOURCE_BUNDLE_SERVICE_API_FETCH_EXCEPTION.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}
	
	/**
	 *  Junit test method for updateLocale
	 */
	@Test
	public void testUpdateLocale() {
		BffCoreResponse response = translationServiceImpl.updateLocale("En");
		assertEquals(BffResponseCode.LOCALE_SUCCESS_CODE_UPDATE.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
	}
	
	@Test
	public void testUpdateLocaleException() {
		doThrow(RuntimeException.class).when(sessionDetails).setLocale(null);
		BffCoreResponse response = translationServiceImpl.updateLocale(null);
		assertEquals(BffResponseCode.LOCALE_ERR_UPDATE_LOCALE.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}
	
	@Test
	public void testGetlocalizedResBundleEntries() {
		List<ResourceBundle> resourceBundleList = new ArrayList<>();
		resourceBundleList.add(getResourceBundle());		
		when(resourceBundleRepo.findByLocaleAndTypeOrderByRbkeyAscRbvalueAsc(Mockito.any(),Mockito.any())).thenReturn(resourceBundleList);
		translationServiceImpl.getlocalizedResBundleEntries("INTERNAL");
		assertTrue(true);
	}
	
	/**
	 * @return TranslationRequest
	 */
	private TranslationRequest getResourceBundleRequest() {		
		return TranslationRequest.builder()
					.locale(BffAdminConstantsUtils.LOCALE)
					.rbkey("123")
					.rbvalue("tetst2")
					.type("INTERNAL")
					.uid(UUID.randomUUID())
					.build();

	}

	/**
	 * @return ResourceBundle
	 */
	private ResourceBundle getResourceBundle() {
		ResourceBundle translation = new ResourceBundle();
		translation.setUid(UUID.fromString("009b8f97-9107-bf45-b17e-0db4f2ddfc87"));
		translation.setLocale("TEST");
		translation.setRbkey("TEST");
		translation.setRbvalue("TEST");
		translation.setType("TEST");
		return translation;
	}

	/**
	 * @return List<ResourceBundle>
	 */
	private List<ResourceBundle> getRbList() {
		List<ResourceBundle> rbList = new ArrayList<>();
		ResourceBundle translation = new ResourceBundle();
		translation.setUid(UUID.fromString("009b8f97-9107-bf45-b17e-0db4f2ddfc87"));
		translation.setLocale("TEST");
		translation.setRbkey("TEST");
		translation.setRbvalue("TEST");
		translation.setType("TEST");
		rbList.add(translation);
		return rbList;
	}

}
