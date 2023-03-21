package com.pb.testautomation.label.layout;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.pb.testautomation.label.constant.CommonConstants;
import com.pb.testautomation.label.exception.LabelException;
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
import com.pb.testautomation.label.util.CommonUtil;
import com.pb.testautomation.label.util.LabelUtil;
import com.pb.testautomation.label.util.Messages;

public class ImageLayoutComparator extends LayoutComparator {
	private Logger logger = Logger.getLogger(ImageLayoutComparator.class);
	
	public ImageLayoutComparator(File test, File configFile) {
		super(test, configFile);
	}

	@Override
	protected void processLabels() {
		// Do nothing here as the image labels do not need any processing.
	}

	@Override
	protected void compareLayout() {
		File[] testLabels = test.listFiles();
		
		String labelName = null;
		String templateType = null;
		LabelReport labelReport = null;
		PageReport pageReport = null;
		TextDiffReport textDiffReport = null;
		VisualDiffReport visualDiffReport = null;
		
		for(File testLabel : testLabels) {
			labelName = testLabel.getName();
			String extension = CommonUtil.getExtension(labelName);
			
			if(CommonUtil.isSupportedImageExtension(extension)) {
				logger.info(Messages.getMessage(CommonConstants.INFO_COMPARING_IMAGE_LABEL_LAYOUT, labelName));
				String []fileNameParts = labelName.split(LabelUtil.LABEL_TYPE_FILE_NAME_DELIMITER);
				templateType = fileNameParts[0];
				boolean isLayoutMatch = true;
				if(layoutConfigMappings.containsKey(templateType)) {
					try {
						long executionStartTime = System.currentTimeMillis();
						int equivPageNum = 1;
						if(fileNameParts.length > 2) {
							try {
								equivPageNum = Integer.parseInt(fileNameParts[1]);				
							} catch(NumberFormatException e) {
								//Do nothing here
							}
						}
						labelReport = new LabelReport(labelName);
						pageReport = new PageReport(Integer.toString(equivPageNum));
						textDiffReport = new TextDiffReport();
						visualDiffReport = new VisualDiffReport();
						
						LabelTypeConfig labelTypeConfig = layoutConfigMappings.get(templateType);
						Map<Integer, Page> pagesMap = labelTypeConfig.getPagesMap();
						
						Page curPage = pagesMap.get(equivPageNum);
						BufferedImage diffImage = null;
						if(curPage!=null){
							List<LabelSpec> labelSpecs = curPage.getLabelSpecs();
							
							float scale = LabelUtil.getInstance().getScaleFactorForImage(labelTypeConfig.getUom(), labelTypeConfig.getResolution());
							
							ImageUtil imageUtil = ImageUtil.getInstance();
							BufferedImage originalImage = imageUtil.loadImage(testLabel);
							
							
							for(LabelSpec labelSpec: labelSpecs) {
								Template template = labelSpec.getTemplate();
								List<Region> regions = template.getRegions();
								List<Region> invalidLayoutRegions = new ArrayList<Region>();
								
								for(Region region: regions) {
									boolean isValidRegion = imageUtil.isLayoutMatch(originalImage, region, labelSpec.getTemplateLeft(), labelSpec.getTemplateTop(), scale);
									if(!isValidRegion){
										textDiffReport.addInvalidLayoutRegion(region.getId());
										invalidLayoutRegions.add(region);
									}
									isLayoutMatch = isLayoutMatch && isValidRegion;
								}
								if(!invalidLayoutRegions.isEmpty()) {
									if(diffImage == null) {
										diffImage = imageUtil.loadImage(testLabel);
									}
									diffImage = imageUtil.createLayoutDiffImage(diffImage, invalidLayoutRegions, labelSpec.getTemplateLeft(), labelSpec.getTemplateTop(), scale);
								}
							}
							
							if(isLayoutMatch) {
								pageReport.setPageMatch(true);
								pageReport.setStatusMessage(PageReport.LAYOUT_VALID_MESSAGE);
							} else {
								pageReport.setPageMatch(false);
								pageReport.setStatusMessage(PageReport.LAYOUT_INVALID_MESSAGE);
								pageReport.addReport(textDiffReport);
								
								String extn = CommonUtil.getExtension(labelName);
								File diffImageFile = new File(reportHandler.getDiffImageDirectory().getAbsolutePath()+File.separator+ImageUtil.DIFF_IMAGE_PREFIX+labelName);
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
