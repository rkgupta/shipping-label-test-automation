package com.pb.testautomation.label.image;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.pb.testautomation.label.Orientation;
import com.pb.testautomation.label.constant.CommonConstants;
import com.pb.testautomation.label.exception.LabelException;
import com.pb.testautomation.label.layout.model.Region;
import com.pb.testautomation.label.model.LabelSpec;
import com.pb.testautomation.label.model.MaskableRegion;
import com.pb.testautomation.label.model.Template;
import com.pb.testautomation.label.util.CommonUtil;
import com.pb.testautomation.label.util.LabelUtil;
import com.pb.testautomation.label.util.Messages;


/**
 * Utility class for image. Provides methods for image manipulation.
 * @author RA013GU
 *
 */
public class ImageUtil {
	
	private static ImageUtil instance = new ImageUtil();
	public Logger logger = Logger.getLogger(ImageUtil.class);
	
	public static final String DIFF_IMAGE_PREFIX = "diff-";
	public static final String ORIENTATION_MISMATCH_MESSAGE = "Orientaion does not match";
	public static final Color IMAGE_DIFF_HIGHLIGHT_COLOR = Color.RED;
	/**
	 *  Singleton.
	 */
	private ImageUtil(){
		
	}
	
	public static ImageUtil getInstance(){
		return instance;
	}
		
	/**
	 * Loads the image with given filename.
	 * @param file - the image file.
	 * @return BufferedImage instance of the file.
	 */
	public BufferedImage loadImage(File file) {
		BufferedImage buffImg = null;
		try {
			buffImg = ImageIO.read(file);
		} catch (IOException e) {
			logger.error(Messages.getMessage(CommonConstants.IO_EXCEPTION, "while reading from file ", file.getAbsolutePath(), e.getMessage()));
		}
		return buffImg;
	}
	/**
	 * Writes an image to the given file location
	 * @param img - BufferedImage to write.
	 * @param extn - The filetype.
	 * @param file - The file on which to write.
	 */
	public boolean writeImage(BufferedImage img, String extn, File file){
		boolean writeSuccess = false;
		try {
			writeSuccess = ImageIO.write(img, extn, file);
		} catch (IOException e) {
			logger.error(Messages.getMessage(CommonConstants.IO_EXCEPTION, "while writing to file ", file.getAbsolutePath(), e.getMessage()));
		}
		return writeSuccess;
	}
	/**
	 * rotates given image <code>img</code> to angle <code>angle</code>.
	 * @param img
	 * @param angle
	 * @return
	 */
	public BufferedImage rotate(BufferedImage img, int angle) {  
        int w = img.getWidth();  
        int h = img.getHeight();  
        BufferedImage dimg = new BufferedImage(w, h, img.getType());  
        Graphics2D g = dimg.createGraphics();  
        g.rotate(Math.toRadians(angle), w/2, h/2);  
        g.drawImage(img, null, 0, 0);  
        return dimg;  
    }
	/**
	 * resizes given image <code>img</code> to given width <code>newW</code> and height <code>newH</code>
	 * @param img
	 * @param newW
	 * @param newH
	 * @return
	 */
	public BufferedImage resize(BufferedImage img, int newW, int newH) {
		int w = img.getWidth();
		int h = img.getHeight();
		BufferedImage dimg = new BufferedImage(newW, newH, img.getType());
		Graphics2D g = dimg.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.drawImage(img, 0, 0, newW, newH, 0, 0, w, h, null);
		g.dispose();
		return dimg;
	}
	/**
	 * Returns the orientation of an image file <code>img</code>.
	 */
	public Orientation getOrientation(File img){
		Orientation orientation = null;
		BufferedImage bimg = loadImage(img);
		if(bimg.getWidth() > bimg.getHeight()){
			orientation = Orientation.LANDSCAPE;
		}else{
			orientation = Orientation.PORTRAIT;
		}
		return orientation;
	}
	
	/**
	 * XOR two images <code>img1</code> and <code>img2</code> and create a diff image <code>diffImage</code>.
	 * @param img1
	 * @param img2
	 * @param diffImage
	 */
	public boolean constructDiffImage(File img1, File img2, File diffImage){		
		BufferedImage bimg1 = loadImage(img1);
		BufferedImage bimg2 = loadImage(img2);
		
		int width = Math.min(bimg1.getWidth(), bimg2.getWidth());
		int height = Math.min(bimg1.getHeight(), bimg2.getHeight());
		
		BufferedImage bimg3 = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for(int i = 0; i < height; i++) {
			for(int j = 0; j < width; j++) {	
				int xor = bimg2.getRGB(j, i)^bimg1.getRGB(j, i);
				/* 
				 * 16777215 - is the RGB value for (255,255,255)
				 * 15395562 - RGB int value for (233,233,233). Any pixel having RGB value greater than this is
				 * is assumed as a part of the Region of Interest (pixels highlighted as difference). 
				 * This value is set after doing some experiment using ImageJ tool. (Rajni) 
				 */
				if(xor < 15395562){
					xor = bimg2.getRGB(j, i); //In case RGB value is less than the threshold take test image pixel RGB.
				}else{
					xor = IMAGE_DIFF_HIGHLIGHT_COLOR.getRGB(); //else highlight the pixel as difference.
				}
				bimg3.setRGB(j, i, xor);
			}
		}
		boolean writeSuccess = writeImage(bimg3, ImageType.jpeg.toString(), diffImage);
		return writeSuccess;
	}
	
	/**
	 * This method applies the mask on the <code>sourceImageFile</code> and writes it into the <code>destImgFile</code>
	 * @param sourceImageFile	Source image file
	 * @param destImgFile		Destination image file
	 * @param labelSpecs		List of label specs acc to which mask is to be applied
	 * @param uom				Unit of measurement
	 * @param resolution		Resolution of the image. Required only if the uom is (in) or (cm)
	 * @throws IOException
	 * @throws LabelException
	 */
	public void applyMaskForImage(File sourceImageFile, File destImgFile, List<LabelSpec> labelSpecs, 
			 				String uom, int resolution) throws IOException, LabelException {
		BufferedImage img1 = ImageIO.read(sourceImageFile);
        int rgb_black = Color.BLACK.getRGB();

        float scale = LabelUtil.getInstance().getScaleFactorForImage(uom, resolution);
        
        for(int i = 0; i < labelSpecs.size(); i++) {
        	LabelSpec labelSpec = labelSpecs.get(i);
        	Template template = labelSpec.getTemplate();

        	for(int  j = 0; j < template.getMaskableRegions().size(); j++) {
        		MaskableRegion maskableRegion = template.getMaskableRegions().get(j);
        		
        		int x = (int) (scale * (labelSpec.getTemplateLeft() + maskableRegion.getLeft()));
        		int y = (int) (scale * (labelSpec.getTemplateTop() + maskableRegion.getTop()));
        		int width = (int) (scale * maskableRegion.getWidth());
        		int height = (int) (scale * maskableRegion.getHeight());
        		
        		for(int col = x; col <= x + width && col < img1.getWidth(); col++) {
        			for(int row = y; row <= y + height && row < img1.getHeight(); row++) {
        				img1.setRGB(col, row, rgb_black);
        			}
        		}
        	}
        	
        }
        
        String extension = CommonUtil.getExtension(sourceImageFile.getName());
        if(extension == null) {
        	extension = ImageType.gif.toString();
        }
        writeImage(img1, extension, destImgFile);
	 }

	/**
	 * Extracts data bytes from a given file <code>input</code>.
	 * @param input the file.
	 * @return bytes array data from input file.
	 */
	public byte[] getBytes(File input){		
		BufferedImage buffImg = null;
		try {
			buffImg = ImageIO.read(input);
			DataBufferByte db = (DataBufferByte)buffImg.getData().getDataBuffer();		
		    byte[] data = db.getData();
		    return data;
		} catch (IOException e) {
			logger.error(Messages.getMessage(CommonConstants.IO_EXCEPTION, "while reading from file ", input.getAbsolutePath(), e.getMessage()));
		}
		return null;
	}
	
	
	/**
	 * Checks whether a label element is within its bound. This algorithm checks if there is any black pixel within the padding region
	 * in which case there is a layout violation.
	 * @param img image file for the given label.
	 * @param labelSpec label specification for the given label.	 
	 * @param region the region of interest inside a label.
	 * @param scale the scaling factor.
	 */
	public boolean isLayoutMatch(BufferedImage bimg, Region region, float templateLeft, float templateTop, float scale) {
		boolean isInnerPadding = isPaddingRectInside(region);
		
		Rectangle outerRectangle = getOuterRectangle(region, templateLeft, templateTop, scale, isInnerPadding);
		Rectangle innerRectangle = getInnerRectangle(region, templateLeft, templateTop, scale, isInnerPadding);
		
		return isPaddingRegionEmpty(bimg, outerRectangle, innerRectangle);
	}
	/**
	 * Determines if padding region is outside or inside the region of interest<code>region</code>.
	 * @param region the region of interest.
	 * @return true is the padding region is inside, false otherwise.
	 */
	private boolean isPaddingRectInside(Region region) {
		boolean isInnerPadding = true;
		
		if((region.getPaddingLeft() < 0) || (region.getPaddingBottom() < 0) 
				|| (region.getPaddingRight() < 0) || (region.getPaddingBottom() < 0)) {
			isInnerPadding = false;
		}
		return isInnerPadding;
	}
	/**
	 * Returns the inner rectangle of the region <code>region</code> after considering the padding.
	 * @param region
	 * @param templateLeft
	 * @param templateTop
	 * @param scale
	 * @param isInnerPadding
	 * @return
	 */
	private Rectangle getInnerRectangle(Region region, float templateLeft, float templateTop, float scale, boolean isInnerPadding) {
		if(isInnerPadding) {
			//The padding rectangle lies inside the actual bounding box
			
			int innerRectX = (int) (scale * (templateLeft + region.getLeft() + region.getLineWidthLeft() + Math.abs(region.getPaddingLeft())));
			int innerRectY = (int) (scale * (templateTop + region.getTop() + region.getLineWidthTop() + Math.abs(region.getPaddingTop())));
			int innerRectWidth = (int) (scale * (region.getWidth() - region.getLineWidthLeft() - region.getLineWidthRight() - Math.abs(region.getPaddingLeft()) - Math.abs(region.getPaddingRight())));
			int innerRectHeight = (int) (scale * (region.getHeight() - region.getLineWidthTop() - region.getLineWidthBottom() - Math.abs(region.getPaddingTop()) - Math.abs(region.getPaddingBottom())));

			Rectangle innerRectangle = new Rectangle(innerRectX, innerRectY, innerRectWidth, innerRectHeight);
			return innerRectangle;
		} else {
			//The padding rectangle lies outside the actual bounding box
			
			int innerRectX = (int) (scale * (templateLeft + region.getLeft()));
			int innerRectY = (int) (scale * (templateTop + region.getTop()));
			int innerRectWidth = (int) (scale * (region.getWidth()));
			int innerRectHeight = (int) (scale * (region.getHeight()));
			
			Rectangle innerRectangle = new Rectangle(innerRectX, innerRectY, innerRectWidth, innerRectHeight);
			return innerRectangle;
		}
	}
	/**
	 * Returns the outer rectangle of the region <code>region</code> after considering the padding.
	 * @param region
	 * @param templateLeft
	 * @param templateTop
	 * @param scale
	 * @param isInnerPadding
	 * @return
	 */
	private Rectangle getOuterRectangle(Region region, float templateLeft, float templateTop, float scale, boolean isInnerPadding) {
		if(isInnerPadding) {
			//The padding rectangle lies inside the actual bounding box
			
			int outerRectX = (int) (scale * (templateLeft + region.getLeft() + region.getLineWidthLeft())) + 1;
			int outerRectY = (int) (scale * (templateTop + region.getTop() + region.getLineWidthTop())) + 1;
			int outerRectWidth = (int) (scale * (region.getWidth() - region.getLineWidthLeft() - region.getLineWidthRight()));
			int outerRectHeight = (int) (scale * (region.getHeight() - region.getLineWidthTop() - region.getLineWidthBottom()));

			Rectangle outerRectangle = new Rectangle(outerRectX, outerRectY, outerRectWidth, outerRectHeight);
			return outerRectangle;
		} else {
			//The padding rectangle lies outside the actual bounding box
			
			int outerRectX = (int) (scale * (templateLeft + region.getLeft() - Math.abs(region.getPaddingLeft())));
			int outerRectY = (int) (scale * (templateTop + region.getTop() - Math.abs(region.getPaddingTop())));
			int outerRectWidth = (int) (scale * (region.getWidth() + Math.abs(region.getPaddingLeft()) + Math.abs(region.getPaddingRight())));
			int outerRectHeight = (int) (scale * (region.getHeight() + Math.abs(region.getPaddingTop()) + Math.abs(region.getPaddingBottom())));
			
			Rectangle outerRectangle = new Rectangle(outerRectX, outerRectY, outerRectWidth, outerRectHeight);
			return outerRectangle;
		}
	}
	/**
	 * Checks if the padding region contains any black pixels, in which case there is a invalid layout. 
	 * @param bimg
	 * @param outerRect
	 * @param innerRect
	 * @return
	 */
	private boolean isPaddingRegionEmpty(BufferedImage bimg, Rectangle outerRect, Rectangle innerRect) {
		int imageWidth = bimg.getWidth();
		int imageHeight = bimg.getHeight();
		
		int outerRectX = (int) outerRect.getX();
		int outerRectY = (int) outerRect.getY();
		int outerRectWidth = (int) outerRect.getWidth();
		int outerRectHeight = (int) outerRect.getHeight();
		
		int innerRectX = (int) innerRect.getX();
		int innerRectY = (int) innerRect.getY();
		int innerRectWidth = (int) innerRect.getWidth();
		int innerRectHeight = (int) innerRect.getHeight();
		
		for(int i = outerRectY + 1; (i < (outerRectY + outerRectHeight)) && (i < imageHeight); i++) {
			for(int j = outerRectX + 1; (j < (outerRectX + outerRectWidth)) && (j < imageWidth); j++) {
				
				if((j >= innerRectX && j < (innerRectX + innerRectWidth)) && ((i >= innerRectY) && (i <= innerRectY + innerRectHeight))) {
					//If the iterations has reached inner rectangle, skip it.
					j = j + innerRectWidth;
					continue;
				}
				int rgb = bimg.getRGB(j, i);
				if(Color.BLACK.getRGB() == rgb) {
					return false;
				}
			}
		}
		return true;
	}
	/**
	 * Updates the layout diff buffered image to highlight outer and inner rectangles.
	 * @param image
	 * @param problematicRegions
	 * @param templateLeft
	 * @param templateTop
	 * @param scale
	 * @return
	 */
	public BufferedImage createLayoutDiffImage(BufferedImage image, List<Region> problematicRegions, 
			float templateLeft, float templateTop, float scale) {

		if(image.getColorModel() instanceof IndexColorModel){
			image = ((IndexColorModel)image.getColorModel()).convertToIntDiscrete(image.getData(), true);
		}
		
		for(Region region : problematicRegions) {
			boolean isInnerPadding = isPaddingRectInside(region);
			
			Rectangle innerRectangle = getInnerRectangle(region, templateLeft, templateTop, scale, isInnerPadding);
			Rectangle outerRectangle = getOuterRectangle(region, templateLeft, templateTop, scale, isInnerPadding);
			
			drawPaddingArea(image, innerRectangle, outerRectangle);
		}
		return image;
	}
	
	
	private void drawPaddingArea(BufferedImage image, Rectangle innerRect, Rectangle outerRect) {
		Rectangle topRect = new Rectangle();
		topRect.x = outerRect.x;
		topRect.y = outerRect.y;
		topRect.width = outerRect.width;
		topRect.height = innerRect.y - outerRect.y;
		
		Rectangle bottomRect = new Rectangle();
		bottomRect.x = outerRect.x;
		bottomRect.y = innerRect.y + innerRect.height;
		bottomRect.width = outerRect.width;
		bottomRect.height = outerRect.y + outerRect.height - innerRect.y - innerRect.height;
		
		Rectangle leftRect = new Rectangle();
		leftRect.x = outerRect.x;
		leftRect.y = innerRect.y;
		leftRect.width = innerRect.x - outerRect.x;
		leftRect.height = innerRect.height;
		
		Rectangle rightRect = new Rectangle();
		rightRect.x = innerRect.x + innerRect.width;
		rightRect.y = innerRect.y;
		rightRect.width = outerRect.x + outerRect.width - innerRect.x - innerRect.width;
		rightRect.height = innerRect.height;
		
		Graphics2D graphics2d = image.createGraphics();
		graphics2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		graphics2d.setColor(new Color(1f, 0f, 0f, 0.50f));

		graphics2d.fillRect(topRect.x, topRect.y, topRect.width, topRect.height);
		graphics2d.fillRect(bottomRect.x, bottomRect.y, bottomRect.width, bottomRect.height);
		graphics2d.fillRect(leftRect.x, leftRect.y, leftRect.width, leftRect.height);
		graphics2d.fillRect(rightRect.x, rightRect.y, rightRect.width, rightRect.height);
		
		graphics2d.setColor(new Color(0f, 1f, 0f, 0.25f));
		graphics2d.fillRect(innerRect.x, innerRect.y, innerRect.width, innerRect.height);
		
		graphics2d.setStroke(new BasicStroke(1));
		graphics2d.setColor(Color.GREEN.brighter());
		graphics2d.drawRect(innerRect.x, innerRect.y, innerRect.width, innerRect.height);
		graphics2d.setColor(Color.RED);
		graphics2d.drawRect(outerRect.x, outerRect.y, outerRect.width, outerRect.height);
		
		graphics2d.dispose();
	}
	
	/**
	 * Draws rectangles in specified region in the image <code>image</code>.
	 * @param image
	 * @param rect
	 * @param solid
	 * @return
	 */
	private BufferedImage drawRectangle(BufferedImage image, Rectangle rect, boolean solid) {
		int imageWidth = image.getWidth();
		int imageHeight = image.getHeight();
		
		int rectColor = IMAGE_DIFF_HIGHLIGHT_COLOR.getRGB();
		int regionRightEdge = rect.x + rect.width;
		int regionBottomEdge = rect.y + rect.height;
		
		int incFactor = 1;
		
		if(!solid) {
			rectColor = Color.GREEN.getRGB();
		}
		//This is needed to handle the IndexColorModel images in which case the rectangles were drawn in black rather than red and green.
		if(image.getColorModel() instanceof IndexColorModel){
			image = ((IndexColorModel)image.getColorModel()).convertToIntDiscrete(image.getData(), true);
		}
		//Draw top and bottom edge.
		for(int i = rect.x; i < regionRightEdge ; i += incFactor) {
			if(i >= 0 && i < imageWidth) {
				if(rect.y >= 0 && rect.y < imageHeight) {
					image.setRGB(i, rect.y, rectColor);
				}
				if(rect.y >= 0 && regionBottomEdge < imageHeight) {
					image.setRGB(i, regionBottomEdge, rectColor);
				}
			}
		}
		
		//Draw the left and right edge.
		for(int j = rect.y; j < regionBottomEdge; j += incFactor) {
			if(j >= 0 && j < imageHeight) {
				if(rect.x >=0 && rect.x < imageWidth) {
					image.setRGB(rect.x, j, rectColor);					
				}
				if(regionRightEdge >= 0 && regionRightEdge < imageWidth) {
					image.setRGB(regionRightEdge, j, rectColor);
				}
			}
		}
		
		return image;
	}
	/**
	 * Utility method for generating test case for testing the tool performance.
	 * @param file
	 * @param testCaseSize
	 */
	private void generateTestCase(File file, int testCaseSize){
		String copyImageName = file.getName();
		String imageNameWithoutExt = copyImageName.substring(0, copyImageName.lastIndexOf(LabelUtil.DOT));
		String extension = CommonUtil.getExtension(copyImageName);
		
		for(int i =1; i<=testCaseSize; i++){
			copyImageName = imageNameWithoutExt+i+LabelUtil.DOT+extension;
			try {
				FileUtils.copyFile(file, new File(file.getParent() + File.separator + copyImageName));
			} catch (IOException e) {
				logger.error("IOException while creating file: "+copyImageName);
			}
			
		}
		
	}
	
	public static void main(String[] args) {
		ImageUtil.getInstance().generateTestCase(new File("C:/Label/2X7_NonExpress_Domestic-Output.pdf"), 10000);
	}
}
