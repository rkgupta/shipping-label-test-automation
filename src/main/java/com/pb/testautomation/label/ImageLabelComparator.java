package com.pb.testautomation.label;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.pb.testautomation.label.constant.CommonConstants;
import com.pb.testautomation.label.image.ImageUtil;
import com.pb.testautomation.label.model.LabelSpec;
import com.pb.testautomation.label.model.LabelTypeConfig;
import com.pb.testautomation.label.model.Page;
import com.pb.testautomation.label.report.IReport;
import com.pb.testautomation.label.report.LabelReport;
import com.pb.testautomation.label.report.PageReport;
import com.pb.testautomation.label.report.VisualDiffReport;
import com.pb.testautomation.label.util.CommonUtil;
import com.pb.testautomation.label.util.Messages;
import com.pb.testautomation.label.util.LabelUtil;
import com.pb.testautomation.label.util.MD5Encoder;
import com.pb.testautomation.label.util.MaskingUtil;


/**
 * Image label comparator class. This class handles comparison of Image labels.
 * 
 * @author ta013ba
 */
public class ImageLabelComparator extends LabelComparator {
	
	private Logger logger = Logger.getLogger(ImageLabelComparator.class);
	
	public ImageLabelComparator(File master, File test, File configFile){
		super(master, test, configFile);
	}

	@Override
	void processMasterLabels() {
		if(isConfigFileModified || isMasterLabelModified) {
			applyMaskOnMaster();
			cacheMasterImageHashData();
		}
	}

	/*
	 *This method cleans the image directory and applies the mask on the images according to the config specs 
	 */
	private void applyMaskOnMaster() {
		try {
			FileUtils.cleanDirectory(getImageDirectory(master));
		} catch (IOException e) {
			logger.error(Messages.getMessage(CommonConstants.IO_EXCEPTION, "while deleting files from ", masterImageDirectory.getAbsolutePath(), e.getMessage()));
		}
		
		logger.info(Messages.getMessage(CommonConstants.INFO_MASTER_LABEL_IMAGE_MASKING));
		if(master.isDirectory()) {
			File[] masterLabels = master.listFiles();
			for(File masterLabel : masterLabels) {
				applyMaskOnMasterFile(masterLabel);
			}
		} else {
			applyMaskOnMasterFile(master);
		}
	}

	/*
	 * This method applies the mask on the supported master image files. 
	 * @param masterLabel
	 */
	private void applyMaskOnMasterFile(File masterLabel) {
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
	
	
	@Override
	void processTestLabels() {
		applyMaskOnTest();
	}

	/*
	 * This method applies the mask on the supported test image files, which are in common with the master files.
	 */
	private void applyMaskOnTest() {
		try {
			FileUtils.cleanDirectory(getImageDirectory(test));
		} catch (IOException e) {
			logger.error(Messages.getMessage(CommonConstants.IO_EXCEPTION, "while deleting files from ", testImageDirectory.getAbsolutePath(), e.getMessage()));
		}
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
				applyMaskOnTestFile(masterMap, file);
			}
		} else {
			
			Map<String, File> masterMap = new HashMap<String, File>();
			masterMap.put(master.getName(), master);
			
			applyMaskOnTestFile(masterMap, test);
		}
	}

	/*
	 * This method applies the mask on the test file, if the file with the same name is present in the masterMap
	 */
	private void applyMaskOnTestFile(Map<String, File> masterMap, File file) {
		String labelName = file.getName();
		if(file.isFile() && CommonUtil.isSupportedImageExtension(CommonUtil.getExtension(labelName))) {	//Check if the file is a valid image
			
			if(masterMap.containsKey(labelName)) {	//Check if the file is also present in test
				
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
	}

	/*
	 * This method applies the mask on the masterLabel according to the labelTypeConfig specs and moves the generated image into the images sub-directory.
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
	
	protected void compareLabels() {
		LabelReport labelReport = null;
		PageReport pageReport = null;
		
		for(Entry<String, File> entry : commonTestLabelsMap.entrySet()){
			long executionStartTime = System.currentTimeMillis();
			String labelName = entry.getKey();
			File testImageFile = entry.getValue();
			
			File maskedTestImageFile = new File(testImageFile.getAbsoluteFile().getParentFile() + LabelUtil.RELATIVE_IMAGE_DIR + labelName);
			
			labelReport = new LabelReport(maskedTestImageFile.getName());
		
			pageReport = new PageReport("1");
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
						
						//Clear all the pages report and let the corrupt message be printed for the label level. Not for the page level 
						labelReport.clearPageReport();
					} else {
						pageReport.addReport(imageReport);					
					}
				}					
			} else {	//Case where corrupt master imageis found, in which case masked image and hence hash is not generated.
				pageReport.setPageMatch(false);
				pageReport.setStatusMessage(PageReport.PAGE_MATCH_FAILURE_MESSAGE + LabelReport.MASTER_TEST_CORRUPT);
				
				//Clear all the pages report and let the corrupt message be printed for the label level. Not for the page level 
				labelReport.clearPageReport();
			}
			
			long executionEndTime = System.currentTimeMillis();
			labelReport.setExecutionTime(executionEndTime - executionStartTime);
			reportHandler.addReport(labelReport);
		}
	}
	
	/*
	 * Creates a visual comparison report by comparing the master and test images.
	 * It checks for orientation as well as any graphical difference.
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
			boolean isImageDiffFound = ImageUtil.getInstance().constructDiffImage(masterImage, testImage, diffImageFile);
			if(isImageDiffFound){
				visualDiffReport.setDiffImageMessage("See the Diff Image @ " + diffImageFile.getAbsolutePath());
			}			
			return visualDiffReport;
		} catch(Exception e) {
			logger.error(Messages.getMessage(CommonConstants.IMAGE_COMPARISON_REPORT_EXCEPTION, testImage.getName()));
		}
		return null;
	}
}
