/**
 * 
 */
package com.jda.mobility.framework.extensions.model;

/**
 * The class PrepRequest.java HCL Technologies Ltd.
 */

public class PrepRequest {
	/** The field flowRo of type FlowRo */
	private FlowRequest flowRo;

	/** The field name of type String */
	private String name;

	/** The field propValue of type String */
	private String propValue;

	/** The field version of type String */
	private int version;
	
	private boolean isDefaultWarehouse;

	/**
	 * @return the flowRo of type FlowRo
	 */
	public FlowRequest getFlowRo() {
		return flowRo;
	}

	/**
	 * @param flowRo of type FlowRo
	 */
	public void setFlowRo(FlowRequest flowRo) {
		this.flowRo = flowRo;
	}

	/**
	 * @return the name of type String
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name of type String
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the propValue of type String
	 */
	public String getPropValue() {
		return propValue;
	}

	/**
	 * @param propValue of type String
	 */
	public void setPropValue(String propValue) {
		this.propValue = propValue;
	}

	/**
	 * @return the version of type int
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * @param version of type int
	 */
	public void setVersion(int version) {
		this.version = version;
	}

	public boolean getIsDefaultWarehouse() {
		return isDefaultWarehouse;
	}

	public void setIsDefaultWarehouse(boolean isDefaultWarehouse) {
		this.isDefaultWarehouse = isDefaultWarehouse;
	}
}