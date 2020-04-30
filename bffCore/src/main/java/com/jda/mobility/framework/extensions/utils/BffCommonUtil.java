package com.jda.mobility.framework.extensions.utils;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

/**
 * Utility class to check user permission , fetch Resource bundle keys  
 * @author HCL
 *
 */
@Component
public class BffCommonUtil {
	
	@Autowired
	private ResourceBundleRepository resBundleRepo;
	@Autowired
	private SessionDetails sessionDetails;
	@Autowired
	ProductPropertyRepository productPropertyRepository;
	@Autowired
	private ProductMasterRepository productMasterRepo;
	

	/**Fetch and prepare resource bundle object for given key
	 *  - If no value present for given locale , key will be set as value
	 * @param  key Resource bundle Key 
	 * @return TranslationRequest Resource bundle Object
	 */
	public TranslationRequest getResourceBundle(String key) {
		if (StringUtils.isNotEmpty(key)) {
			// Get locale from session
			String locale = sessionDetails.getLocale() != null ? sessionDetails.getLocale()
					: BffAdminConstantsUtils.LOCALE;
			// Get the Resounce Bundle for given key and locale
			List<ResourceBundle> menuResourceBundle = resBundleRepo.findByLocaleAndRbkey(locale, key);
			if (menuResourceBundle != null && !menuResourceBundle.isEmpty()) {
				ResourceBundle translation = menuResourceBundle.get(0);
				return TranslationRequest.builder()
								.locale(translation.getLocale())
								.rbkey(translation.getRbkey())
								.rbvalue(translation.getRbvalue())
								.type(translation.getType())				
								.uid(translation.getUid())
								.build();
			} else {
				//Set the rbvalue with key ,if no entry found for that locale
				return TranslationRequest.builder()
						.locale(locale)
						.rbkey(key)
						.rbvalue(key)
						.type(BffAdminConstantsUtils.EMPTY_SPACES)
						.build();
			}
		}
		return null;
	}
	
	/**Check whether User has permission to access the flow
	 * 
	 * @param flowPermissionList List of flow permissions
	 * @param userPermissions List of user permissions
	 * @return boolean Returns true if user has permission to the flow
	 */
	public boolean checkUserHasPermissionForFlow( List<FlowPermission> flowPermissionList,
			List<String> userPermissions) {
		boolean isPresent = false;
		
		for (FlowPermission permission : flowPermissionList) {
			if (userPermissions.contains(permission.getPermission())) {
				isPresent = true;
				break;
			}
		}
		return isPresent;
	}
	
	/**Create or Retrieve the secondary Reference Id
	 *  - If already present (productName) - Retrieve the secondaryRefId
	 *  - If not present(productName) - Create and retrieves seondaryRefId
	 *  
	 * @param warehouseName Name of the product /warehouse
	 * @return ProductProperty Object holds the variables for Product 
	 */
	public ProductProperty createdOrGetSecondaryRefId(String warehouseName)
	{
		ProductProperty result = null ;
		// Get the secondaryRefId for warehouse name
		List<ProductProperty> productProperty = productPropertyRepository.findByNameAndPropValueAndIsSecondaryRefTrue(
				BffAdminConstantsUtils.PRODUCT_MASTER_CODE, warehouseName);

		if (productProperty != null && !productProperty.isEmpty()) {
			result = productProperty.get(0);
		} else {
			// Get Product name
			ProductMaster productMaster = productMasterRepo.findByName(BffAdminConstantsUtils.PRODUCT_MASTER_CODE);

			ProductProperty proProp = new ProductProperty();
			proProp.setName(BffAdminConstantsUtils.PRODUCT_MASTER_CODE);
			proProp.setPropValue(warehouseName);
			proProp.setProductMaster(productMaster);
			proProp.setPrimaryRef(false);
			proProp.setSecondaryRef(true);
			result = productPropertyRepository.save(proProp);
		}
		return result;
	}
	
	/**Check whether user has permission for menu items
	 * 
	 * @param menuPermissionList List of menu permission
	 * @param userPermissions List of user permission
	 * @return boolean Return true if user has permission to menu items
	 */
	public boolean checkMenuHasPermissionForMenu( List<MenuPermission> menuPermissionList,
			List<String> userPermissions) {
		boolean isPresent = false;
		
		for (MenuPermission permission : menuPermissionList) {
			if (userPermissions.contains(permission.getPermission())) {
				isPresent = true;
				break;
			}
		}
		return isPresent;
	}
	

}
