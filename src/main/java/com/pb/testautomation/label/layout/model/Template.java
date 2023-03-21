package com.pb.testautomation.label.layout.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Template model. This class represent a particular template which can be positioned anywhere on a page.
 * 
 * @author ta013ba
 */
public class Template {
	private String id;
	private List<Region> regions;
	
	private Float paddingLeft;
	private Float paddingTop;
	private Float paddingBottom;
	private Float paddingRight;
	private Float defaultPadding;
	private Float lineWidth;
	private Float lineWidthLeft;
	private Float lineWidthRight;
	private Float lineWidthTop;
	private Float lineWidthBottom;
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @return the Regions
	 */
	public List<Region> getRegions() {
		return regions;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @param regions the Regions to set
	 */
	public void setRegions(List<Region> regions) {
		this.regions = regions;
	}
	/**
	 * 
	 * @param region the maskable region to add.
	 */
	public void addRegion(Region region) {
		if(regions == null) {
			regions = new ArrayList<Region>();
		}
		regions.add(region);
	}
	/**
	 * @return the paddingLeft
	 */
	public Float getPaddingLeft() {
		return paddingLeft;
	}
	/**
	 * @return the paddingTop
	 */
	public Float getPaddingTop() {
		return paddingTop;
	}
	/**
	 * @return the paddingBottom
	 */
	public Float getPaddingBottom() {
		return paddingBottom;
	}
	/**
	 * @return the paddingRight
	 */
	public Float getPaddingRight() {
		return paddingRight;
	}
	/**
	 * @param paddingLeft the paddingLeft to set
	 */
	public void setPaddingLeft(Float paddingLeft) {
		this.paddingLeft = paddingLeft;
	}
	/**
	 * @param paddingTop the paddingTop to set
	 */
	public void setPaddingTop(Float paddingTop) {
		this.paddingTop = paddingTop;
	}
	/**
	 * @param paddingBottom the paddingBottom to set
	 */
	public void setPaddingBottom(Float paddingBottom) {
		this.paddingBottom = paddingBottom;
	}
	/**
	 * @param paddingRight the paddingRight to set
	 */
	public void setPaddingRight(Float paddingRight) {
		this.paddingRight = paddingRight;
	}
	public void setPadding(Float padding) {
		this.defaultPadding = padding;
		this.paddingBottom = padding;
		this.paddingLeft = padding;
		this.paddingTop = padding;
		this.paddingRight = padding;
	}
	
	public Float getDefaultPadding() {
		return defaultPadding;
	}
	/**
	 * @return the lineWidth
	 */
	public Float getLineWidth() {
		return lineWidth;
	}
	/**
	 * @param lineWidth the lineWidth to set
	 */
	public void setLineWidth(Float lineWidth) {
		this.lineWidth = lineWidth;
		this.lineWidthLeft = lineWidth;
		this.lineWidthRight = lineWidth;
		this.lineWidthTop = lineWidth;
		this.lineWidthBottom = lineWidth;
	}
	/**
	 * @return the lineWidthLeft
	 */
	public Float getLineWidthLeft() {
		return lineWidthLeft;
	}
	/**
	 * @return the lineWidthRight
	 */
	public Float getLineWidthRight() {
		return lineWidthRight;
	}
	/**
	 * @return the lineWidthTop
	 */
	public Float getLineWidthTop() {
		return lineWidthTop;
	}
	/**
	 * @return the lineWidthBottom
	 */
	public Float getLineWidthBottom() {
		return lineWidthBottom;
	}
	/**
	 * @param lineWidthLeft the lineWidthLeft to set
	 */
	public void setLineWidthLeft(Float lineWidthLeft) {
		this.lineWidthLeft = lineWidthLeft;
	}
	/**
	 * @param lineWidthRight the lineWidthRight to set
	 */
	public void setLineWidthRight(Float lineWidthRight) {
		this.lineWidthRight = lineWidthRight;
	}
	/**
	 * @param lineWidthTop the lineWidthTop to set
	 */
	public void setLineWidthTop(Float lineWidthTop) {
		this.lineWidthTop = lineWidthTop;
	}
	/**
	 * @param lineWidthBottom the lineWidthBottom to set
	 */
	public void setLineWidthBottom(Float lineWidthBottom) {
		this.lineWidthBottom = lineWidthBottom;
	}
}
