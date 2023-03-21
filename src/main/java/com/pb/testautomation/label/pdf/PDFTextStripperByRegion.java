package com.pb.testautomation.label.pdf;

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.pdfbox.util.TextPosition;

/**
 * This class extracts the text from the pdf, except for the escape regions specified.
 * 
 * @author ta013ba
 */
public class PDFTextStripperByRegion extends PDFTextStripper
{
    private Map<String,Rectangle2D> escapeRegionArea = new HashMap<String,Rectangle2D>();

    /**
     * Constructor.
     * @throws IOException If there is an error loading properties.
     */
    public PDFTextStripperByRegion() throws IOException {
        super();
        setPageSeparator( "" );
    }

        
    /**
     * Instantiate a new PDFTextStripperArea object. Loading all of the operator
     * mappings from the properties object that is passed in. Does not convert
     * the text to more encoding-specific output.
     * 
     * @param props
     *            The properties containing the mapping of operators to
     *            PDFOperator classes.
     * 
     * @throws IOException
     *             If there is an error reading the properties.
     */
    public PDFTextStripperByRegion(Properties props) throws IOException {
        super(props);
        setPageSeparator("");
    }

    /**
     * Instantiate a new PDFTextStripperArea object. This object will load
     * properties from PDFTextStripper.properties and will apply
     * encoding-specific conversions to the output text.
     * 
     * @param encoding
     *            The encoding that the output will be written in.
     * @throws IOException
     *             If there is an error reading the properties.
     */
    public PDFTextStripperByRegion(String encoding) throws IOException {
        super(encoding);
        setPageSeparator("");
    }
    
   /**
     * Add a new region which we want to escape while extracting the text.
     *
     * @param regionName The name of the region.
     * @param rect The rectangle area to escape the text from.
     */
    public void addEscapeRegion(String regionName, Rectangle2D rect) {
        escapeRegionArea.put( regionName, rect );
    }

    /**
     * {@inheritDoc}
     */
    protected void processTextPosition(TextPosition text) {
        Iterator<String> regionIter = escapeRegionArea.keySet().iterator();
        boolean isInEscapeRegion = false;
        
        while( regionIter.hasNext() )
        {
            String region = regionIter.next();
            Rectangle2D rect = escapeRegionArea.get( region );
            
            float x = text.getX();
            float y = text.getY();
            float width = text.getWidth();
            float height = text.getHeight();
            
            if(text.getDir() == 0) {	//When text is from left to right
            	if(rect.contains(x, y) && rect.contains(x+width, y+height)) {
            		isInEscapeRegion = true;
            		break;
            	}
            	
            } else {	//Any other orientiation of the text
            	if(rect.contains(x, y)) {
            		isInEscapeRegion = true;
            		break;
            	}
            }
        }
        
        if(!isInEscapeRegion) {	//If the current text is not in the escape region, then process it. Otherwise do nothing.
        	super.processTextPosition(text);        	
        }
    }
}
