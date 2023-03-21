package com.pb.testautomation.label.layout;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import com.pb.testautomation.label.exception.ConfigException;
import com.pb.testautomation.label.layout.model.Page;
import com.pb.testautomation.label.layout.model.Region;
import com.pb.testautomation.label.layout.model.Template;
import com.pb.testautomation.label.util.CommonUtil;

public class ExternalTemplateFileParser extends DefaultHandler {
	private String fileName;
	
	private static final String TAG_TEMPLATES = "templates";
	private static final String TAG_TEMPLATE = "template";
	private static final String ATTRIBUTE_ID = "id";
	private static final String ATTRIBUTE_LINE_WIDTH = "lineWidth";
	private static final String ATTRIBUTE_LINE_WIDTH_LEFT = "lineWidthLeft";
	private static final String ATTRIBUTE_LINE_WIDTH_RIGHT = "lineWidthRight";
	private static final String ATTRIBUTE_LINE_WIDTH_TOP = "lineWidthTop";
	private static final String ATTRIBUTE_LINE_WIDTH_BOTTOM = "lineWidthBottom";
	private static final String ATTRIBUTE_PADDING = "padding";
	private static final String ATTRIBUTE_PADDING_LEFT = "paddingLeft";
	private static final String ATTRIBUTE_PADDING_TOP = "paddingTop";
	private static final String ATTRIBUTE_PADDING_RIGHT = "paddingRight";
	private static final String ATTRIBUTE_PADDING_BOTTOM = "paddingBottom";
	
	private static final String ATTRIBUTE_NUMBER = "number";
	private static final String TAG_REGION = "region";
	private static final String TAG_LEFT = "left";
	private static final String TAG_TOP = "top";
	private static final String TAG_WIDTH = "width";
	private static final String TAG_HEIGHT = "height";
	
	private StringBuilder builder = new StringBuilder();
	private Map<String, Template> templatesMap = new HashMap<String, Template>();
	private Template template;
	private Page page;
	private Region region;
	
	private List<String> errors = new ArrayList<String>();
	private Map<String, Integer> errorsMap = new HashMap<String, Integer>();

	public ExternalTemplateFileParser(String fileName) {
		this.fileName = fileName;
	}
	
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if(errors != null && !errors.isEmpty()) {
			return;
		}
		if(TAG_TEMPLATE.equals(qName)) {
			template = new Template();
			
			String id = attributes.getValue(ATTRIBUTE_ID);
			template.setId(id);
			
			String padding = attributes.getValue(ATTRIBUTE_PADDING);
			template.setPadding(CommonUtil.parseFloat(padding));
			
			String paddingLeft = attributes.getValue(ATTRIBUTE_PADDING_LEFT);
			Float paddingLeftVal = CommonUtil.parseFloat(paddingLeft);
			if(paddingLeftVal != null) {
				template.setPaddingLeft(paddingLeftVal);
			}
			
			String paddingRight = attributes.getValue(ATTRIBUTE_PADDING_RIGHT);
			Float paddingRightVal = CommonUtil.parseFloat(paddingRight);
			if(paddingRightVal != null) {
				template.setPaddingRight(paddingRightVal);
			}
			
			String paddingTop = attributes.getValue(ATTRIBUTE_PADDING_TOP);
			Float paddingTopVal = CommonUtil.parseFloat(paddingTop);
			if(paddingTopVal != null) {
				template.setPaddingTop(paddingTopVal);
			}
			
			String paddingBottom = attributes.getValue(ATTRIBUTE_PADDING_BOTTOM);
			Float paddingBottomVal = CommonUtil.parseFloat(paddingBottom);
			if(paddingBottomVal != null) {
				template.setPaddingBottom(paddingBottomVal);
			}
			
			String lineWidth = attributes.getValue(ATTRIBUTE_LINE_WIDTH);
			Float lineWidthVal = CommonUtil.parseFloat(lineWidth);
			if(lineWidthVal != null) {
				template.setLineWidth(lineWidthVal);
			}
			
			String lineWidthLeft = attributes.getValue(ATTRIBUTE_LINE_WIDTH_LEFT);
			Float lineWidthLeftVal = CommonUtil.parseFloat(lineWidthLeft);
			if(lineWidthLeftVal != null) {
				template.setLineWidthLeft(lineWidthLeftVal);
			}
			
			String lineWidthRight = attributes.getValue(ATTRIBUTE_LINE_WIDTH_RIGHT);
			Float lineWidthRightVal = CommonUtil.parseFloat(lineWidthRight);
			if(lineWidthRightVal != null) {
				template.setLineWidthRight(lineWidthRightVal);
			}
			
			String lineWidthTop = attributes.getValue(ATTRIBUTE_LINE_WIDTH_TOP);
			Float lineWidthTopVal = CommonUtil.parseFloat(lineWidthTop);
			if(lineWidthTopVal != null) {
				template.setLineWidthTop(lineWidthTopVal);
			}
			
			String lineWidthBottom = attributes.getValue(ATTRIBUTE_LINE_WIDTH_BOTTOM);
			Float lineWidthBottomVal = CommonUtil.parseFloat(lineWidthBottom);
			if(lineWidthBottomVal != null) {
				template.setLineWidthBottom(lineWidthBottomVal);
			}
		} else if(TAG_REGION.equals(qName)) {
			region = new Region();
			
			String id = attributes.getValue(ATTRIBUTE_ID);
			region.setId(id);
			
			//Set the default values from the template
			region.setPadding(template.getDefaultPadding());
			region.setPaddingLeft(template.getPaddingLeft());
			region.setPaddingRight(template.getPaddingRight());
			region.setPaddingTop(template.getPaddingTop());
			region.setPaddingBottom(template.getPaddingBottom());
			
			region.setLineWidth(template.getLineWidth());
			region.setLineWidthLeft(template.getLineWidthLeft());
			region.setLineWidthRight(template.getLineWidthRight());
			region.setLineWidthTop(template.getLineWidthTop());
			region.setLineWidthBottom(template.getLineWidthBottom());
			
			//Override the template padding values from the values specified in this tag.
			String padding = attributes.getValue(ATTRIBUTE_PADDING);
			if(padding != null) {
				region.setPadding(CommonUtil.parseFloat(padding));				
			}
			
			String paddingLeft = attributes.getValue(ATTRIBUTE_PADDING_LEFT);
			Float paddingLeftVal = CommonUtil.parseFloat(paddingLeft);
			if(paddingLeftVal != null) {
				region.setPaddingLeft(paddingLeftVal);
			}
			
			String paddingRight = attributes.getValue(ATTRIBUTE_PADDING_RIGHT);
			Float paddingRightVal = CommonUtil.parseFloat(paddingRight);
			if(paddingRightVal != null) {
				region.setPaddingRight(paddingRightVal);
			}
			
			String paddingTop = attributes.getValue(ATTRIBUTE_PADDING_TOP);
			Float paddingTopVal = CommonUtil.parseFloat(paddingTop);
			if(paddingTopVal != null) {
				region.setPaddingTop(paddingTopVal);
			}
			
			String paddingBottom = attributes.getValue(ATTRIBUTE_PADDING_BOTTOM);
			Float paddingBottomVal = CommonUtil.parseFloat(paddingBottom);
			if(paddingBottomVal != null) {
				region.setPaddingBottom(paddingBottomVal);
			}
			
			String lineWidth = attributes.getValue(ATTRIBUTE_LINE_WIDTH);
			Float lineWidthVal = CommonUtil.parseFloat(lineWidth);
			if(lineWidthVal != null) {
				region.setLineWidth(lineWidthVal);
			}
			
			String lineWidthLeft = attributes.getValue(ATTRIBUTE_LINE_WIDTH_LEFT);
			Float lineWidthLeftVal = CommonUtil.parseFloat(lineWidthLeft);
			if(lineWidthLeftVal != null) {
				region.setLineWidthLeft(lineWidthLeftVal);
			}
			
			String lineWidthRight = attributes.getValue(ATTRIBUTE_LINE_WIDTH_RIGHT);
			Float lineWidthRightVal = CommonUtil.parseFloat(lineWidthRight);
			if(lineWidthRightVal != null) {
				region.setLineWidthRight(lineWidthRightVal);
			}
			
			String lineWidthTop = attributes.getValue(ATTRIBUTE_LINE_WIDTH_TOP);
			Float lineWidthTopVal = CommonUtil.parseFloat(lineWidthTop);
			if(lineWidthTopVal != null) {
				region.setLineWidthTop(lineWidthTopVal);
			}
			
			String lineWidthBottom = attributes.getValue(ATTRIBUTE_LINE_WIDTH_BOTTOM);
			Float lineWidthBottomVal = CommonUtil.parseFloat(lineWidthBottom);
			if(lineWidthBottomVal != null) {
				region.setLineWidthBottom(lineWidthBottomVal);
			}
		}
		builder.setLength(0);
	}
	
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if(errors != null && !errors.isEmpty()) {
			return;
		}
		if(ch != null && ch.length > 0) {
			String val = new String(ch, start, length);
			builder.append(val);
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if(errors != null && !errors.isEmpty()) {
			return;
		}
		if(TAG_LEFT.equals(qName)) {
			try {
				region.setLeft(Float.parseFloat(builder.toString()));				
			}catch(NumberFormatException e) {}
		} else if(TAG_TOP.equals(qName)) {
			try {
				region.setTop(Float.parseFloat(builder.toString()));				
			}catch(NumberFormatException e) {}
		} else if(TAG_WIDTH.equals(qName)) {
			try {
				region.setWidth(Float.parseFloat(builder.toString()));				
			}catch(NumberFormatException e) {}
		} else if(TAG_HEIGHT.equals(qName)) {
			try {
				region.setHeight(Float.parseFloat(builder.toString()));				
			}catch(NumberFormatException e) {}
		} else if(TAG_REGION.equals(qName)) {
			if(region != null) {
				template.addRegion(region);
				region = null;
			}
		} else if(TAG_TEMPLATE.equals(qName)) {
			if(template != null) {
				templatesMap.put(template.getId(), template);
				template = null;
			}
		}
	}
	
	@Override
	public void error(SAXParseException e) throws SAXException {
		super.error(e);
		
		addError(e.getLocalizedMessage(), e.getLineNumber(), e.getColumnNumber());
	}
	
	@Override
	public void fatalError(SAXParseException e) throws SAXException {
		super.fatalError(e);
		
		addError(e.getLocalizedMessage(), e.getLineNumber(), e.getColumnNumber());
	}
	
	public Map<String, Template> getTemplatesMap() throws ConfigException {
		return templatesMap;
	}
	
	public boolean hasErrorrs() {
		return errors != null && !errors.isEmpty();
	}
	
	public List<String> getErrors() {
		return errors;
	}
	
	/*
	 * This method takes care of the fact that only one error message is there for a specific line and column number in the config file.  
	 */
	private void addError(String error, int lineNumber, int columnNumber) {
		String position = "File: " + fileName + ", Line: " + lineNumber + ", Column: " + columnNumber;
		String errorMsg = position + " - " + error;		
		
		if(lineNumber != 0 || columnNumber != 0) {
			if(errorsMap.containsKey(position)) {
				int index = errorsMap.get(position);
				errors.set(index, errorMsg);
			} else {
				errorsMap.put(position, errors.size());
				errors.add(errorMsg);
			}
		} else {
			errors.add(errorMsg);
		}
	}
}
