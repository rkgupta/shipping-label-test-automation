package com.pb.testautomation.label.engine;

import java.io.File;

import com.pb.testautomation.label.GenericLabelComparator;
import com.pb.testautomation.label.ImageLabelComparator;
import com.pb.testautomation.label.LabelComparator;
import com.pb.testautomation.label.LabelType;
import com.pb.testautomation.label.PDFLabelComparator;
/**
 * Factory class that creates appropriate LabelComparator instance. 
 * @author RA013GU
 *
 */
public class LabelComparatorFactory {
	/**
	 * Returns appropriate LabelComparator instance based on the label type.
	 * @param master - master label.
	 * @param test - test label.
	 * @param configFile - config file.
	 * @param labelType - label type.
	 * @return
	 */
	public static LabelComparator getLabelComparator(File master, File test, File configFile, LabelType labelType){
		LabelComparator labelComparator = null;
		if(LabelType.PDF.equals(labelType)){
			labelComparator = new PDFLabelComparator(master, test, configFile);
			labelComparator.setLabelType(LabelType.PDF.toString());
		}else if(LabelType.IMAGE.equals(labelType)){
			labelComparator = new ImageLabelComparator(master, test, configFile);
			labelComparator.setLabelType(LabelType.IMAGE.toString());
		}else{
			labelComparator = new GenericLabelComparator(master, test, configFile);
			labelComparator.setLabelType(LabelType.ALL.toString());
		}		
		return labelComparator;
		
	}

}
