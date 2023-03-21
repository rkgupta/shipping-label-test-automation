package com.pb.testautomation.label.layout.report;

import com.pb.testautomation.label.report.IReport;

/**
 * Represents the visual report that gets created while comparing layout of image.
 * @author RA013GU
 *
 */
public class VisualDiffReport implements IReport {
	
	private String diffImageMessage;

	/**
	 * @return the diffImageMessage
	 */
	public String getDiffImageMessage() {
		return diffImageMessage;
	}

	/**
	 * @param diffImageMessage the diffImageMessage to set
	 */
	public void setDiffImageMessage(String diffImageMessage) {
		this.diffImageMessage = diffImageMessage;
	}
	/**
	 *  Generates report for any visual difference found while comparing labels.
	 */
	public String generateReport() {
		StringBuilder sb = new StringBuilder();
		
		if(diffImageMessage != null) {
			sb.append("\n\t\t\t" + diffImageMessage+ "\n");
		}
		return sb.toString();
	}

}
