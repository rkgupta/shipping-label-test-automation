package com.pb.testautomation.label.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Template model. This class represent a particular template which can be positioned anywhere on a page.
 * 
 * @author ta013ba
 */
public class Template {
	private String id;
	private List<MaskableRegion> maskableRegions;
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @return the maskableRegions
	 */
	public List<MaskableRegion> getMaskableRegions() {
		return maskableRegions;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @param maskableRegions the maskableRegions to set
	 */
	public void setMaskableRegions(List<MaskableRegion> maskableRegions) {
		this.maskableRegions = maskableRegions;
	}
	/**
	 * 
	 * @param region the maskable region to add.
	 */
	public void addMaskableRegion(MaskableRegion region) {
		if(maskableRegions == null) {
			maskableRegions = new ArrayList<MaskableRegion>();
		}
		maskableRegions.add(region);
	}
}
