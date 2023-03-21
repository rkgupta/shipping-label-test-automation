package com.pb.testautomation.label.model;


/**
 * Maskable Region Model corresponding to maskableRegion element in config file.
 * 
 * @author ta013ba
 */
public class MaskableRegion {
	private String id;
	private String regionType;
	private String direction;
	private float left;
	private float top;
	private float width;
	private float height;
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @return the regionType
	 */
	public String getRegionType() {
		return regionType;
	}
	/**
	 * @return the direction
	 */
	public String getDirection() {
		return direction;
	}
	/**
	 * @param direction the direction to set
	 */
	public void setDirection(String direction) {
		this.direction = direction;
	}
	/**
	 * @return the left
	 */
	public float getLeft() {
		return left;
	}
	/**
	 * @return the top
	 */
	public float getTop() {
		return top;
	}
	/**
	 * @return the width
	 */
	public float getWidth() {
		return width;
	}
	/**
	 * @return the height
	 */
	public float getHeight() {
		return height;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @param regionType the regionType to set
	 */
	public void setRegionType(String regionType) {
		this.regionType = regionType;
	}
	/**
	 * @param left the left to set
	 */
	public void setLeft(float left) {
		this.left = left;
	}
	/**
	 * @param top the top to set
	 */
	public void setTop(float top) {
		this.top = top;
	}
	/**
	 * @param width the width to set
	 */
	public void setWidth(float width) {
		this.width = width;
	}
	/**
	 * @param height the height to set
	 */
	public void setHeight(float height) {
		this.height = height;
	}
}
