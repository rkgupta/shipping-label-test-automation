package com.pb.testautomation.label.report;
/**
 * Represents the visual report that gets created while comparing images.
 * @author RA013GU
 *
 */
public class VisualDiffReport implements IReport {
	
	private String orientationMessage;
	private String diffImageMessage;

	/**
	 * @return the orientationMessage
	 */
	public String getOrientationMessage() {
		return orientationMessage;
	}

	/**
	 * @param orientationMessage the orientationMessage to set
	 */
	public void setOrientationMessage(String orientationMessage) {
		this.orientationMessage = orientationMessage;
	}

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
		StringBuilder sb = new StringBuilder("\n\t\t\tVisual Difference(s):\n");
		
		boolean isMismatch = false;
		if(orientationMessage != null) {
			sb.append("\t\t\t\t" + orientationMessage + "\n");
			isMismatch = true;
		}
		if(diffImageMessage != null) {
			sb.append("\t\t\t\t" + diffImageMessage+ "\n");
			isMismatch = true;
		}
		if(!isMismatch) {
			//Reset the string buffer length if no difference is found
			sb.setLength(0);
		}
		return sb.toString();
	}

}
