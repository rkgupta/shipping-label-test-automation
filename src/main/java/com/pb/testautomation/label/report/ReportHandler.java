package com.pb.testautomation.label.report;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;

import com.pb.testautomation.label.constant.CommonConstants;
import com.pb.testautomation.label.util.CommonUtil;
import com.pb.testautomation.label.util.Messages;

/**
 * This interface represents the top level report handler class. All specific
 * report handlers should extend this class and and provide specific implementation.
 * <p>e.g FileReportHandler</p>
 * @author RA013GU
 *
 */
public abstract class ReportHandler {
	
	protected File master;
	protected File test;
	protected File reportFile;
	protected File testStatusFile;
	protected File diffImageDirectory;
	protected List<IReport> testReport;
	
	protected HSSFCellStyle boldHeaderFormat;
	protected HSSFCellStyle boldSummaryFormat;
	protected HSSFCellStyle failureCaseFormat;
	
	protected long executionTime;
	
	protected static String REPORT_DIR_NAME = "Reports" + File.separator + "Report_" + CommonUtil.getSimpleDateTimeFormat(System.currentTimeMillis());
	protected static final String REPORT_FILE_NAME = "Report.txt";
	protected static final String TEST_STATUS_FILE_NAME = "LabelTestExecutionResults.xls";
	protected static final String DIFF_IMAGE_DIR_NAME = "DiffImage";
	
	private Logger logger = Logger.getLogger(ReportHandler.class);
	/**
	 * Constructor to be invoked during regression testing.
	 * @param master the master label file/directory.
	 * @param test the test label file/directory.
	 */
	public ReportHandler(File master, File test){
		this.master = master;
		this.test = test;
		REPORT_DIR_NAME = "Reports" + File.separator + "Report_" + CommonUtil.getSimpleDateTimeFormat(System.currentTimeMillis());
		this.reportFile = createReportFile();
		this.testStatusFile = createTestStatusFile();
		this.diffImageDirectory = new File(this.reportFile.getParent()+File.separator+DIFF_IMAGE_DIR_NAME);
		testReport = new ArrayList<IReport>();
	}
	/**
	 * Constructor to be invoked during Layout testing.
	 * @param test the test label file/directory.
	 */
	public ReportHandler(File test) {
		this.test = test;
		REPORT_DIR_NAME = "Reports" + File.separator + "Report_" + CommonUtil.getSimpleDateTimeFormat(System.currentTimeMillis());
		this.reportFile = createReportFile();
		this.testStatusFile = createTestStatusFile();
		this.diffImageDirectory = new File(this.testStatusFile.getParent()+File.separator+DIFF_IMAGE_DIR_NAME);
		testReport = new ArrayList<IReport>();
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
	 * Add a report for each label that is compared.	
	 * @param report - the report object
	 */
	public abstract void addReport(IReport report);
	/**
	 * Generates a consolidated report.
	 */
	public File generateReport() {
		return getReportDirectory();
	};
	/**
	 * Creates the report file instance.
	 * @return
	 */
	private File createReportFile() {
		File reportDir = getReportDirectory();
		File reportFile = new File(reportDir.getAbsolutePath()+File.separator+REPORT_FILE_NAME);
		try {
			// delete the report file and create a new one every time the program runs.
			if(reportFile.exists()){
				reportFile.delete();
			}
			reportFile.createNewFile();
		} catch (IOException e) {
			logger.error(Messages.getMessage(CommonConstants.IO_EXCEPTION, "while creating file ", reportFile.getAbsolutePath(), e.getMessage()));
		}
		return reportFile;
	}
	/**
	 * Creates the test execution summary file instance.
	 * @return
	 */
	private File createTestStatusFile() {
		File reportDir = getReportDirectory();
		File testStatusFile = new File(reportDir.getAbsolutePath()+File.separator+TEST_STATUS_FILE_NAME);
		try {
			testStatusFile.createNewFile();
		} catch (IOException e) {
			logger.error(Messages.getMessage(CommonConstants.IO_EXCEPTION, "while creating file ", testStatusFile.getAbsolutePath(), e.getMessage()));
		}
		return testStatusFile;
	}
	/**
	 * Creates the report directory if it doesn't already exist.
	 * @return
	 */
	private File getReportDirectory(){
		File testLabelParentDir = test.getParentFile();
		File reportDir = new File(testLabelParentDir.getAbsolutePath() + File.separator + REPORT_DIR_NAME);
		if(!reportDir.exists()){
			reportDir.mkdirs();
		}
		return reportDir;
	}
	
	/**
	 * Creates the DiffImage directory, the directory that stores generated diff images if it doesn't already exist. 
	 * @return the diffImageDirectory
	 */
	public File getDiffImageDirectory() {
		if(!diffImageDirectory.exists()){
			diffImageDirectory.mkdir();
		}
		return diffImageDirectory;
	}
	
	/**
	 * Appends a string <code>message</code> to file <code>file</code>.
	 * @param file
	 * @param message
	 */
	protected void appendToFile(File file, String message) {
		try {
		    BufferedWriter out = new BufferedWriter(new FileWriter(file, true));
		    out.write(message);
		    out.close();
		} catch (IOException e) {
			logger.error(Messages.getMessage(CommonConstants.IO_EXCEPTION, "while writing to file ", file.getAbsolutePath(), e.getMessage()));
		}
	}
	
	protected void initExcelData(HSSFWorkbook workBook) {
		HSSFFont font = workBook.createFont();
		font.setFontName(HSSFFont.FONT_ARIAL);
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		
		boldHeaderFormat = workBook.createCellStyle();
		boldHeaderFormat.setFont(font);
		boldHeaderFormat.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
		boldHeaderFormat.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);  
		boldHeaderFormat.setBorderTop((short) 1); // single line border  
		boldHeaderFormat.setBorderBottom((short) 1); // single line border
		
		boldSummaryFormat = workBook.createCellStyle();
		boldHeaderFormat.setFont(font);
		
		failureCaseFormat = workBook.createCellStyle();
		failureCaseFormat.setFont(font);
		failureCaseFormat.setFillForegroundColor(HSSFColor.YELLOW.index);
		failureCaseFormat.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);  
		failureCaseFormat.setBorderTop((short) 1); // single line border  
		failureCaseFormat.setBorderBottom((short) 1); // single line border 
	}
}
