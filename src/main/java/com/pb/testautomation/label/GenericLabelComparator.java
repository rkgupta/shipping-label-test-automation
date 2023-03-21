package com.pb.testautomation.label;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.pb.testautomation.label.constant.CommonConstants;
import com.pb.testautomation.label.image.ImageType;
import com.pb.testautomation.label.image.ImageUtil;
import com.pb.testautomation.label.model.LabelSpec;
import com.pb.testautomation.label.model.LabelTypeConfig;
import com.pb.testautomation.label.model.Page;
import com.pb.testautomation.label.pdf.PDF2ImageUtil;
import com.pb.testautomation.label.report.IReport;
import com.pb.testautomation.label.report.LabelReport;
import com.pb.testautomation.label.report.PageReport;
import com.pb.testautomation.label.report.TextDiffReport;
import com.pb.testautomation.label.report.VisualDiffReport;
import com.pb.testautomation.label.text.TextExtractor;
import com.pb.testautomation.label.text.diff_match_patch.Diff;
import com.pb.testautomation.label.text.diff_match_patch.Operation;
import com.pb.testautomation.label.util.CommonUtil;
import com.pb.testautomation.label.util.Messages;
import com.pb.testautomation.label.util.LabelUtil;
import com.pb.testautomation.label.util.MD5Encoder;
import com.pb.testautomation.label.util.MaskingUtil;
/**
 * Comparator class that handles comparison of labels of mixed type i.e. both PDF and Image.
 * @author RA013GU
 *
 */
public class GenericLabelComparator extends LabelComparator {
	
	private Logger logger = Logger.getLogger(GenericLabelComparator.class);

	public GenericLabelComparator(File master, File test, File configFile) {
		super(master, test, configFile);
	}
	

	@Override
	void processMasterLabels() {
		if(isConfigFileModified || isMasterLabelModified) {
			processGenericMasterLabels();
			cacheMasterImageHashData();
		}
	}

	private void processGenericMasterLabels() {
		logger.info(Messages.getMessage(CommonConstants.INFO_PROCESS_MASTER_LABEL));
		if(master.isDirectory()) {
			try {
				FileUtils.cleanDirectory(masterImageDirectory);
			} catch (IOException e) {
				logger.error(Messages.getMessage(CommonConstants.IO_EXCEPTION, "while deleting files from ", masterImageDirectory.getAbsolutePath(), e.getMessage()));
			}
			File[] masterLabels = master.listFiles();
			for(File masterLabel : masterLabels) {
				String extension = CommonUtil.getExtension(masterLabel.getName());
				if(LabelUtil.PDF_EXTENSION.equalsIgnoreCase(extension)) {
					String templateType = masterLabel.getName().split(LabelUtil.LABEL_TYPE_FILE_NAME_DELIMITER)[0];
					if(MaskingUtil.getInstance().getConfigMappings().containsKey(templateType)) {
						PDF2ImageUtil.getInstance().convertToImage(masterLabel, ImageType.jpeg);						
					}else{
						logger.info(Messages.getMessage(CommonConstants.INFO_NO_CONFIGURATION_FOUND_IN_CONFIG_FILE, masterLabel.getAbsolutePath()));
					}
				}else if(CommonUtil.isSupportedImageExtension(extension)) {
						applyMaskOnMasterImage(masterLabel);
				}
			}
		} else {
			String extension = CommonUtil.getExtension(master.getName());
			if(LabelUtil.PDF_EXTENSION.equalsIgnoreCase(extension)) {					
				String templateType = master.getName().split(LabelUtil.LABEL_TYPE_FILE_NAME_DELIMITER)[0];
				if(MaskingUtil.getInstance().getConfigMappings().containsKey(templateType)) {
					PDF2ImageUtil.getInstance().convertToImage(master, ImageType.jpeg);						
				} else{
					logger.info(Messages.getMessage(CommonConstants.INFO_NO_CONFIGURATION_FOUND_IN_CONFIG_FILE, master.getAbsolutePath()));
				}
			}else if(CommonUtil.isSupportedImageExtension(extension)) {
				applyMaskOnMasterImage(master);
			}
		}
	}

	private void applyMaskOnMasterImage(File masterLabel) {
		String fileName = masterLabel.getName();
		String extension = CommonUtil.getExtension(fileName);
		
		if(CommonUtil.isSupportedImageExtension(extension)) {
			String templateType = fileName.split(LabelUtil.LABEL_TYPE_FILE_NAME_DELIMITER)[0];
			LabelTypeConfig labelTypeConfig = MaskingUtil.getInstance().getConfigMappings().get(templateType);
			if(labelTypeConfig != null) {
				maskAndMoveToImgDir(masterLabel, labelTypeConfig);
			} else {
				logger.info(Messages.getMessage(CommonConstants.INFO_NO_CONFIGURATION_FOUND_IN_CONFIG_FILE, masterLabel.getAbsolutePath()));
			}
		}
	}
	/**
	 * Applies mask on the image and moves it to image directory.
	 * @param masterLabel
	 * @param labelTypeConfig
	 */
	private void maskAndMoveToImgDir(File masterLabel, LabelTypeConfig labelTypeConfig) {
		String fileName = masterLabel.getName();

		File imageDir = getImageDirectory(masterLabel);
		File destImageFile = new File(imageDir.getAbsolutePath() + File.separator + fileName);
		
		String []fileNameParts = fileName.split(LabelUtil.LABEL_TYPE_FILE_NAME_DELIMITER);
		
		int equivPageNum = 1;
		if(fileNameParts.length > 2) {
			try {
				equivPageNum = Integer.parseInt(fileNameParts[1]);				
			} catch(NumberFormatException e) {
				logger.warn(Messages.getMessage(CommonConstants.NUMBER_FORMAT_EXCEPTION, e.getMessage()) + "Taking the default page number i.e. 1");
			}
		}

		Map<Integer, Page> pagesMap = labelTypeConfig.getPagesMap();
		if(pagesMap != null) {
			Page page = pagesMap.get(equivPageNum);
			
			if(page != null) {
				List<LabelSpec> labelSpecs = page.getLabelSpecs();
				
				try {
					ImageUtil.getInstance().applyMaskForImage(masterLabel, destImageFile, labelSpecs, labelTypeConfig.getUom(), labelTypeConfig.getResolution());
				} catch (Exception e) {
					logger.error(Messages.getMessage(CommonConstants.MASKING_EXCEPTION, masterLabel.getAbsolutePath(), e.getMessage()));
				}
			}
		}
	}

	@Override
	void processTestLabels() {
		processPDFTestLabels();
		processImageTestLabels();
	}
	/**
	 *  Process the Image test labels.
	 */
	private void processImageTestLabels() {
		logger.info(Messages.getMessage(CommonConstants.INFO_TEST_LABEL_IMAGE_MASKING));
		if(test.isDirectory()) {
			File[] masterFiles = master.listFiles();
			File[] testFiles = test.listFiles();
			
			Map<String, File> masterMap = new HashMap<String, File>();			
			
			for(File file : masterFiles) {
				if(file.isFile() && CommonUtil.isSupportedImageExtension(CommonUtil.getExtension(file.getName()))){
					masterMap.put(file.getName(), file);
				}						
			}
			masterFiles = null;
			
			for(File file: testFiles) {
				if(masterMap.containsKey(file.getName())) {
					applyMaskOnTestImage(file); 
				}
			}
		} else {
			if(master.getName().equals(test.getName())&& CommonUtil.isSupportedImageExtension(CommonUtil.getExtension(test.getName()))) {
				applyMaskOnTestImage(test);
			}
		}
		
	}

	/**
	 *  Applies mask on test image.
	 * @param masterMap
	 * @param file
	 */
	private void applyMaskOnTestImage(File file) {
		String labelName = file.getName();
		if(file.isFile() && CommonUtil.isSupportedImageExtension(CommonUtil.getExtension(labelName))) {	//Check if the file is a valid image
			String templateType = labelName.split(LabelUtil.LABEL_TYPE_FILE_NAME_DELIMITER)[0];
			LabelTypeConfig labelTypeConfig = MaskingUtil.getInstance().getConfigMappings().get(templateType);
			if(labelTypeConfig != null) {	//Check if the configuration is present
				commonTestLabelsMap.put(labelName, file);	//Add to the common map.
				maskAndMoveToImgDir(file, labelTypeConfig);
			} else {
				logger.info(Messages.getMessage(CommonConstants.INFO_NO_CONFIGURATION_FOUND_IN_CONFIG_FILE, file.getAbsolutePath()));
			}
		}
	}
	/**
	 * Process the PDF test labels.
	 */
	private void processPDFTestLabels() {
		createTestImages();
	}
	/**
	 * Creates images for test labels.
	 * @param test
	 */
	private void createTestImages() {
		logger.info(Messages.getMessage(CommonConstants.INFO_CREATE_TEST_LABEL_IMAGE));
		if(test.isDirectory()) {
			try {
				FileUtils.cleanDirectory(testImageDirectory);
			} catch (IOException e) {
				logger.error(Messages.getMessage(CommonConstants.IO_EXCEPTION, "while deleting files from ", testImageDirectory.getAbsolutePath(), e.getMessage()));
			}			
			
			File[] masterFiles = master.listFiles();
			File[] testFiles = test.listFiles();
			
			Map<String, File> masterMap = new HashMap<String, File>();			
			Map<String, File> testMap = new HashMap<String, File>();
			
			for(File file : masterFiles){
				if(file.isFile() && LabelUtil.PDF_EXTENSION.equalsIgnoreCase(CommonUtil.getExtension(file.getName()))){
					masterMap.put(file.getName(), file);
				}						
			}
			for(File file: testFiles){
				if(file.isFile() && LabelUtil.PDF_EXTENSION.equalsIgnoreCase(CommonUtil.getExtension(file.getName()))){
					testMap.put(file.getName(), file);
				}
			}
			
			testMap.keySet().retainAll(masterMap.keySet());
			
			for(String commonKey : testMap.keySet()) {
				String labelName = commonKey;
				String templateType = labelName.split(LabelUtil.LABEL_TYPE_FILE_NAME_DELIMITER)[0];
				if(MaskingUtil.getInstance().getConfigMappings().containsKey(templateType)) {
					PDF2ImageUtil.getInstance().convertToImage(testMap.get(commonKey), ImageType.jpeg);		
					commonTestLabelsMap.put(commonKey, testMap.get(commonKey));
				} else {
					logger.info(Messages.getMessage(CommonConstants.INFO_NO_CONFIGURATION_FOUND_IN_CONFIG_FILE, testMap.get(commonKey).getAbsolutePath()));
				}
			}
			masterMap = null;
			testMap = null;
		} else {
			commonTestLabelsMap.put(test.getName(), test);
			PDF2ImageUtil.getInstance().convertToImage(test, ImageType.jpeg);
		}
	}

	@Override
	void compareLabels() {		
		for(Entry<String, File> entry : commonTestLabelsMap.entrySet()) {
			try {
				if(CommonUtil.getLabelType(entry.getKey()).equals(LabelType.PDF.toString())){
					comparePDFLabels(entry);
				}else{
					compareImageLabels(entry);
				}				
			} catch(Exception e) {
				logger.error(Messages.getMessage(CommonConstants.LABEL_COMPARISON_EXCEPTION, entry.getKey()));
			}
		}
	
	}
	/**
	 * Compares the Image labels.
	 * @param entry - Entry in the common test labels map.
	 */
	private void compareImageLabels(Entry<String, File> entry) {
		long executionStartTime = System.currentTimeMillis();
		
		String labelName = entry.getKey();
		File testImageFile = entry.getValue();
		
		File maskedTestImageFile = new File(testImageFile.getAbsoluteFile().getParentFile() + LabelUtil.RELATIVE_IMAGE_DIR + labelName);
		
		LabelReport labelReport = new LabelReport(maskedTestImageFile.getName());	
		PageReport pageReport = new PageReport("1");
		
		labelReport.addReport(pageReport);
		
		String masterLabelDataHash = masterLabelDataCache.get(labelName);
		if(masterLabelDataHash != null) {
			logger.info(Messages.getMessage(CommonConstants.INFO_COMPARING_TEST_LABEL_WITH_MASTER, testImageFile.getName()));					
			if(masterLabelDataHash.equals(MD5Encoder.encode(ImageUtil.getInstance().getBytes(maskedTestImageFile)))){
				logger.info(Messages.getMessage(CommonConstants.INFO_IMAGES_MATCH));
				pageReport.setPageMatch(true);
				pageReport.setStatusMessage(PageReport.PAGE_MATCH_SUCCESS_MESSAGE);
			} else {						
				logger.info(Messages.getMessage(CommonConstants.INFO_IMAGES_MISMATCH));
				pageReport.setPageMatch(false);
				pageReport.setStatusMessage(PageReport.PAGE_MATCH_FAILURE_MESSAGE);
				
				IReport imageReport = getImageComparisonReport(maskedTestImageFile);
				if(imageReport == null) {
					pageReport.setStatusMessage(PageReport.PAGE_MATCH_FAILURE_MESSAGE + LabelReport.MASTER_TEST_CORRUPT);
					//Clear all the pages report and let the corrupt message be printed for the pdf level. Not for the page level 
					labelReport.clearPageReport();
				} else {
					pageReport.addReport(imageReport);					
				}
			}					
		} else {
			//Case where corrupt master image is found, in which case masked image and hence hash is not generated. 
			pageReport.setPageMatch(false);
			pageReport.setStatusMessage(PageReport.PAGE_MATCH_FAILURE_MESSAGE + LabelReport.MASTER_TEST_CORRUPT);
			//Clear all the pages report and let the corrupt message be printed for the pdf level. Not for the page level 
			labelReport.clearPageReport();
		}
		
		long executionEndTime = System.currentTimeMillis();
		labelReport.setExecutionTime(executionEndTime - executionStartTime);
		reportHandler.addReport(labelReport);
	}

	/**
	 * Compares the PDF labels.
	 * @param entry - Entry in the common test labels map.
	 */
	private void comparePDFLabels(Entry<String, File> entry) {
		long executionStartTime = System.currentTimeMillis();
		
		String labelName 			= entry.getKey();
		String labelNameWithoutExt 	= labelName.substring(0, labelName.lastIndexOf(LabelUtil.DOT));
		
		File testPdfFile 			= entry.getValue();			
		File masterPdfFile 			= new File(master.getAbsolutePath() + File.separator + testPdfFile.getName());
		
		LabelReport labelReport 	= new LabelReport(testPdfFile.getName());
		PageReport pageReport = null;
	
		File[] pages = getPages(labelNameWithoutExt);
		File page = null;
		for(int curPageIndex = 0; curPageIndex < pages.length; curPageIndex++) {
			page = pages[curPageIndex];
			pageReport = new PageReport(Integer.toString(curPageIndex+1));
			String masterLabelDataHash = masterLabelDataCache.get(page.getName());
			
			if(masterLabelDataHash != null) {
				logger.info(Messages.getMessage(CommonConstants.INFO_COMPARING_TEST_LABEL_WITH_MASTER, page.getName()));					
				if(masterLabelDataHash.equals(MD5Encoder.encode(ImageUtil.getInstance().getBytes(page)))){
					logger.info(Messages.getMessage(CommonConstants.INFO_IMAGES_MATCH));
					pageReport.setPageMatch(true);
					pageReport.setStatusMessage(PageReport.PAGE_MATCH_SUCCESS_MESSAGE);
				} else {						
					logger.info(Messages.getMessage(CommonConstants.INFO_IMAGES_MISMATCH));
					pageReport.setPageMatch(false);
					
					IReport textReport = getTextComparisonReport(masterPdfFile, testPdfFile, curPageIndex, labelName);
					IReport imageReport = getImageComparisonReport(page);
					pageReport.setStatusMessage(PageReport.PAGE_MATCH_FAILURE_MESSAGE);
					
					if(textReport == null && imageReport == null) {
						pageReport.setStatusMessage(PageReport.PAGE_MATCH_FAILURE_MESSAGE + LabelReport.MASTER_TEST_CORRUPT);
						
						//Clear all the pages report and let the corrupt message be printed for the pdf level. Not for the page level 
						labelReport.clearPageReport();
						break;
					} else {
						pageReport.addReport(textReport);	
						pageReport.addReport(imageReport);																				
					}
				}					
			} else {	//Case where corrupt master pdf is found, in which case image and hence hash is not generated.
				pageReport.setPageMatch(false);
				pageReport.setStatusMessage(PageReport.PAGE_MATCH_FAILURE_MESSAGE + LabelReport.MASTER_TEST_CORRUPT);
				
				//Clear all the pages report and let the corrupt message be printed for the pdf level. Not for the page level.
				labelReport.clearPageReport();
				break;
			}
			labelReport.addReport(pageReport);
		}
		
		long executionEndTime = System.currentTimeMillis();
		labelReport.setExecutionTime(executionEndTime - executionStartTime);
		reportHandler.addReport(labelReport);
	}
	/**
	 * Compares text in the master and test pdf files and returns the text diff report object.
	 * @param masterPdfFile
	 * @param testPdfFile
	 * @param curPage
	 * @param labelName
	 * @return
	 */
	private IReport getTextComparisonReport(File masterPdfFile, File testPdfFile, int curPageIndex, String labelName) {
		String templateType = labelName.split(LabelUtil.LABEL_TYPE_FILE_NAME_DELIMITER)[0];
		LabelTypeConfig labelTypeConfig = MaskingUtil.getInstance().getConfigMappings().get(templateType);

		Map<Integer, Page> pagesMap = labelTypeConfig.getPagesMap();
		
		if (pagesMap != null && pagesMap.get(curPageIndex + 1) != null) {
			Page curPdfPage = pagesMap.get(curPageIndex + 1);

			try {
				LinkedList<Diff> textDifferences = TextExtractor.matchText(masterPdfFile, testPdfFile, curPdfPage.getLabelSpecs(),
						curPageIndex, labelTypeConfig.getUom());
				
				TextDiffReport report = new TextDiffReport();
				if(textDifferences != null) {
					for(Diff diff : textDifferences) {
						if(diff.operation.equals(Operation.DELETE)) {
							report.addMissingText(diff.text);
						} else if(diff.operation.equals(Operation.INSERT)) {
							report.addAdditionalText(diff.text);
						}
					}
				}
				return report;
			} catch (Exception e) {
				logger.error(Messages.getMessage(CommonConstants.TEXT_COMPARISON_REPORT_EXCEPTION, e.getMessage()));
			}

		}
		return null;
	}
	
	/**
	 * Creates a visual comparison report by comparing the master and test images.
	 * It checks for orientation as well as any graphical difference.
	 * @param page
	 * @return
	 */
	private IReport getImageComparisonReport(File testImage) {
		try {
			VisualDiffReport visualDiffReport = new VisualDiffReport();
			File masterImage = new File(masterImageDirectory.getAbsolutePath()+File.separator+testImage.getName());
			//compare Orientation
			if(!ImageUtil.getInstance().getOrientation(masterImage).equals(ImageUtil.getInstance().getOrientation(testImage))){
				visualDiffReport.setOrientationMessage(ImageUtil.ORIENTATION_MISMATCH_MESSAGE);
			}		
			//compare Image		
			File diffImageFile = new File(reportHandler.getDiffImageDirectory().getAbsolutePath()
					+File.separator+ImageUtil.DIFF_IMAGE_PREFIX+testImage.getName());
			boolean isImageDiffFound  = ImageUtil.getInstance().constructDiffImage(masterImage, testImage, diffImageFile);
			if(isImageDiffFound){
				visualDiffReport.setDiffImageMessage("See the Diff Image @ " + diffImageFile.getAbsolutePath());
			}
			return visualDiffReport;			
		} catch(Exception e) {
			logger.error(Messages.getMessage(CommonConstants.IMAGE_COMPARISON_REPORT_EXCEPTION, testImage.getName()));
		}
		return null;
	}


	/**
	 * Returns all the images from the image directory which matches the given label name.
	 * @param labelName
	 * @return
	 */
	private File[] getPages(final String labelName){
		return testImageDirectory.listFiles(new FilenameFilter() {			
			public boolean accept(File dir, String name) {
				Pattern pattern = Pattern.compile(labelName + CommonConstants.PDF_PAGE_NUM_TO_IMG_NUM_PREFIX + "\\d{1,}." + ImageType.jpeg);
				Matcher matcher = pattern.matcher(name);
				return matcher.find();
			}
		});
	}

}
