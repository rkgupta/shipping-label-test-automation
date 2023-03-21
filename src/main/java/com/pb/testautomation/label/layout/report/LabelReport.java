package com.pb.testautomation.label.layout.report;

import java.util.ArrayList;
import java.util.List;

import com.pb.testautomation.label.report.IReport;
/**
 * This class represents the label report that gets generated in the report file.
 * @author ra013gu
 *
 */
public class LabelReport implements IReport {
	
	public static final String TEST_LABEL_CORRUPT = "Test label might be corrupt.";
	
	private String labelName;
	private long executionTime;
	private boolean isLabelMatch;
	List<IReport> pageReports = new ArrayList<IReport>();
	/**
	 * Creates a LabelReport instance for label <code>label</code>.
	 * @param label
	 */
	public LabelReport(String labelName) {
		this.labelName = labelName;
		pageReports = new ArrayList<IReport>();
	}
	/**
	 * Adds a page report. 
	 * @param pageReport
	 */
	public void addReport(IReport pageReport){
		pageReports.add(pageReport);
	}

	/**
	 * @return the label
	 */
	public String getLabelName() {
		return labelName;
	}

	/**
	 * @return the executionTime
	 */
	public long getExecutionTime() {
		return executionTime;
	}

	/**
	 * @param executionTime the executionTime to set
	 */
	public void setExecutionTime(long executionTime) {
		this.executionTime = executionTime;
	}

	/**
	 * @return the isLabelMatch
	 */
	public boolean isLabelMatch() {
		if(pageReports.isEmpty()) {
			isLabelMatch = false;
		} else {
			boolean match = true;
			for(IReport pageReport : pageReports) {
				if(pageReport instanceof PageReport && !((PageReport)pageReport).isPageMatch()){
					match = false;
					break;
				}
			}
			isLabelMatch = match;
		}
		return isLabelMatch;
	}

	/**
	 * @param isLabelMatch the isLabelMatch to set
	 */
	public void setLabelMatch(boolean isLabelMatch) {
		this.isLabelMatch = isLabelMatch;
	}

	/**
	 * @return the pageReports
	 */
	public List<IReport> getPageReports() {
		return pageReports;
	}
	/**
	 * Generates label comparison report.  
	 */
	public String generateReport() {
		StringBuilder labelReportBuilder = new StringBuilder();
		labelReportBuilder.append("Testing layout for label "+ labelName);
		if(pageReports.isEmpty()) {
			labelReportBuilder.append("\n\t" + TEST_LABEL_CORRUPT);
		} else {
			for(IReport pageReport : pageReports){
				if(pageReport != null){
					labelReportBuilder.append(pageReport.generateReport());	
				}			
			}			
		}
		return labelReportBuilder.toString();
	}
	/**
	 * Clears all page element reports.
	 */
	public void clearPageReport() {
		pageReports.clear();
	}
}
