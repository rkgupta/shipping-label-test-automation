package com.pb.testautomation.label.report;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represent the Textual Difference report between the master and test labels.
 * 
 * @author ta013ba
 */
public class TextDiffReport implements IReport {
	private List<String> missingText;
	private List<String> additionalText;
	/**
	 * Generates report for any textual difference found while comparing labels. 
	 */
	public String generateReport() {
		StringBuilder sb = new StringBuilder("\n\t\t\tText Difference(s):\n");
		
		boolean isMismatch = false;
		if(missingText != null && missingText.size() > 0) {
			sb.append("\t\t\t\tMissing text in test label: " + missingText.toString());
			isMismatch = true;
		}
		
		if(additionalText != null && additionalText.size() > 0) {
			sb.append("\n\t\t\t\tAdditional text in test label: " + additionalText.toString());
			isMismatch = true;
		}
		if(!isMismatch) {
			//Reset the string buffer length if no difference is found
			sb.setLength(0);
		}
		
		return sb.toString();
	}
	
	/**
	 * This method add the missing text, the text which is missing in the test file.
	 * 
	 * @param text	Text which is missing in test file.
	 */
	public void addMissingText(String text) {
		if(missingText == null) {
			missingText = new ArrayList<String>();
		}
		text = text.replaceAll("\r\n", ", ");
		missingText.add(text);
	}
	
	/**
	 * This method add the additional text, the text which is additional in the test file.
	 * 
	 * @param text	Text which is additional in the test file.
	 */
	public void addAdditionalText(String text) {
		if(additionalText == null) {
			additionalText = new ArrayList<String>();
		}
		text = text.replaceAll("\r\n", ", ");
		additionalText.add(text);
	}

}
