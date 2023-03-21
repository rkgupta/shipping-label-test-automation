package com.pb.testautomation.label.layout;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.pb.testautomation.label.constant.CommonConstants;
import com.pb.testautomation.label.image.ImageType;
import com.pb.testautomation.label.image.ImageUtil;
import com.pb.testautomation.label.layout.model.LabelSpec;
import com.pb.testautomation.label.layout.model.LabelTypeConfig;
import com.pb.testautomation.label.layout.model.Page;
import com.pb.testautomation.label.layout.model.Region;
import com.pb.testautomation.label.layout.model.Template;
import com.pb.testautomation.label.layout.report.LabelReport;
import com.pb.testautomation.label.layout.report.PageReport;
import com.pb.testautomation.label.layout.report.TextDiffReport;
import com.pb.testautomation.label.layout.report.VisualDiffReport;
import com.pb.testautomation.label.pdf.PDF2ImageUtil;
import com.pb.testautomation.label.util.CommonUtil;
import com.pb.testautomation.label.util.LabelUtil;
import com.pb.testautomation.label.util.MaskingUtil;
import com.pb.testautomation.label.util.Messages;

/**
 * This class is used to test layout of PDF labels.
 * @author RA013GU
 *
 */
public class PDFLayoutComparator extends LayoutComparator {
	
	private Logger logger = Logger.getLogger(PDFLayoutComparator.class);
	
	public PDFLayoutComparator(File test, File configFile) {
		super(test, configFile);
	}

	@Override
	protected void processLabels() {
		logger.info(Messages.getMessage(CommonConstants.INFO_CREATE_TEST_LABEL_IMAGE));
		if(test.isDirectory()) {
			try {
				FileUtils.cleanDirectory(testImageDirectory);
			} catch (IOException e) {
				logger.error(Messages.getMessage(CommonConstants.IO_EXCEPTION, "while deleting files from ", testImageDirectory.getAbsolutePath(), e.getMessage()));
			}
			File[] testLabels = test.listFiles();
			for(File testLabel : testLabels) {
				String fileName = testLabel.getName();				
				if(LabelUtil.PDF_EXTENSION.equalsIgnoreCase(CommonUtil.getExtension(fileName))) {					
					String templateType = fileName.split(LabelUtil.LABEL_TYPE_FILE_NAME_DELIMITER)[0];
					if(layoutConfigMappings.containsKey(templateType)) {
						PDF2ImageUtil.getInstance().convertToImage(testLabel, ImageType.jpeg);						
					}else{
						logger.info(Messages.getMessage(CommonConstants.INFO_NO_CONFIGURATION_FOUND_IN_CONFIG_FILE, testLabel.getAbsolutePath()));
					}
				}
			}
		}else {
			String fileName = test.getName();
			if(LabelUtil.PDF_EXTENSION.equalsIgnoreCase(CommonUtil.getExtension(fileName))) {					
				String templateType = fileName.split(LabelUtil.LABEL_TYPE_FILE_NAME_DELIMITER)[0];
				if(MaskingUtil.getInstance().getConfigMappings().containsKey(templateType)) {
					PDF2ImageUtil.getInstance().convertToImage(test, ImageType.jpeg);						
				} else{
					logger.info(Messages.getMessage(CommonConstants.INFO_NO_CONFIGURATION_FOUND_IN_CONFIG_FILE, test.getAbsolutePath()));
				}
			}
		}
	}

	@Override
	protected void compareLayout() {
		File[] testLabels = test.listFiles();
		
		String labelName = null;
		String labelNameWithoutExt = null;
		String templateType = null;
		LabelReport labelReport = null;
		PageReport pageReport = null;
		TextDiffReport textDiffReport = null;
		VisualDiffReport visualDiffReport = null;
		
		for(File testLabel : testLabels) {
			labelName = testLabel.getName();				
			if(LabelUtil.PDF_EXTENSION.equalsIgnoreCase(CommonUtil.getExtension(labelName))) {
				templateType = labelName.split(LabelUtil.LABEL_TYPE_FILE_NAME_DELIMITER)[0];
				logger.info(Messages.getMessage(CommonConstants.INFO_COMPARING_PDF_LABEL_LAYOUT, labelName));
				if(layoutConfigMappings.containsKey(templateType)) {
					try {
						long executionStartTime = System.currentTimeMillis();
						labelReport = new LabelReport(labelName);
						labelNameWithoutExt = labelName.substring(0, labelName.lastIndexOf(LabelUtil.DOT));
						
						LabelTypeConfig labelTypeConfig = layoutConfigMappings.get(templateType);
						Map<Integer, Page> pagesMap = labelTypeConfig.getPagesMap();
						
						float scale = LabelUtil.getInstance().getScalingFactorForPDF(labelTypeConfig.getUom());
						
						File[] pages = getPages(labelNameWithoutExt);
						File page = null;
						boolean isLayoutMatch = true;
						for(int curPageIndex = 0; curPageIndex < pages.length; curPageIndex++) {
							page = pages[curPageIndex];
							
							pageReport = new PageReport(Integer.toString(curPageIndex+1));
							textDiffReport = new TextDiffReport();
							visualDiffReport = new VisualDiffReport();
							
							Page curPage = pagesMap.get(curPageIndex + 1);											
							BufferedImage diffImage = null;
							if(curPage!= null){
								List<LabelSpec> labelSpecs = curPage.getLabelSpecs();
								BufferedImage image = ImageUtil.getInstance().loadImage(page);
								for(LabelSpec labelSpec: labelSpecs) {
									Template template = labelSpec.getTemplate();
									List<Region> regions = template.getRegions();
									List<Region> invalidLayoutRegions = new ArrayList<Region>();
									for(Region region: regions) {
										boolean isValidRegion = ImageUtil.getInstance().isLayoutMatch(image, region, labelSpec.getTemplateLeft(), labelSpec.getTemplateTop(), scale);
										if(!isValidRegion){
											textDiffReport.addInvalidLayoutRegion(region.getId());
											invalidLayoutRegions.add(region);
										}
										isLayoutMatch = isLayoutMatch && isValidRegion;
									}
									
									if(!invalidLayoutRegions.isEmpty()) {
										if(diffImage == null) {
											diffImage = ImageUtil.getInstance().loadImage(page);
										}
										diffImage = ImageUtil.getInstance().createLayoutDiffImage(diffImage, invalidLayoutRegions, labelSpec.getTemplateLeft(), labelSpec.getTemplateTop(), scale);
									}
								}
								if(isLayoutMatch) {
									pageReport.setPageMatch(true);
									pageReport.setStatusMessage(PageReport.LAYOUT_VALID_MESSAGE);
								} else {
									pageReport.setPageMatch(false);
									pageReport.setStatusMessage(PageReport.LAYOUT_INVALID_MESSAGE);
									pageReport.addReport(textDiffReport);
									
									String extn = CommonUtil.getExtension(page.getName());
									
									File diffImageFile = new File(reportHandler.getDiffImageDirectory().getAbsolutePath()+File.separator+ImageUtil.DIFF_IMAGE_PREFIX+page.getName());
									ImageUtil.getInstance().writeImage(diffImage, extn, diffImageFile);
									
									visualDiffReport = new VisualDiffReport();
									visualDiffReport.setDiffImageMessage("See the layout diff image @ "+diffImageFile.getAbsolutePath());
									pageReport.addReport(visualDiffReport);
								}
							}else{
								pageReport.setPageMatch(true);
								pageReport.setStatusMessage(PageReport.PAGE_SPEC_MISSING_IN_CONFIG_FILE);
							}
							labelReport.addReport(pageReport);
						}
						long executionEndTime = System.currentTimeMillis();
						labelReport.setExecutionTime(executionEndTime - executionStartTime);
						reportHandler.addReport(labelReport);
					} catch(Exception e) {
						logger.error(Messages.getMessage(CommonConstants.CHECKING_LAYOUT_EXCEPTION, testLabel.getAbsolutePath(), e.getMessage()));
					}
				} else {
					logger.info(Messages.getMessage(CommonConstants.INFO_NO_CONFIGURATION_FOUND_IN_CONFIG_FILE, testLabel.getAbsolutePath()));
				}
			}
		}
	}

}
