package com.pb.testautomation.label.model;

/**
 * Lable Spec Model corresponding to labelSpec element in config file.
 * 
 * @author ta013ba
 */
public class LabelSpec {
	private float templateLeft;
	private float templateTop;
	private Template template;
	
	/**
	 * @return the templateLeft
	 */
	public float getTemplateLeft() {
		return templateLeft;
	}
	/**
	 * @return the templateTop
	 */
	public float getTemplateTop() {
		return templateTop;
	}
	/**
	 * @return the template
	 */
	public Template getTemplate() {
		return template;
	}
	/**
	 * @param templateLeft the templateLeft to set
	 */
	public void setTemplateLeft(float templateLeft) {
		this.templateLeft = templateLeft;
	}
	/**
	 * @param templateTop the templateTop to set
	 */
	public void setTemplateTop(float templateTop) {
		this.templateTop = templateTop;
	}
	/**
	 * @param template the template to set
	 */
	public void setTemplate(Template template) {
		this.template = template;
	}
}
