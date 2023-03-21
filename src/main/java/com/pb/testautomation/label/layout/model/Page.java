package com.pb.testautomation.label.layout.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Page Model corresponding to the config file page element.
 * 
 * @author ta013ba
 */
public class Page {
	private String number;
	private List<LabelSpec> labelSpecs;
	
	/**
	 * @return the number
	 */
	public String getNumber() {
		return number;
	}
	/**
	 * @return the labelSpecs
	 */
	public List<LabelSpec> getLabelSpecs() {
		return labelSpecs;
	}
	/**
	 * @param number the number to set
	 */
	public void setNumber(String number) {
		this.number = number;
	}
	/**
	 * @param labelSpecs the labelSpecs to set
	 */
	public void setLabelSpecs(List<LabelSpec> labelSpecs) {
		this.labelSpecs = labelSpecs;
	}
	
	/**
	 * This method adds the label spec to the domain model
	 * @param labelSpec
	 */
	public void addLabelSpec(LabelSpec labelSpec) {
		if(labelSpecs == null) {
			labelSpecs = new ArrayList<LabelSpec>();
		}
		labelSpecs.add(labelSpec);
	}
}
