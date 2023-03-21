package com.pb.testautomation.label.text;


import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import com.pb.testautomation.label.constant.CommonConstants;
import com.pb.testautomation.label.exception.LabelException;
import com.pb.testautomation.label.exception.UnknownConversionUnitException;
import com.pb.testautomation.label.model.LabelSpec;
import com.pb.testautomation.label.model.MaskableRegion;
import com.pb.testautomation.label.model.Template;
import com.pb.testautomation.label.pdf.PDFTextStripperByRegion;
import com.pb.testautomation.label.text.diff_match_patch.Diff;
import com.pb.testautomation.label.util.CommonUtil;

/**
 * Utility class to extract and match the text from the pdf files.
 * 
 * @author ta013ba
 */
public class TextExtractor {
	private static final String PDF_HEADER_REGEX = "@start .* @end\r\n";
	private static final String CARRIAGE_RETURN = "\r\n";
	
	/**
	 * This method extracts the text from <code>pageIndex</code> page of the pdf and compares for the similarity b/w master and test pdf(leaving the maskable regions) 
	 * @param masterFile	Master file to test against
	 * @param testFile		Test file which is to be tested
	 * @param labelSpecs	Label specs which defines the maskable regions
	 * @param pageIndex		0 based page Index, representing the page of the pdf for which we want to mask the regions
	 * @param uom			Unit of measurement
	 * @return				Returns the linked list of the differences b/w the master and test pdf
	 *  
	 * @throws LabelException, IOException
	 */
	public static LinkedList<Diff> matchText(File masterFile, File testFile, List<LabelSpec> labelSpecs, int pageIndex, String uom) throws LabelException, IOException {
		if(masterFile != null && testFile != null && masterFile.exists() && testFile.exists() 
				&& masterFile.isFile() && testFile.isFile()) {
				
			PDDocument masterDoc = PDDocument.load(masterFile);
			PDDocument testDoc = PDDocument.load(testFile);
			
			List<PDPage> masterPages = masterDoc.getDocumentCatalog().getAllPages();
			List<PDPage> testPages = testDoc.getDocumentCatalog().getAllPages();
			
			if(masterPages.size() != testPages.size()) {
				throw new LabelException("Number of pages in master: "+ masterFile.getName() + " doesn't match with that in test: "+ testFile.getName() + ". So, Skipping the files");
			}
			
			PDFTextStripperByRegion textStripperByRegion = new PDFTextStripperByRegion();
            textStripperByRegion.setSortByPosition(true);
            textStripperByRegion.setWordSeparator("");
            textStripperByRegion.setStartPage(pageIndex + 1);
            textStripperByRegion.setEndPage(pageIndex + 1);
            
            //Add the regions to escape and handle unit conversion
            formatMaskableRegions(labelSpecs, textStripperByRegion, uom);
            
            //Extract the text from pdf
            String masterPageText = textStripperByRegion.getText(masterDoc);
            String testPageText = textStripperByRegion.getText(testDoc);
            
            //Remove the headers
            masterPageText = masterPageText.replaceAll(PDF_HEADER_REGEX, "");
            testPageText = testPageText.replaceAll(PDF_HEADER_REGEX, "");
            
            //Compare the text
            diff_match_patch diffMatchPatch = new diff_match_patch();
            LinkedList<Diff> diffsList = diffMatchPatch.diff_main(masterPageText, testPageText, true);
            diffMatchPatch.diff_cleanupSemantic(diffsList);
            
            masterDoc.close();
            testDoc.close();
            
            return diffsList;
		} else if(masterFile == null || !masterFile.exists()) {
			throw new LabelException("Master file: "+ masterFile.getAbsolutePath() + " doesnt't exist");
		} else if(testFile == null || !testFile.exists()) {
			throw new LabelException("Test file: "+ testFile.getAbsolutePath() + " doesnt't exist");
		}
		
		return null;
	}

	/*
	 * This method formats the values of the maskable region taking into consideration the unit of measurement
	 */
	private static List<String> formatMaskableRegions(List<LabelSpec> labelSpecs, PDFTextStripperByRegion masterStripperByArea, String uom) throws UnknownConversionUnitException {
		float scale = getScale(uom);
		List<String> regionIds = new ArrayList<String>();
		
		for(int spec = 0; spec < labelSpecs.size(); spec++) {
			LabelSpec labelSpec = labelSpecs.get(spec);
			
			Template template = labelSpec.getTemplate();
			
			if(template != null) {
				List<MaskableRegion> maskableRegions = template.getMaskableRegions(); 
				for(int curMaskRegion = 0; curMaskRegion < maskableRegions.size(); curMaskRegion++) {
					MaskableRegion region = maskableRegions.get(curMaskRegion);
					
					if(CommonConstants.MASKABLE_REGION_TYPE_TEXT.equalsIgnoreCase(region.getRegionType())) {
						float left = scale * (labelSpec.getTemplateLeft() + region.getLeft());
						float top = scale * (labelSpec.getTemplateTop() + region.getTop());
						float width = scale * region.getWidth();
						float height = scale * region.getHeight();
						
						Rectangle rect = new Rectangle((int)left, (int)(top), (int)Math.ceil(width+1), (int)Math.ceil(height+1));
						
						String regionId = "region_" + spec + "_" + curMaskRegion;
						
						masterStripperByArea.addEscapeRegion(regionId, rect);
						
						regionIds.add(regionId);
					}
				}
			}
		}
		return regionIds;
	}

	/*
	 * This method returns the scale value to be used for scaling of the coordinates.
	 */
	private static float getScale(String uom) throws UnknownConversionUnitException {
		float scale = 72.0f;
		if(CommonConstants.UOM_CM.equals(uom)) {
			scale *= CommonConstants.CONVERSION_FACTOR_CM_TO_INCH;
		} else if(CommonConstants.UOM_INCH.equals(uom)) {
			scale *= 1.0f;
		} else if(CommonUtil.isEmpty(uom)) {
			scale *= 1.0f;	//Default is Inch
		} else {
			throw new UnknownConversionUnitException("Conversion unit " + uom + " is unknown");
		}
		return scale;
	}
}
