package com.jda.mobility.framework.extensions.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.jda.mobility.framework.extensions.common.BffResponse;
import com.jda.mobility.framework.extensions.common.SessionDetails;
import com.jda.mobility.framework.extensions.dto.AppConfigDto;
import com.jda.mobility.framework.extensions.dto.AppSettingsDto;
import com.jda.mobility.framework.extensions.dto.DefaultHomeFlowDto;
import com.jda.mobility.framework.extensions.dto.FlowDto;
import com.jda.mobility.framework.extensions.entity.AppConfigDetail;
import com.jda.mobility.framework.extensions.entity.AppConfigMaster;
import com.jda.mobility.framework.extensions.entity.Flow;
import com.jda.mobility.framework.extensions.entity.Form;
import com.jda.mobility.framework.extensions.entity.ProductConfig;
import com.jda.mobility.framework.extensions.entity.ProductMaster;
import com.jda.mobility.framework.extensions.entity.ProductProperty;
import com.jda.mobility.framework.extensions.entity.RoleMaster;
import com.jda.mobility.framework.extensions.entity.UserRole;
import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.model.Layer;
import com.jda.mobility.framework.extensions.model.PrepRequest;
import com.jda.mobility.framework.extensions.repository.AppConfigMasterRepository;
import com.jda.mobility.framework.extensions.repository.FlowRepository;
import com.jda.mobility.framework.extensions.repository.FormRepository;
import com.jda.mobility.framework.extensions.repository.ProductConfigRepository;
import com.jda.mobility.framework.extensions.repository.ProductMasterRepository;
import com.jda.mobility.framework.extensions.repository.ProductPropertyRepository;
import com.jda.mobility.framework.extensions.repository.UserRoleRepository;
import com.jda.mobility.framework.extensions.service.AppConfigService;
import com.jda.mobility.framework.extensions.service.HotKeyCodeService;
import com.jda.mobility.framework.extensions.service.ProductPrepareService;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.AppCfgRequestType;
import com.jda.mobility.framework.extensions.utils.BffMessageConstantsUtils.StatusCode;
import com.jda.mobility.framework.extensions.utils.BffResponseCode;

/**
 *The class that does functionalities related to product configuration.
 * Ex: Fetch/Create secondary reference Id , Product Config Id , Mobile App Launch configuration etc.
 * 
 * @author HCL Technologies
 */
@Service
public class ProductPrepareServiceImpl implements ProductPrepareService {
	private static final Logger LOGGER = LogManager.getLogger(ProductPrepareServiceImpl.class);
	@Autowired
	private ProductPropertyRepository productPropertyRepo;
	@Autowired
	private ProductConfigRepository productConfigRepo;
	@Autowired
	private ProductMasterRepository productMasterRepo;
	@Autowired
	private FlowRepository flowRepo;
	@Autowired
	private AppConfigMasterRepository appConfigRepo;
	@Autowired
	private BffResponse bffResponse;
	@Autowired
	private SessionDetails sessionDetails;
	@Autowired
	private UserRoleRepository userRoleRepository;
	@Autowired
	private FormRepository formRepository;
	@Autowired
	private AppConfigService appConfigService;
	@Autowired
	private HotKeyCodeService hotKeyCodeService;


	/**
	 * Fetch default flow information for the application or product
	 * 
	 * @param configName
	 * @return BffCoreResponse
	 */
	@Override
	public BffCoreResponse fetchDefaultFlowConfig(String configName) {
		BffCoreResponse bffCoreResponse = null;
		try {
			List<AppConfigMaster> appconfig = appConfigRepo.findByConfigName(configName);
			List<AppConfigDto> appConfigDtoList = new ArrayList<>();
			for (AppConfigMaster appConfig : appconfig) {
				appConfigDtoList.add(convertToAppConfigDto(appConfig));
			}
			bffCoreResponse = bffResponse.response(appConfigDtoList,
					BffResponseCode.PRODUCT_PREPARE_SUCCESS_CODE_DEFAULT_FLOW,
					BffResponseCode.PRODUCT_PREPARE_USER_CODE_DEFAULT_FLOW, StatusCode.OK, null,
					configName);
		} catch (DataAccessException exp) {			
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.DB_EXP_MSG, configName);
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_PRODUCT_PREPARE_API_DEFAULT_FLOW_DBEXCEPTION,
							BffResponseCode.ERR_PRODUCT_PREPARE_USER_DEFAULT_FLOW_DBEXCEPTION),
					StatusCode.INTERNALSERVERERROR, null, configName);
		} catch (Exception exp) {			
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.APP_EXP_MSG, configName);
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_PRODUCT_PREPARE_API_DEFAULT_FLOW_EXCEPTION,
							BffResponseCode.ERR_PRODUCT_PREPARE_USER_DEFAULT_FLOW_EXCEPTION),
					StatusCode.BADREQUEST, null, configName);
		}
		return bffCoreResponse;
	}

	/**
	 * Retrieves flow list based on layering concept for given warehouse and default warehouse
	 * 
	 * @param prepRequest
	 * @return BffCoreResponse
	 */
	@Override
	public BffCoreResponse fetchDashboardFlows(PrepRequest prepRequest) {
		// current layer and above inner join
		BffCoreResponse bffCoreResponse = null;
		List<Flow> fetchedFlows;
		try {
			UserRole userRole = userRoleRepository.findByUserId(sessionDetails.getPrincipalName()).orElseThrow();
			int currentLayer = Integer.MIN_VALUE;
			if (userRole.getRoleMaster() != null) {
				currentLayer = userRole.getRoleMaster().getLevel();
				LOGGER.log(Level.DEBUG, "Current User Role Level : {}", currentLayer);
			}
			List<ProductConfig> prodConfigIdList = buildProdConfigs(prepRequest, currentLayer);
			fetchedFlows = new ArrayList<>();
			if (!prodConfigIdList.isEmpty()) {
				// Get the flows of warehouse supplied and default warehosue
				fetchedFlows.addAll(flowRepo.findByProductConfigInOrderByLastModifiedDateDesc(prodConfigIdList));
			}

			// Convert to model from entity
			List<FlowDto> flowDtoList = new ArrayList<>();
			for (Flow flow : fetchedFlows) {
				flowDtoList.add(convertToFlowDto(flow));
			}
			bffCoreResponse = bffResponse.response(flowDtoList,
					BffResponseCode.PRODUCT_PREPARE_SUCCESS_CODE_DASHBOARD_FLOWS,
					BffResponseCode.PRODUCT_PREPARE_USER_CODE_DASHBOARD_FLOWS, StatusCode.OK, null,
					prepRequest.getName());
		} catch (DataAccessException exp) {			
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.DB_EXP_MSG, prepRequest.getName());
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_PRODUCT_PREPARE_API_DASHBOARD_FLOWS_DBEXCEPTION,
							BffResponseCode.ERR_PRODUCT_PREPARE_USER_DASHBOARD_FLOWS_DBEXCEPTION),
					StatusCode.INTERNALSERVERERROR, null, prepRequest.getName());
		} catch (Exception exp) {			
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.APP_EXP_MSG, prepRequest.getName());
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_PRODUCT_PREPARE_API_DASHBOARD_FLOWS_EXCEPTION,
							BffResponseCode.ERR_PRODUCT_PREPARE_USER_DASHBOARD_FLOWS_EXCEPTION),
					StatusCode.BADREQUEST, null, prepRequest.getName());
		}
		return bffCoreResponse;
	}

	/**Build Product Config Ids based on layering concept
	 * 
	 * @param prepRequest
	 * @param layer
	 * @return
	 */
	private List<ProductConfig> buildProdConfigs(PrepRequest prepRequest, int layer) {
		// Get the product (Warehouse specific)
		ProductMaster productMaster = productMasterRepo.findByName(sessionDetails.getTenant());
		List<ProductProperty> productPropList = productPropertyRepo
				.findByNameAndPropValueAndProductMasterAndIsSecondaryRefTrue(prepRequest.getName(),
						prepRequest.getPropValue(), productMaster);

		// Get the default product
		List<ProductProperty> productPropDefList = productPropertyRepo
				.findByNameAndPropValueAndProductMasterAndIsSecondaryRefTrue(prepRequest.getName(),
						BffAdminConstantsUtils.WAREHOUSE_DEFAULT, productMaster);

		List<ProductConfig> prodConfigIdList = new ArrayList<>();
		LOGGER.log(Level.DEBUG, "Build prodConfigIdlist from ProdProperty");
		buildProductConfigList(layer, productPropList, prodConfigIdList);

		LOGGER.log(Level.DEBUG, "Build prodConfigIdlist from default ProdProperty");
		buildProductConfigList(layer, productPropDefList, prodConfigIdList);

		return prodConfigIdList;
	}

	private void buildProductConfigList(int layer, List<ProductProperty> productPropList,
			List<ProductConfig> prodConfigIdList) {
		if (productPropList != null) {
			for (ProductProperty prodProp : productPropList) {

				for (ProductConfig productConfig : productConfigRepo.findBySecondaryRefId(prodProp.getUid())) {
					LOGGER.log(Level.DEBUG, "ProductConfigId: {} and ProductConfigRoleLevel : {} and RoleName : {}",
							productConfig.getUid(), productConfig.getRoleMaster().getLevel(),
							productConfig.getRoleMaster().getName());
					if (productConfig.getRoleMaster().getLevel() <= layer) {
						prodConfigIdList.add(productConfig);
					}

				}
			}
		}
	}

	/**
	 * Create and Fetch product config Id forgiven warehouse and layer
	 */
	@Override
	public BffCoreResponse fetchProductConfigId(PrepRequest prepRequest) {
		String productConfigId = null;
		BffCoreResponse bffCoreResponse = null;
		try {
			productConfigId = fetchOrCreateProdConfigId(prepRequest);

			bffCoreResponse = bffResponse.response(productConfigId,
					BffResponseCode.PRODUCT_PREPARE_SUCCESS_CODE_FETCH_CONFIGID,
					BffResponseCode.PRODUCT_PREPARE_USER_CODE_FETCH_CONFIGID, StatusCode.OK, null,
					prepRequest.getPropValue());
		} catch (DataAccessException exp) {			
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.DB_EXP_MSG, prepRequest.getName());
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_PRODUCT_PREPARE_API_FETCH_CONFIGID_DBEXCEPTION,
							BffResponseCode.ERR_PRODUCT_PREPARE_USER_FETCH_CONFIGID_DBEXCEPTION),
					StatusCode.INTERNALSERVERERROR, null, prepRequest.getPropValue());
		} catch (Exception exp) {			
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.APP_EXP_MSG, prepRequest.getName());
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_PRODUCT_PREPARE_API_FETCH_CONFIGID_EXCEPTION,
							BffResponseCode.ERR_PRODUCT_PREPARE_USER_FETCH_CONFIGID_EXCEPTION),
					StatusCode.BADREQUEST, null,prepRequest.getPropValue());
		}
		return bffCoreResponse;
	}

	/**Method to retrieve list of product config id for current and layer above
	 * 
	 * @return List&lt;ProductConfig&gt;
	 */
	@Override
	public List<ProductConfig> getLayeredProductConfigList() {
		List<ProductConfig> prodConfigIdList = new ArrayList<>();
		int currentLayer = Integer.MIN_VALUE;

		UserRole userRole = userRoleRepository.findByUserId(sessionDetails.getPrincipalName()).orElseThrow();

		if (userRole.getRoleMaster() != null) {
			currentLayer = userRole.getRoleMaster().getLevel();
			LOGGER.log(Level.DEBUG, "Current UserRole Level : {}", currentLayer);
		}

		// Get the product (Warehouse specific)
		ProductMaster productMaster = productMasterRepo.findByName(sessionDetails.getTenant());

		// Get the default product
		List<ProductProperty> productPropDefList = productPropertyRepo
				.findByNameAndPropValueAndProductMasterAndIsSecondaryRefTrue(BffAdminConstantsUtils.PRODUCT_MASTER_CODE,
						BffAdminConstantsUtils.WAREHOUSE_DEFAULT, productMaster);

		if (productPropDefList != null)
			for (ProductProperty prodProp : productPropDefList) {

				for (ProductConfig productConfig : productConfigRepo.findBySecondaryRefId(prodProp.getUid())) {
					LOGGER.log(Level.DEBUG, "ProductConfigId: {} and RoleLevel : {}", productConfig.getUid(),
							productConfig.getRoleMaster().getLevel());
					if (productConfig.getRoleMaster().getLevel() <= currentLayer) {
						prodConfigIdList.add(productConfig);
					}
				}
			}
		return prodConfigIdList;
	}

	/**
	 * Method to return product config of current layer
	 * 
	 * @return ProductConfig
	 */
	public ProductConfig getCurrentLayerProdConfigId() {
		ProductConfig prodConfig = null;

		UserRole userRole = userRoleRepository.findByUserId(sessionDetails.getPrincipalName()).orElseThrow();
		// Get the product (Warehouse specific)
		ProductMaster productMaster = productMasterRepo.findByName(sessionDetails.getTenant());

		// Get the default product
		List<ProductProperty> productPropDefList = productPropertyRepo
				.findByNameAndPropValueAndProductMasterAndIsSecondaryRefTrue(BffAdminConstantsUtils.PRODUCT_MASTER_CODE,
						BffAdminConstantsUtils.WAREHOUSE_DEFAULT, productMaster);

		if (productPropDefList != null) {
			for (ProductProperty prodProp : productPropDefList) {

				for (ProductConfig productConfig : productConfigRepo.findBySecondaryRefId(prodProp.getUid())) {
					LOGGER.log(Level.DEBUG, "ProductConfigId: {} and ProductConfigRoleLevel : {}",
							productConfig.getUid(), productConfig.getRoleMaster().getLevel());
					if (productConfig.getRoleMaster().equals(userRole.getRoleMaster())) {
						prodConfig = productConfig;
						break;
					}
				}
			}
		}

		return prodConfig;
	}

	/**
	 * This method fetches for Default flow and Home flow for ADMIN UI
	 * 
	 * @return BffCoreResponse
	 */
	@Override
	public BffCoreResponse getDefaultHomeFlow() {
		BffCoreResponse bffCoreResponse = null;
		DefaultHomeFlowDto defaultHomeFlowDto = new DefaultHomeFlowDto();
		try {
			// Default flow
			boolean isDefaultFlowAvl = setDefaultFlow(defaultHomeFlowDto);

			// Home Flow
			boolean isHomeFlowAvl = setHomeFlow(defaultHomeFlowDto);

			if (!isDefaultFlowAvl && !isHomeFlowAvl) {
				return bffResponse.response(BffAdminConstantsUtils.NO_DEFAULT_OR_HOME_FLOW,
						BffResponseCode.PRODUCT_PREPARE_SUCCESS_CODE_DEFAULT_HOME_FLOW,
						BffResponseCode.PRODUCT_PREPARE_USER_CODE_DEFAULT_HOME_FLOW, StatusCode.OK);
			}

			bffCoreResponse = bffResponse.response(defaultHomeFlowDto,
					BffResponseCode.PRODUCT_PREPARE_SUCCESS_CODE_DEFAULT_HOME_FLOW,
					BffResponseCode.PRODUCT_PREPARE_USER_CODE_DEFAULT_HOME_FLOW, StatusCode.OK);
		} catch (DataAccessException exp) {			
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_PRODUCT_PREPARE_API_DEFAULT_HOME_FLOW_DBEXCEPTION,
							BffResponseCode.ERR_PRODUCT_PREPARE_USER_DEFAULT_HOME_FLOW_DBEXCEPTION),
					StatusCode.INTERNALSERVERERROR);
		} catch (Exception exp) {			
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(List.of(BffResponseCode.ERR_PRODUCT_PREPARE_API_DEFAULT_HOME_FLOW,
					BffResponseCode.ERR_PRODUCT_PREPARE_USER_DEFAULT_HOME_FLOW), StatusCode.BADREQUEST);
		}
		return bffCoreResponse;
	}

	/**Validate and set details for home flow
	 * 
	 * @param defaultHomeFlowDto
	 * @return boolean
	 */
	private boolean setHomeFlow(DefaultHomeFlowDto defaultHomeFlowDto) {

		AppConfigMaster homeFlowAppConfig = appConfigRepo.findByConfigNameAndConfigType(
				BffAdminConstantsUtils.HOME_FLOW_KEY, AppCfgRequestType.APPLICATION.getType());
		if (null != homeFlowAppConfig  && !CollectionUtils.isEmpty(homeFlowAppConfig.getAppConfigDetails())) {
			for (AppConfigDetail homeFlowAppConfigDetail : homeFlowAppConfig.getAppConfigDetails()) {
				if (null != homeFlowAppConfigDetail && null != homeFlowAppConfigDetail.getConfigValue()
						&& !homeFlowAppConfigDetail.getConfigValue().isEmpty()) {
					// Validate home flow
					Optional<Flow> homeFlow = flowRepo
							.findById(UUID.fromString(homeFlowAppConfigDetail.getConfigValue()));
					if (homeFlow.isPresent()) {
						Flow flow = homeFlow.get();
						defaultHomeFlowDto.setHomeFlowId(flow.getUid());
						defaultHomeFlowDto.setHomeFlowName(flow.getName());
						defaultHomeFlowDto.setHomeFlowVersion(flow.getVersion());
						defaultHomeFlowDto.setHomeFlowDefFormId(null!= flow.getDefaultFormId() ? flow.getDefaultFormId().toString(): null);
					}
				}
			}
			return true;
		} else {
			return false;
		}
	}

	/**Validate and set information of default flow
	 * 
	 * @param defaultHomeFlowDto
	 * @return boolean
	 */
	private boolean setDefaultFlow(DefaultHomeFlowDto defaultHomeFlowDto) {

		AppConfigMaster defFlowAppConfig = appConfigRepo.findByConfigNameAndConfigType(
				BffAdminConstantsUtils.DEFAULT_FLOW_KEY, AppCfgRequestType.APPLICATION.getType());

		if (null != defFlowAppConfig && !CollectionUtils.isEmpty(defFlowAppConfig.getAppConfigDetails())) {
			for (AppConfigDetail defFlowConfigDetail : defFlowAppConfig.getAppConfigDetails()) {
				if (null != defFlowConfigDetail && null!=defFlowConfigDetail.getConfigValue()
						&& !BffAdminConstantsUtils.EMPTY_SPACES.equals(defFlowConfigDetail.getConfigValue())) {
					Optional<Flow> defaultFlow = flowRepo
							.findById(UUID.fromString(defFlowConfigDetail.getConfigValue()));

					// Validate default flow
					if (defaultFlow.isPresent()) {
						Flow flow = defaultFlow.get();
						defaultHomeFlowDto.setDefaultFlowId(flow.getUid());
						defaultHomeFlowDto.setDefaultFlowName(flow.getName());
						defaultHomeFlowDto.setDefaultFlowVersion(flow.getVersion());
						defaultHomeFlowDto.setDefaultFlowdefFormId(null!= flow.getDefaultFormId() ? flow.getDefaultFormId().toString() : null);

					}
				}
			}
			return true;
		} else {
			return false;
		}
	}

	/**Validation of default flow for Mobile
	 * 
	 * @param defFlowAppConfig
	 * @param defaultFlow
	 * @return BffCoreResponse
	 */
	private BffCoreResponse validateDefaultFlow(AppConfigMaster defFlowAppConfig, Optional<Flow> defaultFlow) {

		for (AppConfigDetail homeFlowConfigDetail : defFlowAppConfig.getAppConfigDetails()) {
			if (defaultFlow.isPresent()) {
				if (defaultFlow.get().isDisabled()) {
					return bffResponse.errResponse(
							List.of(BffResponseCode.ERR_PRODUCT_PREPARE_API_DEFAULT_FLOW_DISABLED,
									BffResponseCode.ERR_PRODUCT_PREPARE_API_DEFAULT_FLOW_DISABLED),
							StatusCode.BADREQUEST, null, homeFlowConfigDetail.getConfigValue());
				}
				if (!defaultFlow.get().isPublishedFlow()) {
					return bffResponse.errResponse(
							List.of(BffResponseCode.ERR_PRODUCT_PREPARE_API_DEFAULT_FLOW_NOT_PUBLISHED,
									BffResponseCode.ERR_PRODUCT_PREPARE_API_DEFAULT_FLOW_NOT_PUBLISHED),
							StatusCode.BADREQUEST, null,homeFlowConfigDetail.getConfigValue());
				}
				if (defaultFlow.get().getPublishedDefaultFormId() == null
						&& defaultFlow.get().getDefaultFormId() == null) {
					return bffResponse.errResponse(
							List.of(BffResponseCode.ERR_PRODUCT_PREPARE_API_DEFAULT_FLOW_DEFAULT_FORM_NOT_FOUND,
									BffResponseCode.ERR_PRODUCT_PREPARE_API_DEFAULT_FLOW_DEFAULT_FORM_NOT_FOUND),
							StatusCode.BADREQUEST);
				}
				if (defaultFlow.get().getPublishedDefaultFormId() == null
						&& defaultFlow.get().getDefaultFormId() != null) {
					return bffResponse.errResponse(
							List.of(BffResponseCode.ERR_PRODUCT_PREPARE_API_DEFAULT_FLOW_DEFAULT_FORM_NOT_PUBLISHED,
									BffResponseCode.ERR_PRODUCT_PREPARE_API_DEFAULT_FLOW_DEFAULT_FORM_NOT_PUBLISHED),
							StatusCode.BADREQUEST);
				}
			}
		}
		return null;
	}

	/**Validation of home flow of Mobile
	 * 
	 * @param homeFlowAppConfig
	 * @param homeFlow
	 * @return BffCoreResponse
	 */
	private BffCoreResponse validateHomeFlow(AppConfigMaster homeFlowAppConfig, Optional<Flow> homeFlow) {

		for (AppConfigDetail homeFlowConfigDetail : homeFlowAppConfig.getAppConfigDetails()) {
			if (homeFlow.isPresent()) {
				if (homeFlow.get().isDisabled()) {
					return bffResponse.errResponse(
							List.of(BffResponseCode.ERR_PRODUCT_PREPARE_API_HOME_FLOW_DISABLED,
									BffResponseCode.ERR_PRODUCT_PREPARE_API_HOME_FLOW_DISABLED),
							StatusCode.BADREQUEST, null, homeFlowConfigDetail.getConfigValue());
				}
				if (!homeFlow.get().isPublishedFlow()) {
					return bffResponse.errResponse(
							List.of(BffResponseCode.ERR_PRODUCT_PREPARE_API_HOME_FLOW_NOT_PUBLISHED,
									BffResponseCode.ERR_PRODUCT_PREPARE_API_HOME_FLOW_NOT_PUBLISHED),
							StatusCode.BADREQUEST, null, homeFlowConfigDetail.getConfigValue());
				}
				if (homeFlow.get().getPublishedDefaultFormId() == null && homeFlow.get().getDefaultFormId() == null) {
					return bffResponse.errResponse(
							List.of(BffResponseCode.ERR_PRODUCT_PREPARE_API_HOME_FLOW_DEFAULT_FORM_NOT_FOUND,
									BffResponseCode.ERR_PRODUCT_PREPARE_API_HOME_FLOW_DEFAULT_FORM_NOT_FOUND),
							StatusCode.BADREQUEST);
				}
				if (homeFlow.get().getPublishedDefaultFormId() == null && homeFlow.get().getDefaultFormId() != null) {
					return bffResponse.errResponse(
							List.of(BffResponseCode.ERR_PRODUCT_PREPARE_API_HOME_FLOW_DEFAULT_FORM_NOT_PUBLISHED,
									BffResponseCode.ERR_PRODUCT_PREPARE_API_HOME_FLOW_DEFAULT_FORM_NOT_PUBLISHED),
							StatusCode.BADREQUEST, null,
							homeFlow.get().getDefaultFormId().toString());
				}
			}
		}
		return null;
	}

	/**Validate default form of home flow for mobile
	 * 
	 * @param homeFlowAppConfig
	 * @param defaultFormOfHomeFlow
	 * @return BffCoreResponse
	 */
	private BffCoreResponse validateDefFormOfHomeFlow(AppConfigMaster homeFlowAppConfig,
			Optional<Form> defaultFormOfHomeFlow) {
		for (AppConfigDetail homeFlowConfigDetail : homeFlowAppConfig.getAppConfigDetails()) {
			if (!defaultFormOfHomeFlow.isPresent()) {
				return bffResponse.errResponse(
						List.of(BffResponseCode.ERR_PRODUCT_PREPARE_API_DEFAULT_FLOW_HOME_FORM_DELETED,
								BffResponseCode.ERR_PRODUCT_PREPARE_API_DEFAULT_FLOW_HOME_FORM_DELETED),
						StatusCode.BADREQUEST, null, homeFlowConfigDetail.getConfigValue());
			}
			if (defaultFormOfHomeFlow.get().isDisabled()) {
				return bffResponse.errResponse(
						List.of(BffResponseCode.ERR_PRODUCT_PREPARE_API_HOME_FLOW_DEFAULT_FORM_DISABLED,
								BffResponseCode.ERR_PRODUCT_PREPARE_API_HOME_FLOW_DEFAULT_FORM_DISABLED),
						StatusCode.BADREQUEST, null, homeFlowConfigDetail.getConfigValue());
			}
			if (null == defaultFormOfHomeFlow.get().getPublishedForm()) {
				return bffResponse.errResponse(
						List.of(BffResponseCode.ERR_PRODUCT_PREPARE_API_HOME_FLOW_DEFAULT_FORM_NOT_PUBLISHED,
								BffResponseCode.ERR_PRODUCT_PREPARE_API_HOME_FLOW_DEFAULT_FORM_NOT_PUBLISHED),
						StatusCode.BADREQUEST, null, homeFlowConfigDetail.getConfigValue());
			}
		}

		return null;

	}

	/**Validate default form of default flow for mobile
	 * 
	 * @param defFlowAppConfig
	 * @param defaultFormOfDefaultFlow
	 * @return BffCoreResponse
	 */
	private BffCoreResponse validateDefFormOfDefFlow(AppConfigMaster defFlowAppConfig,
			Optional<Form> defaultFormOfDefaultFlow) {
		for (AppConfigDetail defFlowConfigDetail : defFlowAppConfig.getAppConfigDetails()) {
			if (!defaultFormOfDefaultFlow.isPresent()) {
				return bffResponse.errResponse(
						List.of(BffResponseCode.ERR_PRODUCT_PREPARE_API_DEFAULT_FLOW_DEFAULT_FORM_DELETED,
								BffResponseCode.ERR_PRODUCT_PREPARE_API_DEFAULT_FLOW_DEFAULT_FORM_DELETED),
						StatusCode.BADREQUEST, null,defFlowConfigDetail.getConfigValue());
			}

			if (defaultFormOfDefaultFlow.get().isDisabled()) {
				return bffResponse.errResponse(
						List.of(BffResponseCode.ERR_PRODUCT_PREPARE_API_DEFAULT_FLOW_DEFAULT_FORM_DISABLED,
								BffResponseCode.ERR_PRODUCT_PREPARE_API_DEFAULT_FLOW_DEFAULT_FORM_DISABLED),
						StatusCode.BADREQUEST, null,defFlowConfigDetail.getConfigValue());
			}
			if (null == defaultFormOfDefaultFlow.get().getPublishedForm()) {
				return bffResponse.errResponse(
						List.of(BffResponseCode.ERR_PRODUCT_PREPARE_API_DEFAULT_FLOW_DEFAULT_FORM_NOT_PUBLISHED,
								BffResponseCode.ERR_PRODUCT_PREPARE_API_DEFAULT_FLOW_DEFAULT_FORM_NOT_PUBLISHED),
						StatusCode.BADREQUEST, null, defFlowConfigDetail.getConfigValue());
			}
		}
		return null;
	}

	/**Create product config Id and fetch the product configId for given warehouse and layer
	 * 
	 * @param prepRequest
	 * @return String
	 */
	private String fetchOrCreateProdConfigId(PrepRequest prepRequest) {
		// Get Product name
		ProductMaster productMaster = productMasterRepo.findByName(sessionDetails.getTenant());

		// If Default warehouse , then set WMS and __DEFAULT
		if (prepRequest.getIsDefaultWarehouse()) {
			prepRequest.setPropValue(BffAdminConstantsUtils.WAREHOUSE_DEFAULT);
			prepRequest.setName(BffAdminConstantsUtils.PRODUCT_MASTER_CODE);
		}

		// Get Role from session
		RoleMaster roleMaster = null;
		String userId = sessionDetails.getPrincipalName();
		UserRole userRole = userRoleRepository.findByUserId(userId).orElseThrow();
		if (userRole.getRoleMaster() != null) {
			roleMaster = userRole.getRoleMaster();
		}
		String prodConfigId = null;

		// Get the product property (Secondary Ref id)
		List<ProductProperty> prodPropertyList = productPropertyRepo
				.findByNameAndPropValueAndProductMasterAndIsSecondaryRefTrue(prepRequest.getName(),
						prepRequest.getPropValue(), productMaster);
		// If not found ,create product config and secondary ref id
		if (prodPropertyList == null || prodPropertyList.isEmpty()) {
			ProductProperty proProp = new ProductProperty();
			proProp.setName(prepRequest.getName());
			proProp.setPropValue(prepRequest.getPropValue());
			proProp.setProductMaster(productMaster);
			proProp.setPrimaryRef(false);
			proProp.setSecondaryRef(true);
			proProp = productPropertyRepo.save(proProp);

			ProductConfig productConfig = new ProductConfig();
			productConfig.setSecondaryRefId(proProp.getUid());
			productConfig.setRoleMaster(roleMaster);
			productConfig.setVersionId(prepRequest.getVersion());
			productConfig = productConfigRepo.save(productConfig);
			prodConfigId = productConfig.getUid().toString();
		} else {
			// Get Product config Id by role
			ProductConfig rtrvProductConfId = productConfigRepo
					.findBySecondaryRefIdAndRoleMaster(prodPropertyList.get(0).getUid(), roleMaster);

			// If found, return
			if (rtrvProductConfId != null && rtrvProductConfId.getUid() != null) {
				prodConfigId = rtrvProductConfId.getUid().toString();
			}
			// if not found, create product config id
			else {
				ProductConfig productConfig = new ProductConfig();
				productConfig.setSecondaryRefId(prodPropertyList.get(0).getUid());
				productConfig.setRoleMaster(roleMaster);
				productConfig.setVersionId(prepRequest.getVersion());
				productConfig = productConfigRepo.save(productConfig);
				prodConfigId = productConfig.getUid().toString();
			}
		}
		return prodConfigId;

	}

	/**
	 * @param flow
	 * @param productConfig
	 * @return FlowDto
	 */
	private FlowDto convertToFlowDto(Flow flow) {
		Layer layer = null;
		if (flow.getProductConfig() != null && flow.getProductConfig().getRoleMaster() != null) {
			layer = new Layer();
			layer.setLevel(flow.getProductConfig().getRoleMaster().getLevel());
			layer.setName(flow.getProductConfig().getRoleMaster().getName());
		}

		return new FlowDto.FlowBuilder(flow.getName()).setFlowId(flow.getUid()).setDescription(flow.getDescription())
				.setDefaultFormId(flow.getDefaultFormId()).setDisabled(flow.isDisabled())
				.setExtDisabled(flow.isExtDisabled()).setPublished(flow.isPublished()).setTag(flow.getTag())
				.setLayer(layer).setVersion(flow.getVersion()).setExtendedFromFlowId(flow.getExtendedFromFlowId())
				.build();
	}

	private AppConfigDto convertToAppConfigDto(AppConfigMaster appConfig) {
		AppConfigDetail appConfigDetail = new AppConfigDetail();
		return AppConfigDto.builder().appConfigId(appConfig.getUid()).configName(appConfig.getConfigName())
				.configType(appConfig.getConfigType()).rawValue(appConfig.getRawValue())
				.configValue(appConfigDetail.getConfigValue()).flowId(appConfigDetail.getFlowId())
				.userId(appConfigDetail.getUserId()).description(appConfigDetail.getDescription()).build();

	}

	/**
	 *Method to fetch configuration required for launching mobile application
	 * - Get default flow , home flow , hot keys , context , global and application variables
	 * - For session recovery - Return the context and global variable of selected device "recoveryDeviceId", 
	 * - Otherwise -  Return the context and global variable of current device (Logged in)
	 * 
	 * @return BffCoreResponse
	 */
	@Override
	public BffCoreResponse getAppSettings(String recoveredDeviceId) {
		BffCoreResponse bffCoreResponse = null;
		AppSettingsDto appSettings = new AppSettingsDto();

		// Retrieving Default and Home Flow
		try {
			List<AppConfigMaster> appConfigList = appConfigRepo.findByConfigNameInAndConfigType(
					Arrays.asList(BffAdminConstantsUtils.HOME_FLOW_KEY, BffAdminConstantsUtils.DEFAULT_FLOW_KEY),
					AppCfgRequestType.APPLICATION.getType());

			AppConfigMaster homeFlowAppConfig = null;
			AppConfigMaster defaultFlowAppConfig = null;

			if (appConfigList != null && !appConfigList.isEmpty()) {
				for (AppConfigMaster appConfig : appConfigList) {
					if (appConfig.getConfigName().equals(BffAdminConstantsUtils.HOME_FLOW_KEY)) {
						homeFlowAppConfig = appConfig;
					}
					if (appConfig.getConfigName().equals(BffAdminConstantsUtils.DEFAULT_FLOW_KEY)) {
						defaultFlowAppConfig = appConfig;
					}
				}
			} else {
				return bffResponse.response(BffAdminConstantsUtils.NO_DEFAULT_OR_HOME_FLOW,
						BffResponseCode.PRODUCT_PREPARE_SUCCESS_CODE_DEFAULT_HOME_FLOW,
						BffResponseCode.PRODUCT_PREPARE_USER_CODE_DEFAULT_HOME_FLOW, StatusCode.OK);

			}

			if (null != homeFlowAppConfig && null != homeFlowAppConfig.getAppConfigDetails()) {
				// Validating mandatory Home Flow
				for (AppConfigDetail homeFlowConfigDetail : homeFlowAppConfig.getAppConfigDetails()) {
					if (homeFlowConfigDetail.getConfigValue() == null
							|| homeFlowConfigDetail.getConfigValue().equals(BffAdminConstantsUtils.EMPTY_SPACES)) {
						return bffResponse.errResponse(
								List.of(BffResponseCode.ERR_PRODUCT_PREPARE_API_HOME_FLOW_NOT_FOUND,
										BffResponseCode.ERR_PRODUCT_PREPARE_API_HOME_FLOW_NOT_FOUND),
								StatusCode.BADREQUEST);
					}
				}
			}

			DefaultHomeFlowDto defaultHomeFlowDto = new DefaultHomeFlowDto();

			// Validate and Prepare the default flow data
			bffCoreResponse = validateAndPrepareDefaultflow(defaultHomeFlowDto, defaultFlowAppConfig);
			if (null != bffCoreResponse) {
				return bffCoreResponse;
			}

			// Validate and Prepare the Home flow data
			bffCoreResponse = validateAndPrepareHomeFlow(defaultHomeFlowDto, homeFlowAppConfig);
			if (null != bffCoreResponse) {
				return bffCoreResponse;
			}

			appSettings.setDefaultHomeFlow(defaultHomeFlowDto);
			
			//Get Hot keys 
			appSettings.setHotKeyCodeDtoList(hotKeyCodeService.getHotKeyCodeDtoList());
			

		} catch (DataAccessException exp) {			
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			return bffResponse.errResponse(
					List.of(BffResponseCode.ERR_PRODUCT_PREPARE_API_DEFAULT_HOME_FLOW_DBEXCEPTION,
							BffResponseCode.ERR_PRODUCT_PREPARE_USER_DEFAULT_HOME_FLOW_DBEXCEPTION),
					StatusCode.INTERNALSERVERERROR);
		} catch (Exception exp) {			
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			return bffResponse.errResponse(List.of(BffResponseCode.ERR_PRODUCT_PREPARE_API_DEFAULT_HOME_FLOW,
					BffResponseCode.ERR_PRODUCT_PREPARE_USER_DEFAULT_HOME_FLOW), StatusCode.BADREQUEST);
		}
	
		
		//Get Context , Global Variables , Application
		bffCoreResponse = retrieveContextAndGlobalVariables(recoveredDeviceId, appSettings);
		if (null != bffCoreResponse) {
			return bffCoreResponse;
		}

		bffCoreResponse = bffResponse.response(appSettings,
				BffResponseCode.PRODUCT_PREPARE_SUCCESS_CODE_DEFAULT_HOME_FLOW,
				BffResponseCode.PRODUCT_PREPARE_USER_CODE_DEFAULT_HOME_FLOW, StatusCode.OK);

		return bffCoreResponse;

	}

	/**Retrieves context, global and application variable for given user and device id
	 * 
	 * @param recoveredDeviceId
	 * @param appSettings
	 * @return BffCoreResponse
	 */
	private BffCoreResponse retrieveContextAndGlobalVariables(String recoveredDeviceId, AppSettingsDto appSettings) {
		// Retrieving CONTEXT, GLOBAL and APPLICATION variables
		try {
			boolean recovery = false;
			String deviceName = sessionDetails.getDeviceName();
			if(null!=recoveredDeviceId && !recoveredDeviceId.isEmpty())
			{
				recovery = true;
				deviceName = recoveredDeviceId;
			}
			List<AppConfigDto> appConfigDtoList = appConfigService.getAppSettingsList(deviceName);
			appSettings.setAppConfigs(appConfigDtoList);
			
			//Delete/Clear the session recovered Context and Global variables
			if(recovery)
			{
				appConfigService.clearAppConfig(sessionDetails.getPrincipalName(), deviceName);
			}
		} catch (DataAccessException exp) {
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			return bffResponse.errResponse(
					List.of(BffResponseCode.ERR_APP_CONFIG_SERVICE_API_FETCHALL_DBEXCEPTION,
							BffResponseCode.ERR_APP_CONFIG_SERVICE_USER_FETCHALL_DBEXCEPTION),
					StatusCode.INTERNALSERVERERROR);

		} catch (Exception exp) {			
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);

			return bffResponse.errResponse(List.of(BffResponseCode.ERR_APP_CONFIG_SERVICE_API_FETCHALL_EXCEPTION,
					BffResponseCode.ERR_APP_CONFIG_SERVICE_USER_FETCHALL_EXCEPTION), StatusCode.BADREQUEST);
		}

		return  null;


	}

	/**Validate and set information of Home flow - Mobile
	 * @param defaultHomeFlowDto
	 * @param homeFlowAppConfig
	 * @return BffCoreResponse
	 */
	private BffCoreResponse validateAndPrepareHomeFlow(DefaultHomeFlowDto defaultHomeFlowDto,
			AppConfigMaster homeFlowAppConfig) {
		BffCoreResponse bffCoreResponse = null;
		for (AppConfigDetail homeFlowConfigDetail : homeFlowAppConfig.getAppConfigDetails()) {
			if (!ObjectUtils.isEmpty(homeFlowConfigDetail.getConfigValue())) {
				// Validate home flow
				Optional<Flow> homeFlow = flowRepo.findById(UUID.fromString(homeFlowConfigDetail.getConfigValue()));
				if (!homeFlow.isPresent()) {
					return bffResponse.errResponse(
							List.of(BffResponseCode.ERR_PRODUCT_PREPARE_API_HOME_FLOW_NOT_FOUND,
									BffResponseCode.ERR_PRODUCT_PREPARE_API_HOME_FLOW_NOT_FOUND),
							StatusCode.BADREQUEST, null, homeFlowConfigDetail.getConfigValue());
				}

				bffCoreResponse = validateHomeFlow(homeFlowAppConfig, homeFlow);
				if (bffCoreResponse != null) {
					return bffCoreResponse;
				}
				defaultHomeFlowDto.setHomeFlowId(homeFlow.get().getUid());
				defaultHomeFlowDto.setHomeFlowName(homeFlow.get().getName());
				defaultHomeFlowDto.setHomeFlowVersion(homeFlow.get().getVersion());

				// Validate home flow - default form id
				if (homeFlow.get().getPublishedDefaultFormId() != null) {
					Optional<Form> defaultFormOfHomeFlow = formRepository
							.findById(homeFlow.get().getPublishedDefaultFormId());

					bffCoreResponse = validateDefFormOfHomeFlow(homeFlowAppConfig, defaultFormOfHomeFlow);
					if (bffCoreResponse != null) {
						return bffCoreResponse;
					}

					defaultHomeFlowDto.setHomeFlowDefFormId(homeFlow.get().getPublishedDefaultFormId().toString());
					defaultHomeFlowDto.setHomeFormTabbed(homeFlow.get().isDefaultFormTabbed());
					defaultHomeFlowDto.setDefaultFormModalForm(homeFlow.get().isDefaultModalForm());
				}
				else
				{
					return bffResponse.errResponse(
							List.of(BffResponseCode.ERR_PRODUCT_PREPARE_API_HOME_FLOW_DEFAULT_FORM_NOT_FOUND,
									BffResponseCode.ERR_PRODUCT_PREPARE_API_HOME_FLOW_DEFAULT_FORM_NOT_FOUND),
							StatusCode.BADREQUEST, null, homeFlowConfigDetail.getConfigValue());
				}
			}
		}
		return bffCoreResponse;
	}

	/**Validate and set information of default flow - Mobile
	 * 
	 * @param defaultHomeFlowDto
	 * @param defFlowAppConfig
	 * @return BffCoreResponse
	 */
	private BffCoreResponse validateAndPrepareDefaultflow(DefaultHomeFlowDto defaultHomeFlowDto,
			AppConfigMaster defFlowAppConfig) {
		BffCoreResponse bffCoreResponse = null;

		if (null != defFlowAppConfig && defFlowAppConfig.getAppConfigDetails() != null) {
			// Default flow
			for (AppConfigDetail defFlowConfigDetail : defFlowAppConfig.getAppConfigDetails()) {
				if (null != defFlowConfigDetail && !ObjectUtils.isEmpty(defFlowConfigDetail.getConfigValue())) {
					Optional<Flow> defaultFlow = flowRepo
							.findById(UUID.fromString(defFlowConfigDetail.getConfigValue()));

					// Validate default flow
					if (!defaultFlow.isPresent()) {
						return bffResponse.errResponse(
								List.of(BffResponseCode.ERR_PRODUCT_PREPARE_API_DEFAULT_FLOW_NOT_FOUND,
										BffResponseCode.ERR_PRODUCT_PREPARE_API_DEFAULT_FLOW_NOT_FOUND),
								StatusCode.BADREQUEST, null, defFlowConfigDetail.getConfigValue());
					}
					bffCoreResponse = validateDefaultFlow(defFlowAppConfig, defaultFlow);
					if (bffCoreResponse != null) {
						return bffCoreResponse;
					}

					defaultHomeFlowDto.setDefaultFlowId(defaultFlow.get().getUid());
					defaultHomeFlowDto.setDefaultFlowName(defaultFlow.get().getName());
					defaultHomeFlowDto.setDefaultFlowVersion(defaultFlow.get().getVersion());

					// Validate default flow - default form Id
					if (defaultFlow.get().getPublishedDefaultFormId() != null) {
						Optional<Form> defaultFormOfDefaultFlow = formRepository
								.getModalAndTabbedDetails(defaultFlow.get().getPublishedDefaultFormId());
						bffCoreResponse = validateDefFormOfDefFlow(defFlowAppConfig, defaultFormOfDefaultFlow);
						if (bffCoreResponse != null) {
							return bffCoreResponse;
						}
						defaultHomeFlowDto
								.setDefaultFlowdefFormId(defaultFlow.get().getPublishedDefaultFormId().toString());
						defaultHomeFlowDto.setDefaultFormTabbed(defaultFlow.get().isDefaultFormTabbed());
						defaultHomeFlowDto.setDefaultFormModalForm(defaultFlow.get().isDefaultModalForm());
					}
					else
					{
						return bffResponse.errResponse(
								List.of(BffResponseCode.ERR_PRODUCT_PREPARE_API_DEFAULT_FLOW_DEFAULT_FORM_NOT_FOUND,
										BffResponseCode.ERR_PRODUCT_PREPARE_API_DEFAULT_FLOW_DEFAULT_FORM_NOT_FOUND),
								StatusCode.BADREQUEST, null, defFlowConfigDetail.getConfigValue());
					}
						
				}
			}
		}
		return bffCoreResponse;
	}
}
