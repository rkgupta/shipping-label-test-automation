package com.pb.testautomation.label.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Label Type Config Model corresponding to labelTypeConfig element in config file.
 * 
 * @author ta013ba
 */
public class LabelTypeConfig {
	private String prefix;
	private String uom;
	private int resolution;
	private Map<Integer, Page> pagesMap;
	
	/**
	 * @return the prefix
	 */
	public String getPrefix() {
		return prefix;
	}
	/**
	 * @return the uom
	 */
	public String getUom() {
		return uom;
	}
	/**
	 * @return the resolution
	 */
	public int getResolution() {
		return resolution;
	}
	/**
	 * @param prefix the prefix to set
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	/**
	 * @param uom the uom to set
	 */
	public void setUom(String uom) {
		this.uom = uom;
	}
	/**
	 * @param resolution the resolution to set
	 */
	public void setResolution(int resolution) {
		this.resolution = resolution;
	}
	/**
	 * @return the pagesMap
	 */
	public Map<Integer, Page> getPagesMap() {
		return pagesMap;
	}
	/**
	 * @param pagesMap the pagesMap to set
	 */
	public void setPagesMap(Map<Integer, Page> pagesMap) {
		this.pagesMap = pagesMap;
	}
	/**
	 * Adds a Page instance in the map.
	 * @param pageNumber
	 * @param page
	 */
	public void addPage(int pageNumber, Page page) {
		if(pagesMap == null) {
			pagesMap = new HashMap<Integer, Page>();
		}
		pagesMap.put(pageNumber, page);
	}
	/**
	 * 
	 * @param pageNumber
	 * @return Page instance corresponding to the given page number.
	 */
	public Page getPage(int pageNumber) {
		if(pagesMap != null) {
			return pagesMap.get(pageNumber);
		}
		return null;
	}
}
