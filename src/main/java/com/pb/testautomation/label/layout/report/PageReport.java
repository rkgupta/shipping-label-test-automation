package com.pb.testautomation.label.layout.report;

import java.util.ArrayList;
import java.util.List;

import com.pb.testautomation.label.report.IReport;
/**
 * This class represents the page level report that gets generated in the report file.
 * @author ra013gu
 *
 */
public class PageReport implements IReport {
	
	public static final String LAYOUT_VALID_MESSAGE = "Layout is valid.";
	public static final String LAYOUT_INVALID_MESSAGE = "Layout is not valid.";
	public static final String PAGE_SPEC_MISSING_IN_CONFIG_FILE = "Skipping layout validation. Page specification missing in config file.";
	
	private String pageIndex;
	private boolean isPageMatch;
	private String statusMessage;
	
	private List<IReport> pageElementReports;
	/**
	 * Creates a PageReport instance for page with index <code>pageIndex</code> 
	 * @param pageIndex - page index of the page whose report is generated.
	 */
	public PageReport(String pageIndex) {
		this.pageIndex = pageIndex;
		pageElementReports = new ArrayList<IReport>();;
	}
	/**
	 * @return the isPageMatch
	 */
	public boolean isPageMatch() {
		return isPageMatch;
	}

	/**
	 * @param isPageMatch the isPageMatch to set
	 */
	public void setPageMatch(boolean isPageMatch) {
		this.isPageMatch = isPageMatch;
	}

	/**
	 * @return the statusMessage
	 */
	public String getStatusMessage() {
		return statusMessage;
	}
	/**
	 * @param statusMessage the statusMessage to set
	 */
	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}
	/**
	 * Adds page element report e.g, TextDiffReport and VisualDiffReport. 
	 * @param report - the page element report.
	 */
	public void addReport(IReport report){
		pageElementReports.add(report);
	}
	/**
	 * Generates the page level comparison report.
	 */
	public String generateReport() {
		StringBuilder pageReportBuilder = new StringBuilder();
		pageReportBuilder.append("\n\tPage " + pageIndex + ": " + statusMessage);
		for(IReport pageElementReport : pageElementReports){
			if(pageElementReport != null) {
				pageReportBuilder.append(pageElementReport.generateReport());
			}			
		}
		return pageReportBuilder.toString();
	}

}
