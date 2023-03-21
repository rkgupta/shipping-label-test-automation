package com.pb.testautomation.label.pdf;

import java.io.File;

import org.apache.log4j.Logger;
import org.apache.pdfbox.PDFToImage;
import org.apache.pdfbox.examples.util.PrintImageLocations;

import com.pb.testautomation.label.constant.CommonConstants;
import com.pb.testautomation.label.image.ImageType;
import com.pb.testautomation.label.util.CommonUtil;
import com.pb.testautomation.label.util.Messages;
import com.pb.testautomation.label.util.LabelUtil;
/**
 * Utility class to convert a PDF to an image.
 * @author RA013GU
 *
 */
public class PDF2ImageUtil {
	
	private static PDF2ImageUtil instance = new PDF2ImageUtil();
	private Logger logger = Logger.getLogger(PDF2ImageUtil.class);	
	
	
	/**
	 * Singleton
	 */
	private PDF2ImageUtil(){
		
	}
	/**
	 * Returns singleton instance of {@link PDF2ImageUtil}
	 * @return
	 */
	public static PDF2ImageUtil getInstance(){
		return instance;
	}
	/**
	 * converts pdf file <code>pdfFile</code> to image of type <code>imageType</code>.
	 * @param pdfFile
	 * @param imageType
	 */
	public void convertToImage(File pdfFile, ImageType imageType) {
		String pdfFileName = pdfFile.getName().substring(0, pdfFile.getName().lastIndexOf(LabelUtil.DOT));
		String outputPrefix = pdfFile.getParentFile().getAbsolutePath() + LabelUtil.RELATIVE_IMAGE_DIR 
								+ pdfFileName + CommonConstants.PDF_PAGE_NUM_TO_IMG_NUM_PREFIX;
		
		String [] args = {"-imageType",imageType.toString(),"-outputPrefix",outputPrefix, "-resolution", String.valueOf(LabelUtil.RESOLUTION_DPI), pdfFile.getAbsolutePath()};		
		try {
			PDFToImage.main(args);
		} catch (Exception e) {
			logger.error(Messages.getMessage(CommonConstants.PDF_TO_IMAGE_CONVERSION_EXCEPTION, pdfFile.getName(), e.getMessage()));
		}
	}
	
	public void getImageCoordinates(String pdfFile){
		String [] args = {pdfFile};
		try {
			PrintImageLocations.main(args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		try {
			long start = System.currentTimeMillis();
			PDF2ImageUtil.getInstance().convertToImage(new File("C:\\LabelDemo\\master\\2x7_add3_overlap.pdf"), ImageType.bmp);
			long end = System.currentTimeMillis();
			System.out.println("Execution time: "+CommonUtil.getExecutionTimeFormat((end-start)));
			//ImageUtil.getInstance().applyMask(778, 202, 973, 240, new File ("F:/WS Testing Framework/Label Comparer/PDFBox/images/output1.JPEG"));
			//PDF2ImageUtil.getInstance().getImageCoordinates("F:/WS Testing Framework/Label Comparer/PDFBox/output.pdf");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
