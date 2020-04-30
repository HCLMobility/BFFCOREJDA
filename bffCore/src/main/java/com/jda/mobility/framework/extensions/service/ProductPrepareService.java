/**
 * 
 */
package com.jda.mobility.framework.extensions.service;


import java.util.List;

import org.springframework.retry.annotation.Retryable;

import com.jda.mobility.framework.extensions.entity.ProductConfig;
import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.model.PrepRequest;
/**
 * Interface for ProductPrepareServiceImpl
 */
@Retryable(value = { Exception.class }, maxAttempts = 5)
public interface ProductPrepareService {

	/**
	 * @param configName The config name to fetch default config
	 * @return BffCoreResponse The success/error response object
	 */
	public BffCoreResponse fetchDefaultFlowConfig(String configName);

	/**
	 * @param prepRequest The product preparation request object to prepare the product
	 * @return BffCoreResponse The success/error response object
	 */
	public BffCoreResponse fetchDashboardFlows(PrepRequest prepRequest);

	/**
	 * @param prepRequest The product preparation request object to prepare the product
	 * @return BffCoreResponse The success/error response object
	 */
	public BffCoreResponse fetchProductConfigId(PrepRequest prepRequest);

	/**
	 * @return BffCoreResponse The success/error response object
	 */
	public BffCoreResponse getDefaultHomeFlow();

	/**
	 * @return List&lt;ProductConfig&gt; The list of product config objects in current layer and above
	 */
	public List<ProductConfig> getLayeredProductConfigList();

	/**
	 * @return ProductConfig The product config object of the current layer
	 */
	public ProductConfig getCurrentLayerProdConfigId();
	/**
	 * @param recoveredDeviceId The recovery device id for session recovery purpose
	 * @return BffCoreResponse The success/error response object
	 */
	public BffCoreResponse getAppSettings(String recoveredDeviceId);
}