package com.pb.testautomation.label.layout.report;

import java.util.ArrayList;
import java.util.List;

import com.pb.testautomation.label.report.IReport;

/**
 * This class represent the Textual Difference report between the master and test labels.
 * 
 * @author ra013gu
 */
public class TextDiffReport implements IReport {
	private List<String> invalidLayoutRegions;
	/**
	 * Generates report for regions with invalid layout. 
	 */
	public String generateReport() {
		StringBuilder sb = new StringBuilder();
		if(invalidLayoutRegions != null && invalidLayoutRegions.size() > 0) {
			sb.append("\n\t\t\tRegions with invalid layout: " + invalidLayoutRegions.toString());
		}
		return sb.toString();
	}
	
	/**
	 * This method add the missing text, the text which is missing in the test file.
	 * 
	 * @param text	Text which is missing in test file.
	 */
	public void addInvalidLayoutRegion(String text) {
		if(invalidLayoutRegions == null) {
			invalidLayoutRegions = new ArrayList<String>();
		}
		invalidLayoutRegions.add(text);
	}
	

}
