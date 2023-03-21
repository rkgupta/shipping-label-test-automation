package com.pb.testautomation.label.engine;

import java.io.File;

import com.pb.testautomation.label.LabelType;
import com.pb.testautomation.label.layout.GenericLayoutComparator;
import com.pb.testautomation.label.layout.ImageLayoutComparator;
import com.pb.testautomation.label.layout.LayoutComparator;
import com.pb.testautomation.label.layout.PDFLayoutComparator;

/**
 * Factory class that gives LayoutComparator instance of appropriate type.
 * @author ra013gu
 *
 */
public class LayoutComparatorFactory {
	/**
	 * Creates LayoutComparator instance of appropriate type based on the given label type.
	 * @param test the test label file/directory.
	 * @param configFile the configuration file.
	 * @param labelType the type of label (PDF/Image/All).
	 * @return <code>layoutComparator</code> instance.
	 */
	public static LayoutComparator getLayoutComparator(File test, File configFile, LabelType labelType){
		LayoutComparator layoutComparator = null;
		if(LabelType.PDF.equals(labelType)){
			layoutComparator = new PDFLayoutComparator(test, configFile);
		}else if(LabelType.IMAGE.equals(labelType)){
			layoutComparator = new ImageLayoutComparator(test, configFile);
		}else{
			layoutComparator = new GenericLayoutComparator(test, configFile);
		}
		return layoutComparator;
	}

}
