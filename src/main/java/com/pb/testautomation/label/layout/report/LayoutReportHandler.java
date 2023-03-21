package com.pb.testautomation.label.layout.report;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.pb.testautomation.label.constant.CommonConstants;
import com.pb.testautomation.label.report.ExecutionStatus;
import com.pb.testautomation.label.report.IReport;
import com.pb.testautomation.label.report.ReportHandler;
import com.pb.testautomation.label.util.CommonUtil;
import com.pb.testautomation.label.util.LabelUtil;
import com.pb.testautomation.label.util.Messages;

/**
 * Report class that handles generation of layout testing report.
 * @author ra013gu
 *
 */
public class LayoutReportHandler extends ReportHandler {
	
	private static int labelNo = 1;
	private int matchingLabelsCount = 0;
	private Logger logger = Logger.getLogger(LayoutReportHandler.class);
	
	private HSSFWorkbook workbook;
	private HSSFSheet sheet;
	

	public LayoutReportHandler(File test) {
		super(test);
		labelNo = 1;
		appendToFile(reportFile, getReportFileHeader());
		try {
			workbook = new HSSFWorkbook();
			sheet = workbook.createSheet("Sheet1");
			
			initExcelData(workbook);
			writeStatusFileHeader();
		} catch (Exception e) {
			logger.error(Messages.getMessage(CommonConstants.STATUS_FILE_WRITE_EXCEPTION, e.getMessage()));
		}
	}

	private void writeStatusFileHeader() throws IOException {
		//Product
		HSSFRow productRow = sheet.createRow(0);
		
		HSSFCell productCell = productRow.createCell(0);
		productCell.setCellValue(new HSSFRichTextString("PRODUCT = "));
		
		HSSFCell productVal = productRow.createCell(1);
		productVal.setCellValue(new HSSFRichTextString(LabelUtil.getProperties().getProperty("PRODUCT")));
		
		//Version
		HSSFRow versionRow = sheet.createRow(1);
		
		HSSFCell versionCell = versionRow.createCell(0);
		versionCell.setCellValue(new HSSFRichTextString("VERSION = "));
		
		HSSFCell versionVal = versionRow.createCell(1);
		versionVal.setCellValue(new HSSFRichTextString(LabelUtil.getProperties().getProperty("VERSION")));
		
		//Workstation
		HSSFRow workstationRow = sheet.createRow(2);
		
		HSSFCell workstationCell = workstationRow.createCell(0);
		workstationCell.setCellValue(new HSSFRichTextString("WORKSTATION = "));
		
		HSSFCell workstationVal = workstationRow.createCell(1);
		workstationVal.setCellValue(new HSSFRichTextString(LabelUtil.getProperties().getProperty("WORKSTATION")));
		
		//Software Image
		HSSFRow swImageRow = sheet.createRow(3);
		
		HSSFCell swCell = swImageRow.createCell(0);
		swCell.setCellValue(new HSSFRichTextString("SOFTWARE_IMAGE = "));
		
		HSSFCell swVal = swImageRow.createCell(1);
		swVal.setCellValue(new HSSFRichTextString(LabelUtil.getProperties().getProperty("SOFTWARE_IMAGE")));
		
		//Tester
		HSSFRow testerRow = sheet.createRow(4);
		
		HSSFCell testerCell = testerRow.createCell(0);
		testerCell.setCellValue(new HSSFRichTextString("TESTER = "));
		
		HSSFCell testerVal = testerRow.createCell(1);
		testerVal.setCellValue(new HSSFRichTextString(LabelUtil.getProperties().getProperty("TESTER")));
		
		//Script Name
		HSSFRow scriptNameRow = sheet.createRow(5);
		
		HSSFCell scriptCell = scriptNameRow.createCell(0);
		scriptCell.setCellValue(new HSSFRichTextString("AUTOMATIED_SCRIPT_NAME = "));
		
		HSSFCell scriptVal = scriptNameRow.createCell(1);
		scriptVal.setCellValue(new HSSFRichTextString(LabelUtil.getProperties().getProperty("AUTOMATIED_SCRIPT_NAME")));
		
		//Build Number
		HSSFRow buildNumRow = sheet.createRow(6);
		
		HSSFCell buildCell = buildNumRow.createCell(0);
		buildCell.setCellValue(new HSSFRichTextString("AUTOMATON_BUILD_NO = "));
		
		HSSFCell buildVal = buildNumRow.createCell(1);
		buildVal.setCellValue(new HSSFRichTextString(LabelUtil.getProperties().getProperty("AUTOMATON_BUILD_NO")));
		
		//Date Executed
		HSSFRow dateRow = sheet.createRow(7);
		
		HSSFCell dateCell = dateRow.createCell(0);
		dateCell.setCellValue(new HSSFRichTextString("DATE_EXECUTED = "));
		
		HSSFCell dateVal = dateRow.createCell(1);
		dateVal.setCellValue(new HSSFRichTextString(CommonUtil.getSimpleDateTimeFormat(System.currentTimeMillis())));
		
		//Test Folder
		HSSFRow testFolderRow = sheet.createRow(8);
		
		HSSFCell testFolderCell = testFolderRow.createCell(0);
		testFolderCell.setCellValue(new HSSFRichTextString("TEST_FOLDER = "));
		
		HSSFCell testFolderVal = testFolderRow.createCell(1);
		testFolderVal.setCellValue(new HSSFRichTextString(test.getAbsolutePath()));
		
//		WritableHyperlink testLocValue = new WritableHyperlink(1, 8, test.getCanonicalFile());
//		sheet.addHyperlink(testLocValue);
		
		//Diff Image
		HSSFRow diffImageRow = sheet.createRow(8);
		
		HSSFCell diffFolderCell = diffImageRow.createCell(0);
		diffFolderCell.setCellValue(new HSSFRichTextString("LAYOUT_DIFF_IMAGE_FOLDER = "));
		
		HSSFCell diffFolderVal = diffImageRow.createCell(1);
		diffFolderVal.setCellValue(new HSSFRichTextString(getDiffImageDirectory().getAbsolutePath()));
		
//		WritableHyperlink diffLocValue = new WritableHyperlink(1, 9, getDiffImageDirectory().getCanonicalFile());
//		sheet.addHyperlink(diffLocValue);
		
		HSSFRow headersRow = sheet.createRow(11);
		
		//Label
		HSSFCell labelCell = headersRow.createCell(0);
		labelCell.setCellValue(new HSSFRichTextString("Label"));
		labelCell.setCellStyle(boldHeaderFormat);
		
		//Template
		HSSFCell templateCell = headersRow.createCell(1);
		templateCell.setCellValue(new HSSFRichTextString("Template"));
		templateCell.setCellStyle(boldHeaderFormat);
		
		//Type
		HSSFCell typeCell = headersRow.createCell(2);
		typeCell.setCellValue(new HSSFRichTextString("Type"));
		typeCell.setCellStyle(boldHeaderFormat);
		
		//Status
		HSSFCell statusCell = headersRow.createCell(3);
		statusCell.setCellValue(new HSSFRichTextString("Status"));
		statusCell.setCellStyle(boldHeaderFormat);
		
		//Execution Time
		HSSFCell executionTimeCell = headersRow.createCell(4);
		executionTimeCell.setCellValue(new HSSFRichTextString("Execution Time"));
		executionTimeCell.setCellStyle(boldHeaderFormat);
	}

	private String getReportFileHeader() {
		StringBuilder reportBuilder = new StringBuilder();
		reportBuilder.append("Testing layout for label(s) at: "+test.getAbsolutePath()+" \ndated "
				+CommonUtil.getSimpleDateTimeFormat(System.currentTimeMillis()));
		reportBuilder.append("\n----------------------------------------------------------------------------\n");
		return reportBuilder.toString();
	}

	@Override
	public void addReport(IReport report) {
		testReport.add(report);
		appendToReportFile(report);
		appendToTestStatusFile(report);
		testReport.clear();
	}
	
	/**
	 * Appends the label report to the report file.
	 * @param report
	 */
	private void appendToReportFile(IReport report) {
		StringBuilder reportBuilder = new StringBuilder();
		reportBuilder.append(labelNo+++"."+report.generateReport()+"\n\n");	
		appendToFile(reportFile, reportBuilder.toString());
	}

	/**
	 * Appends the label report to the test status file.
	 * @param report
	 */
	private void appendToTestStatusFile(IReport report) {
		if(report instanceof LabelReport){
			String labelName = ((LabelReport)report).getLabelName();
			String templateType = labelName.split(LabelUtil.LABEL_TYPE_FILE_NAME_DELIMITER)[0];
			String labelType = CommonUtil.getLabelType(labelName);
			String executionTime = CommonUtil.getExecutionTimeFormat(((LabelReport) report).getExecutionTime());
			
			boolean isLabelMatch = ((LabelReport)report).isLabelMatch();
			String executionStatus = isLabelMatch ? ExecutionStatus.PASS.toString() : ExecutionStatus.FAIL.toString();
			if(isLabelMatch) {
				matchingLabelsCount++;
			}
			
			int row = 11 + labelNo - 1;
			
			try {
				HSSFRow dataRow = sheet.createRow(row);
				
				HSSFCell labelCell = dataRow.createCell(0);
				labelCell.setCellValue(new HSSFRichTextString(labelName));
				if(!isLabelMatch) {
					labelCell.setCellStyle(failureCaseFormat);					
				}
				
				HSSFCell templateTypeCell = dataRow.createCell(1);
				templateTypeCell.setCellValue(new HSSFRichTextString(templateType));
				if(!isLabelMatch) {
					templateTypeCell.setCellStyle(failureCaseFormat);					
				}
				
				HSSFCell labelTypeCell = dataRow.createCell(2);
				labelTypeCell.setCellValue(new HSSFRichTextString(labelType));
				if(!isLabelMatch) {
					labelTypeCell.setCellStyle(failureCaseFormat);					
				}
				
				HSSFCell execStatusCell = dataRow.createCell(3);
				execStatusCell.setCellValue(new HSSFRichTextString(executionStatus));
				if(!isLabelMatch) {
					execStatusCell.setCellStyle(failureCaseFormat);					
				}
				
				HSSFCell execTimeCell = dataRow.createCell(4);
				execTimeCell.setCellValue(new HSSFRichTextString(executionTime));
				if(!isLabelMatch) {
					execTimeCell.setCellStyle(failureCaseFormat);					
				}
			} catch(Exception e) {
				logger.error(Messages.getMessage(CommonConstants.STATUS_FILE_WRITE_EXCEPTION, e.getMessage()));
			}
		}
	}

	@Override
	public File generateReport() {
		FileOutputStream fos = null;
		StringBuilder reportBuilder = new StringBuilder();
		reportBuilder.append("\nTime taken to test layout of the labels: "+CommonUtil.getExecutionTimeFormat(executionTime));
		reportBuilder.append("\n-------------------------------------END------------------------------------\n");
		appendToFile(reportFile, reportBuilder.toString());
		
		try {
			int row = 11 + labelNo + 2;
			
			HSSFRow summaryRow = sheet.createRow(row);
			
			HSSFCell summaryCell = summaryRow.createCell(0);
			summaryCell.setCellValue(new HSSFRichTextString("===== Summary Of Test Run Results ====="));
			summaryCell.setCellStyle(boldSummaryFormat);
			
			HSSFRow summaryDetailsRow = sheet.createRow(row + 1);
			
			HSSFCell summaryDetailsCell = summaryDetailsRow.createCell(0);
			summaryDetailsCell.setCellValue(new HSSFRichTextString("Passed:"+matchingLabelsCount + " Failed:"+((labelNo-1)-matchingLabelsCount) + " Total:"+(labelNo-1)));
			summaryDetailsCell.setCellStyle(boldSummaryFormat);
			
			if(!testStatusFile.exists()) {
				testStatusFile.createNewFile();
			}
			fos = new FileOutputStream(testStatusFile);
			workbook.write(fos);
		} catch (Exception e) {
			logger.error(Messages.getMessage(CommonConstants.STATUS_FILE_WRITE_EXCEPTION, e.getMessage()));
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException ex) {}
		}
		
		return super.generateReport();
	}

}
