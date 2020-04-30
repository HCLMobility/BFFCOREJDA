package com.jda.mobility.framework.extensions.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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

import com.jda.mobility.framework.extensions.common.SessionDetails;
import com.jda.mobility.framework.extensions.dto.TranslationRequest;
import com.jda.mobility.framework.extensions.entity.FlowPermission;
import com.jda.mobility.framework.extensions.entity.MenuPermission;
import com.jda.mobility.framework.extensions.entity.ProductMaster;
import com.jda.mobility.framework.extensions.entity.ProductProperty;
import com.jda.mobility.framework.extensions.entity.ResourceBundle;
import com.jda.mobility.framework.extensions.repository.ProductMasterRepository;
import com.jda.mobility.framework.extensions.repository.ProductPropertyRepository;
import com.jda.mobility.framework.extensions.repository.ResourceBundleRepository;

@RunWith(SpringJUnit4ClassRunner.class)
public class BffCommonUtilTest {

	@InjectMocks
	private BffCommonUtil bffCommonUtil;
	@Mock
	private ResourceBundleRepository resourceBundleRepo;
	@Mock
	private SessionDetails sessionDetails;
	@Mock
	ProductPropertyRepository productPropertyRepository;
	@Mock
	private ProductMasterRepository productMasterRepo;
	
	@Test
	public void getResourceBundleTest() {
		List<ResourceBundle> rbList = new ArrayList<>();
		ResourceBundle resourceBundle = new ResourceBundle();
		resourceBundle.setRbvalue("BUTTON");
		rbList.add(resourceBundle);
		when(resourceBundleRepo.findByLocaleAndRbkey(Mockito.any(), Mockito.any())).thenReturn(rbList);
		TranslationRequest translationRequest = bffCommonUtil.getResourceBundle("test");
		assertEquals("BUTTON", translationRequest.getRbvalue());
	}
	
	@Test
	public void getResourceBundleTest_else() {
		List<ResourceBundle> rbList = new ArrayList<>();
		ResourceBundle resourceBundle = new ResourceBundle();
		resourceBundle.setRbvalue("BUTTON");
		rbList.add(resourceBundle);
		TranslationRequest translationRequest = bffCommonUtil.getResourceBundle("BUTTON");
		assertEquals("BUTTON", translationRequest.getRbvalue());
	}
	
	@Test
	public void getCreatedOrGetSecondaryRefId() {
		String warehouseName="WMS";
		List<ProductProperty> rbList = new ArrayList<>();
		ProductProperty productProperty = new ProductProperty();
		productProperty.setSecondaryRef(true);
		productProperty.setUid(UUID.randomUUID());
		productProperty.setName("WMS");
		rbList.add(productProperty);
		when(productPropertyRepository.findByNameAndPropValueAndIsSecondaryRefTrue(Mockito.any(), Mockito.any())).thenReturn(rbList);
		ProductProperty prodProp = bffCommonUtil.createdOrGetSecondaryRefId(warehouseName);
		assertEquals(prodProp.getUid(), productProperty.getUid());
	}
	
	@Test
	public void getCreatedOrGetSecondaryRefId_false() {
		String warehouseName="WMS";
		List<ProductProperty> productProperties = new ArrayList<>();
		ProductProperty productProperty = new ProductProperty();
		productProperty.setSecondaryRef(true);
		productProperty.setUid(UUID.randomUUID());
		productProperty.setName("WMS");
		productProperty.setPropValue(warehouseName);
		productProperty.setPrimaryRef(false);
		productProperty.setAddlConfig("Test");
		
		ProductMaster productMaster = new ProductMaster();
		productMaster.setName("WMS");
		productMaster.setUid(UUID.randomUUID());
		productMaster.setConfigProperties(warehouseName);
		productMaster.setProductProperties(productProperties);
		productMaster.setProductDataSourceMaster(new ArrayList<>());
		productProperty.setProductMaster(productMaster);
		
		when(productMasterRepo.findByName(Mockito.any())).thenReturn(productMaster);
		when(productPropertyRepository.save(Mockito.any())).thenReturn(productProperty);
		ProductProperty prodProp = bffCommonUtil.createdOrGetSecondaryRefId(warehouseName);
		assertEquals(prodProp.getUid(), productProperty.getUid());
	}
	
	
	@Test
	public void TestCheckUserHasPermissionForFlow() {
		List<FlowPermission> flowPermissionList =new ArrayList<>();
		FlowPermission flowPermission= new FlowPermission();
		flowPermission.setPermission("true");
		flowPermissionList.add(flowPermission);
		List<String> userPermissions= new ArrayList<>();	
		String userPermission= "true";
		userPermissions.add(userPermission);
		boolean checkUserHasPermissionForFlow = bffCommonUtil.checkUserHasPermissionForFlow(flowPermissionList,userPermissions);
		assertTrue(checkUserHasPermissionForFlow);
	}
	
	@Test
	public void TestCheckMenuHasPermissionForMenu() {
		List<MenuPermission> menuPermissionList =new ArrayList<>();
		MenuPermission menuPermission= new MenuPermission();
		menuPermission.setPermission("true");
		menuPermissionList.add(menuPermission);
		List<String> userPermissions= new ArrayList<>();	
		String userPermission= "true";
		userPermissions.add(userPermission);
		boolean checkMenuHasPermissionForMenu = bffCommonUtil.checkMenuHasPermissionForMenu(menuPermissionList,userPermissions);
		assertTrue(checkMenuHasPermissionForMenu);
	}
}