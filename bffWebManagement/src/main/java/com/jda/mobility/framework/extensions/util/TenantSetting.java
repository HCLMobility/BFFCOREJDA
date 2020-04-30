/**
 * 
 */
package com.jda.mobility.framework.extensions.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.jda.mobility.framework.extensions.entity.ProductTenantConfig;
import com.jda.mobility.framework.extensions.repository.ProductTenantConfigRepository;

import lombok.Data;

/**
 * @author HCL Technologies Ltd.
 *
 */
@Component
@Data
public class TenantSetting {
	@Autowired
	private ProductTenantConfigRepository prodTenantConfigRepo;
	
	private Map<String, Map<String, String>> tenantConfigMap = new HashMap<>();
	
	@PostConstruct
    public void init() {
		tenantConfigMap.clear();
		List<String> tenants = prodTenantConfigRepo.findDistinctByTenant();
		if(!CollectionUtils.isEmpty(tenants)) {			
			for(String tenant : tenants) {
				tenantConfigMap.put(tenant, new HashMap<String, String>());
			}
			List<ProductTenantConfig> tenantConfigs = prodTenantConfigRepo.findAll();
			for(ProductTenantConfig tenantConfig : tenantConfigs) {
				tenantConfigMap.get(tenantConfig.getTenant()).put(tenantConfig.getConfigName(), tenantConfig.getConfigValue());
			}
		}
	}
}