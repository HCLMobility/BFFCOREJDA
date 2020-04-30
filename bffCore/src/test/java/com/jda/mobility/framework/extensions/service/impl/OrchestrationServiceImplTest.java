package com.jda.mobility.framework.extensions.service.impl;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jda.mobility.framework.extensions.entity.ApiMaster;
import com.jda.mobility.framework.extensions.entity.ApiRegistry;
import com.jda.mobility.framework.extensions.entity.RoleMaster;
import com.jda.mobility.framework.extensions.model.ProdApiWrkMemRequest;
import com.jda.mobility.framework.extensions.repository.ApiMasterRepository;
@RunWith(SpringJUnit4ClassRunner.class)
public class OrchestrationServiceImplTest {

	@InjectMocks
	private OrchestrationServiceImpl orchestrationServiceImpl;
	@Mock
	private ApiMasterRepository apiMasterRepository;

	@Test
	public void testbuildOrchestrationPreProcessor() throws JsonMappingException, JsonProcessingException {		
		ObjectMapper mapper = new ObjectMapper();
		String json = createRequest();
		JsonNode node = mapper.readTree(json);
		String orchestrationName="registerDeviceUsingPOST";
		List<ApiMaster> apiMasterList = new ArrayList<ApiMaster>();
		ProdApiWrkMemRequest  prod= new ProdApiWrkMemRequest();
		prod.setLayer("JDA services");
		prod.setUserId("SUPER");
		ApiMaster apiMaster = new ApiMaster();
		RoleMaster roleMaster = new RoleMaster();
		roleMaster.setLevel(1);
		ApiRegistry apiRegistry = new ApiRegistry();
		apiRegistry.setUid(UUID.randomUUID());
		apiRegistry.setRoleMaster(roleMaster);
		apiMaster.setApiRegistry(apiRegistry);
		apiMaster.setUid(UUID.randomUUID());
		apiMaster.setRequestMethod("GET");
		apiMaster.setRuleContent(createRuleFileString().getBytes());
		apiMaster.setRequestEndpoint("\\work\\area");
		apiMasterList.add(apiMaster);
		when(apiMasterRepository.findByName(orchestrationName)).thenReturn(apiMasterList);
		ProdApiWrkMemRequest prodapi =orchestrationServiceImpl.buildOrchestrationPreProcessor(node, 0);
		Assert.assertNotEquals("Request Created Successfully", prodapi, prod);	
		Assert.assertEquals("JDA services",  prod.getLayer());	
	}
	
	private String createRuleFileString() {		
		StringBuilder ruleBuilder = new StringBuilder();		
		ruleBuilder.append("Include Libraries\r\n" + 
				"Step \"OrchExecution1\"\r\n" + 
				"	Priority 2\r\n" + 
				"	Orchestration \"ORCHDEMOEXECUTION\"\r\n" + 
				"	when \r\n" + 
				"		Get Inputs\r\n" + 
				"	then\r\n" + 
				"		Say \"***Orchestration  Execution  of registerDeviceUsingPOST api****\"\r\n" + 
				"		\r\n" + 
				"end");
		
		return ruleBuilder.toString();
	}
	
	@Test
	public void testGetRuleContent() {		
		String orchestrationName="registerDeviceUsingPOST";
		ApiMaster apiMaster = new ApiMaster();
		ApiRegistry apiRegistry = new ApiRegistry();
		apiRegistry.setUid(UUID.randomUUID());
		apiMaster.setApiRegistry(apiRegistry);
		apiMaster.setUid(UUID.randomUUID());
		apiMaster.setRequestMethod("GET");
		apiMaster.setOrchestrationName(orchestrationName);
		apiMaster.setRuleContent(createRuleFileString().getBytes());
		apiMaster.setRequestEndpoint("\\work\\area");
	
		when(apiMasterRepository.findByApiRegistryAndOrchestrationName(apiRegistry, orchestrationName)).thenReturn(Optional.of(apiMaster));	
	    byte source [] = orchestrationServiceImpl.getRuleContent(apiRegistry, orchestrationName);
	    Assert.assertTrue(source.length > 0);
	}
	    
	private String createRequest() {
		StringBuilder request = new StringBuilder();
		request.append("{\r\n" + 
				"    \"context\": \"\",\r\n" + 
				"    \"registry\": \"3b162504-d65b-4821-83b5-2abfcffa3f21\",\r\n" + 
				"    \"registryApi\": \"1bd49764-cec7-4e1c-8b1f-8d01af0a1821\",\r\n" + 
				"    \"selectedParamType\": \"REQUEST BODY\",\r\n" + 
				"    \"selectedParam\": \"RAW\",\r\n" + 
				"    \"selectedParamValue\": \"\",\r\n" + 
				"    \"selectedResponse\": \"\",\r\n" + 
				"    \"selectedResponseValue\": \"\",\r\n" + 
				"    \"rawValue\": \"\",\r\n" + 
				"    \"onSuccess\": {\r\n" + 
				"        \"actionType\": \"DISMISS\"\r\n" + 
				"    },\r\n" + 
				"    \"onFailure\": {\r\n" + 
				"        \"actionType\": \"DISMISS\"\r\n" + 
				"    },\r\n" + 
				"    \"regName\": \"DEMOORCHESTRATION\",\r\n" + 
				"    \"version\": \"1.0.0\",\r\n" + 
				"    \"basePath\": \"/api/orchestration/v1\",\r\n" + 
				"    \"contextPath\": \"10.151.45.124\",\r\n" + 
				"    \"requestEndpoint\": \"/invoke\",\r\n" + 
				"    \"requestMethod\": \"POST\",\r\n" + 
				"    \"listSchema\": \"\",\r\n" + 
				"    \"requestParam\": [\r\n" + 
				"        {\r\n" + 
				"            \"parameter\": {\r\n" + 
				"                \"propertyName\": \"registerDeviceUsingPOST~deviceCode\",\r\n" + 
				"                \"propertyType\": \"string\"\r\n" + 
				"            },\r\n" + 
				"            \"type\": \"PATH\",\r\n" + 
				"            \"value\": \"RDT002\"\r\n" + 
				"        },\r\n" + 
				"        {\r\n" + 
				"            \"parameter\": {\r\n" + 
				"                \"propertyName\": \"getCartonTypesUsingGET~warehouseId\",\r\n" + 
				"                \"propertyType\": \"string\"\r\n" + 
				"            },\r\n" + 
				"            \"type\": \"QUERY\",\r\n" + 
				"            \"value\": \"WMD2\"\r\n" + 
				"        },\r\n" + 
				"        {\r\n" + 
				"            \"parameter\": {\r\n" + 
				"                \"propertyName\": \"registerDeviceUsingPOST.deviceCode\",\r\n" + 
				"                \"propertyType\": \"string\",\r\n" + 
				"                \"propertyFormat\": \"\"\r\n" + 
				"            },\r\n" + 
				"            \"type\": \"REQUEST BODY\",\r\n" + 
				"            \"value\": \"RDT002\"\r\n" + 
				"        },\r\n" + 
				"        {\r\n" + 
				"            \"parameter\": {\r\n" + 
				"                \"propertyName\": \"registerDeviceUsingPOST.resourceId\",\r\n" + 
				"                \"propertyType\": \"string\",\r\n" + 
				"                \"propertyFormat\": \"\"\r\n" + 
				"            },\r\n" + 
				"            \"type\": \"REQUEST BODY\",\r\n" + 
				"            \"value\": \"null\"\r\n" + 
				"        },\r\n" + 
				"        {\r\n" + 
				"            \"parameter\": {\r\n" + 
				"                \"propertyName\": \"registerDeviceUsingPOST.warehouseId\",\r\n" + 
				"                \"propertyType\": \"string\",\r\n" + 
				"                \"propertyFormat\": \"\"\r\n" + 
				"            },\r\n" + 
				"            \"type\": \"REQUEST BODY\",\r\n" + 
				"            \"value\": \"WMD2\"\r\n" + 
				"        },\r\n" + 
				"        {\r\n" + 
				"            \"parameter\": {\r\n" + 
				"                \"propertyName\": \"WorkArea.absolutePriority\",\r\n" + 
				"                \"propertyType\": \"integer\",\r\n" + 
				"                \"propertyFormat\": \"int32\"\r\n" + 
				"            },\r\n" + 
				"            \"type\": \"REQUEST BODY\",\r\n" + 
				"            \"value\": \"4\"\r\n" + 
				"        },\r\n" + 
				"        {\r\n" + 
				"            \"parameter\": {\r\n" + 
				"                \"propertyName\": \"WorkArea.deltaPriority\",\r\n" + 
				"                \"propertyType\": \"integer\",\r\n" + 
				"                \"propertyFormat\": \"int32\"\r\n" + 
				"            },\r\n" + 
				"            \"type\": \"REQUEST BODY\",\r\n" + 
				"            \"value\": \"5\"\r\n" + 
				"        },\r\n" + 
				"        {\r\n" + 
				"            \"parameter\": {\r\n" + 
				"                \"propertyName\": \"WorkArea.distanceThreshold\",\r\n" + 
				"                \"propertyType\": \"number\",\r\n" + 
				"                \"propertyFormat\": \"double\"\r\n" + 
				"            },\r\n" + 
				"            \"type\": \"REQUEST BODY\",\r\n" + 
				"            \"value\": \"4\"\r\n" + 
				"        },\r\n" + 
				"        {\r\n" + 
				"            \"parameter\": {\r\n" + 
				"                \"propertyName\": \"WorkArea.homeWorkAreaAbsolutePriority\",\r\n" + 
				"                \"propertyType\": \"integer\",\r\n" + 
				"                \"propertyFormat\": \"int32\"\r\n" + 
				"            },\r\n" + 
				"            \"type\": \"REQUEST BODY\",\r\n" + 
				"            \"value\": \"5\"\r\n" + 
				"        },"+       
				"        {\r\n" + 
				"            \"parameter\": {\r\n" + 
				"                \"propertyName\": \"WorkArea.shortDescription\",\r\n" + 
				"                \"propertyType\": \"string\",\r\n" + 
				"                \"propertyFormat\": \"\"\r\n" + 
				"            },\r\n" + 
				"            \"type\": \"REQUEST BODY\",\r\n" + 
				"            \"value\": \"demoor\"\r\n" + 
				"        },\r\n" + 
				"        {\r\n" + 
				"            \"parameter\": {\r\n" + 
				"                \"propertyName\": \"WorkArea.voiceCode\",\r\n" + 
				"                \"propertyType\": \"integer\",\r\n" + 
				"                \"propertyFormat\": \"int32\"\r\n" + 
				"            },\r\n" + 
				"            \"type\": \"REQUEST BODY\",\r\n" + 
				"            \"value\": \"32\"\r\n" + 
				"        },\r\n" + 
				"        {\r\n" + 
				"            \"parameter\": {\r\n" + 
				"                \"propertyName\": \"WorkArea.warehouseId\",\r\n" + 
				"                \"propertyType\": \"string\",\r\n" + 
				"                \"propertyFormat\": \"\"\r\n" + 
				"            },\r\n" + 
				"            \"type\": \"REQUEST BODY\",\r\n" + 
				"            \"value\": \"WMD1\"\r\n" + 
				"        },\r\n" + 
				"        {\r\n" + 
				"            \"parameter\": {\r\n" + 
				"                \"propertyName\": \"WorkArea.workArea\",\r\n" + 
				"                \"propertyType\": \"string\",\r\n" + 
				"                \"propertyFormat\": \"\"\r\n" + 
				"            },\r\n" + 
				"            \"type\": \"REQUEST BODY\",\r\n" + 
				"            \"value\": \"ord16\"\r\n" + 
				"        }\r\n" + 
				"    ],\r\n" + 
				"    \"responseSchema\": []\r\n" + 
				"}");
		return request.toString();
	}
}
