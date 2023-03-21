package com.pb.testautomation.label.layout.model;


/**
 * Maskable Region Model corresponding to maskableRegion element in config file.
 * 
 * @author ta013ba
 */
public class Region {
	private String id;
	
	private float left;
	private float top;
	private float width;
	private float height;
	
	private Float paddingLeft;
	private Float paddingTop;
	private Float paddingBottom;
	private Float paddingRight;
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
	 * @return the paddingLeft
	 */
	public Float getPaddingLeft() {
		if(paddingLeft != null) {
			return paddingLeft;			
		}
		return 0f;
	}
	/**
	 * @return the paddingTop
	 */
	public Float getPaddingTop() {
		if(paddingTop != null) {
			return paddingTop;			
		}
		return 0f;
	}
	/**
	 * @return the paddingBottom
	 */
	public Float getPaddingBottom() {
		if(paddingBottom != null) {
			return paddingBottom;			
		}
		return 0f;
	}
	/**
	 * @return the paddingRight
	 */
	public Float getPaddingRight() {
		if(paddingRight != null) {
			return paddingRight;			
		}
		return 0f;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
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
		this.paddingBottom = padding;
		this.paddingLeft = padding;
		this.paddingTop = padding;
		this.paddingRight = padding;
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
		if(lineWidthLeft != null) {
			return lineWidthLeft;			
		}
		return 0f;
	}
	/**
	 * @return the lineWidthRight
	 */
	public Float getLineWidthRight() {
		if(lineWidthRight != null) {
			return lineWidthRight;			
		}
		return 0f;
	}
	/**
	 * @return the lineWidthTop
	 */
	public Float getLineWidthTop() {
		if(lineWidthTop != null) {
			return lineWidthTop;			
		}
		return 0f;
	}
	/**
	 * @return the lineWidthBottom
	 */
	public Float getLineWidthBottom() {
		if(lineWidthBottom != null) {
			return lineWidthBottom;			
		}
		return 0f;
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
