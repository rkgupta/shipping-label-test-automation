package com.pb.testautomation.label.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import com.pb.testautomation.label.constant.CommonConstants;
import com.pb.testautomation.label.exception.ConfigException;
import com.pb.testautomation.label.model.LabelSpec;
import com.pb.testautomation.label.model.LabelTypeConfig;
import com.pb.testautomation.label.model.MaskableRegion;
import com.pb.testautomation.label.model.Page;
import com.pb.testautomation.label.model.Template;

/**
 * This class represent the parser for the configuration file.
 * 
 * @author ta013ba
 */
public class ConfigFileParser extends DefaultHandler {
	public static Logger logger = Logger.getLogger(ConfigFileParser.class);
	
	private static final String TAG_TEMPLATES = "templates";
	private static final String TAG_TEMPLATE = "template";
	private static final String ATTRIBUTE_ID = "id";
	private static final String ATTRIBUTE_NUMBER = "number";
	private static final String TAG_MASKABLE_REGION = "maskableRegion";
	private static final String TAG_REGION_TYPE = "regionType";
	private static final String TAG_DIRECTION = "direction";
	private static final String TAG_LEFT = "left";
	private static final String TAG_TOP = "top";
	private static final String TAG_WIDTH = "width";
	private static final String TAG_HEIGHT = "height";
	
	
	private static final String ATTRIBUTE_PREFIX = "prefix";
	private static final String ATTRIBUTE_REF = "ref";
	private static final String TAG_LABEL_TYPE_CONFIG = "labelTypeConfig";
	private static final String TAG_UOM = "uom";
	private static final String TAG_RESOLUTION = "resolution";
	private static final String TAG_PAGE_LIST = "pageList";
	private static final String TAG_PAGE = "page";
	private static final String TAG_LABEL_SPEC = "labelSpec";
	private static final String TAG_TEMPLATE_LEFT = "templateLeft";
	private static final String TAG_TEMPLATE_TOP = "templateTop";
	
	
	private StringBuilder builder = new StringBuilder();
	private Map<String, Template> templatesMap;
	private Template template;
	private Page page;
	private MaskableRegion region;
	
	
	private Map<String, LabelTypeConfig> labelsConfigMap = new HashMap<String, LabelTypeConfig>();
	private LabelTypeConfig labelTypeConfig;
	private LabelSpec labelSpec;
	
	private List<String> errors = new ArrayList<String>();
	private Map<String, Integer> errorsMap = new HashMap<String, Integer>();
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if(errors != null && !errors.isEmpty()) {
			return;
		}
		if(TAG_TEMPLATES.equals(qName)) {
			templatesMap = new HashMap<String, Template>();
		} else if(TAG_TEMPLATE.equals(qName)) {
			template = new Template();
			
			String id = attributes.getValue(ATTRIBUTE_ID);
			template.setId(id);
		} else if(TAG_MASKABLE_REGION.equals(qName)) {
			region = new MaskableRegion();
			
			String id = attributes.getValue(ATTRIBUTE_ID);
			region.setId(id);
		} else if(TAG_LABEL_TYPE_CONFIG.equals(qName)) {
			labelTypeConfig = new LabelTypeConfig();
			
			String prefix = attributes.getValue(ATTRIBUTE_PREFIX);
			labelTypeConfig.setPrefix(prefix);
		} else if(TAG_PAGE.equals(qName)) {
			page = new Page();
			
			String number = attributes.getValue(ATTRIBUTE_NUMBER);
			page.setNumber(number);
			
		} else if(TAG_LABEL_SPEC.equals(qName)) {
			labelSpec = new LabelSpec();
			
			String ref = attributes.getValue(ATTRIBUTE_REF);
			Template template = templatesMap.get(ref);
			if(template != null) {
				labelSpec.setTemplate(templatesMap.get(ref));				
			} else {
				String error = "No template found with the template id='" + ref + "'";
				addError(error);
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
		if(TAG_REGION_TYPE.equals(qName)) {
			region.setRegionType(builder.toString());
		} else if(TAG_DIRECTION.equals(qName)) {
			if(CommonUtil.isEmpty(builder.toString())) {
				region.setDirection(CommonConstants.DIR_LEFT_TO_RIGHT);
			} else if(CommonConstants.DIR_LEFT_TO_RIGHT.equalsIgnoreCase(builder.toString())
					|| CommonConstants.DIR_TOP_TO_BOTTOM.equalsIgnoreCase(builder.toString())) {
				region.setDirection(builder.toString());
			} else {
				addError("Unsupported Direction type: " + builder.toString());
			}
		} else if(TAG_LEFT.equals(qName)) {
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
		} else if(TAG_MASKABLE_REGION.equals(qName)) {
			if(region != null) {
				template.addMaskableRegion(region);
				region = null;
			}
		} else if(TAG_TEMPLATE.equals(qName)) {
			if(template != null) {
				templatesMap.put(template.getId(), template);
				template = null;
			}
		} else if(TAG_UOM.equals(qName)) {
			labelTypeConfig.setUom(builder.toString());
		} else if(TAG_RESOLUTION.equals(qName)) {
			try {
				labelTypeConfig.setResolution(Integer.parseInt(builder.toString()));				
			} catch(NumberFormatException e) {}
		} else if(TAG_TEMPLATE_LEFT.equals(qName)) {
			try {
				labelSpec.setTemplateLeft(Float.parseFloat(builder.toString().trim()));				
			}catch(NumberFormatException e) {
				
			}
		} else if(TAG_TEMPLATE_TOP.equals(qName)) {
			try {
				labelSpec.setTemplateTop(Float.parseFloat(builder.toString().trim()));				
			}catch(NumberFormatException e) {
				
			}
		} else if(TAG_LABEL_SPEC.equals(qName)) {
			page.addLabelSpec(labelSpec);
		} else if(TAG_PAGE.equals(qName)) {
			String pageNumber = page.getNumber();
			
			try {
				//Put it acc to the number specified in the xml
				int pageNumberInt = Integer.parseInt(pageNumber);
				Page prevPage = labelTypeConfig.getPage(pageNumberInt);
				if(prevPage == null) {
					labelTypeConfig.addPage(pageNumberInt, page);					
				} else {
					addError("Page with the page number " + pageNumberInt + " already exists");
				}
				page = null;
			} catch(NumberFormatException e) {
			}
		} else if(TAG_LABEL_TYPE_CONFIG.equals(qName)) {
			labelsConfigMap.put(labelTypeConfig.getPrefix(), labelTypeConfig);
		}
	}
	
	/**
	 * This method returns the config file mappings. 
	 * @return
	 * @throws ConfigException	Throws config exception if there is problem in the configuration file.
	 */
	public Map<String, LabelTypeConfig> getConfigFileMappings() throws ConfigException {
		if(errors != null && !errors.isEmpty()) {
			ConfigException e = new ConfigException();
			e.setMessages(errors);
			throw e;
		}
		return labelsConfigMap;
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
	
	/*
	 * This method adds the error into the list of errors.
	 */
	private void addError(String error) {
		errors.add(error);
	}
	
	/*
	 * This method takes care of the fact that only one error message is there for a specific line and column number in the config file.  
	 */
	private void addError(String error, int lineNumber, int columnNumber) {
		String position = "Line: " + lineNumber + ", Column: " + columnNumber;
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
