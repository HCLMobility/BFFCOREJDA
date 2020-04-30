/**
 * 
 */
package com.jda.mobility.framework.extensions.model;

import javax.validation.constraints.NotNull;

/**
 * @author HCL Technologies Limited
 * Model object for auto complete controller
 *
 */
public class SearchRequest {

	@NotNull
	private String searchType;
	
	private String searchTerm;
	/**
	 * @return the sourceName
	 */
	public String getSearchType() {
		return searchType;
	}
	/**
	 * @param searchType the searchType to set
	 */
	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}
	/**
	 * @return the searchTerm
	 */
	public String getSearchTerm() {
		return searchTerm;
	}
	/**
	 * @param searchTerm the searchTerm to set
	 */
	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}

}
