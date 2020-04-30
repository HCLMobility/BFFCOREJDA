/**
 * 
 */
package com.jda.mobility.framework.extensions.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.jda.mobility.framework.extensions.dto.RegistryDto;
import com.jda.mobility.framework.extensions.entity.ApiRegistry;
import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.service.impl.RegistryServiceImpl;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.ApiRegistryType;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.LayerMode;
import com.jda.mobility.framework.extensions.utils.BffMessageConstantsUtils.StatusCode;
import com.jda.mobility.framework.extensions.utils.BffResponseCode;
import com.jda.mobility.framework.extensions.utils.BffUtils;

/**
 * The class RegistryControllerTest.java
 * @author ChittipalliN
 * HCL Technologies Ltd.
 */

public class RegistryControllerTest extends AbstractBaseControllerTest{
	
	
	private static final String registryUrl = "/api/registry/v1";

	@Autowired
	private MockMvc mockMvc;

	@Mock
	private RegistryServiceImpl registryServiceImpl;
	
	
	@InjectMocks
	private RegistryController registryController;
	

	
	@Before
	public void beforeEach() {
		mockMvc = MockMvcBuilders.standaloneSetup(registryController).build();
	} 

	/**
	 * 
	 */
	@Test
	public void testFetchAllRegistries() throws Exception{
		StringBuilder url = new StringBuilder(registryUrl)
				.append("/list");
		List<RegistryDto> registryDtoList = new ArrayList<>();
		registryDtoList.add(getAllRegistry());
		when(registryServiceImpl.fetchAllRegistries(LayerMode.CURRENT_LAYER)).thenReturn(buildResponse(registryDtoList));
		mockMvc.perform( MockMvcRequestBuilders
				.get(url.toString())
				.param("mode", LayerMode.CURRENT_LAYER.toString())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.details.data[0].name").value("Item"));
	}

	
	@Test
	public void testfetchRegistries() throws Exception{
		StringBuilder url = new StringBuilder(registryUrl)
				.append("/list/")
				.append(ApiRegistryType.ORCHESTRATION);
		List<RegistryDto> registryDtoList = new ArrayList<>();
		registryDtoList.add(getAllRegistry());
		when(registryServiceImpl.fetchRegistries(Arrays.asList(ApiRegistryType.ORCHESTRATION),LayerMode.CURRENT_LAYER)).thenReturn(buildResponse(registryDtoList));
		mockMvc.perform( MockMvcRequestBuilders
				.get(url.toString())
				.param("mode", LayerMode.CURRENT_LAYER.toString())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.details.data[0].name").value("Item"));
	}

	/**
	 * Test method for {@link com.jda.mobility.framework.extensions.controller.RegistryController#fetchRegistryById(java.util.UUID)}.
	 */
	@Test
	public void testFetchRegistryById() throws Exception {
		UUID registryId = UUID.randomUUID();
		StringBuilder url = new StringBuilder(registryUrl)
				.append("/")
				.append(registryId);
				when(registryServiceImpl.fetchRegistryById(registryId)).thenReturn(buildResponse(Optional.of(getFindbyRegistryId())));
		mockMvc.perform(MockMvcRequestBuilders
				.get(url.toString())
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.details.data.name").value("WMS"));
	}
	
	@Test
	public void testfetchByRegistryIdOrApiList() throws Exception {
		StringBuilder url = new StringBuilder(registryUrl)
				.append("/master")
				.append("/list");
		List<RegistryDto> registryDtoList = new ArrayList<>();
		registryDtoList.add(getAllRegistry());
		when(registryServiceImpl.fetchAllApis()).thenReturn(buildResponse(registryDtoList));
		mockMvc.perform( MockMvcRequestBuilders
				.get(url.toString())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.details.data[0].name").value("Item"));
		
	}
	
	@Test
	public void testfetchByRegistryIdOrApiList_Else() throws Exception {
		UUID registryId = UUID.randomUUID();
		StringBuilder url = new StringBuilder(registryUrl)
				.append("/master")
				.append("/list");
		List<RegistryDto> registryDtoList = new ArrayList<>();
		registryDtoList.add(getAllRegistry());
		when(registryServiceImpl.fetchApiByRegistryId(registryId)).thenReturn(buildResponse(registryDtoList));
		mockMvc.perform( MockMvcRequestBuilders
				.get(url.toString())
				.param("registryId", registryId.toString())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.details.data[0].name").value("Item"));
	}

	@Test
	public void testfetchApiById() throws Exception{
		UUID id = UUID.randomUUID();
		StringBuilder url = new StringBuilder(registryUrl)
				.append("/master")
				.append("/")
				.append(id);
		when(registryServiceImpl.fetchApiById(id)).thenReturn(buildResponse(Optional.of(getAllRegistry())));
		mockMvc.perform( MockMvcRequestBuilders
				.get(url.toString())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.details.data.name").value("Item"));
	}
	private static <T> BffCoreResponse buildResponse(T data) {
		return BffUtils.buildResponse(data, BffResponseCode.REGISTRY_SERVICE_SUCCESS_CODE_FETCH_ALL.getCode(), "message",
				"detailMessage", StatusCode.OK.getValue());
	}
	
	private RegistryDto getAllRegistry() {

		RegistryDto apiRegistry = new RegistryDto.RegistryBuilder()
		.setRegId(UUID.randomUUID().toString())
		.setApiType("internal")
		.setApiVersion("2.0")
		.setBasePath("/ws/admin")
		.setContextPath("localhost")
		.setCreatedBy("SUPER")
		.setCreatedOn(null)
		.setHelperClass(null)
		.setModifiedBy(null)
		.setModifiedOn(null)
		.setName("Item")
		.setPort("4500")
		.setVersionId(0).build();
		return apiRegistry;

	}
	
	private ApiRegistry getFindbyRegistryId() {
		ApiRegistry apiRegistry = new ApiRegistry();
		apiRegistry.setUid(UUID.randomUUID());
		apiRegistry.setName("WMS");
		return apiRegistry;
	}
}
