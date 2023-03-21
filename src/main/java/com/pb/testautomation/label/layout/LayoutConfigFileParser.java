package com.pb.testautomation.label.layout;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import com.pb.testautomation.label.constant.CommonConstants;
import com.pb.testautomation.label.exception.ConfigException;
import com.pb.testautomation.label.layout.model.LabelSpec;
import com.pb.testautomation.label.layout.model.LabelTypeConfig;
import com.pb.testautomation.label.layout.model.Page;
import com.pb.testautomation.label.layout.model.Region;
import com.pb.testautomation.label.layout.model.Template;
import com.pb.testautomation.label.util.CommonUtil;

/**
 * This class represent the parser for the configuration file.
 * 
 * @author ta013ba
 */
public class LayoutConfigFileParser extends DefaultHandler {
	public static Logger logger = Logger.getLogger(LayoutConfigFileParser.class);
	
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
	
	
	private static final String ATTRIBUTE_PREFIX = "prefix";
	private static final String ATTRIBUTE_PATH = "path";
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
	private Region region;
	
	
	private Map<String, LabelTypeConfig> labelsConfigMap = new HashMap<String, LabelTypeConfig>();
	private LabelTypeConfig labelTypeConfig;
	private LabelSpec labelSpec;
	
	private List<String> errors = new ArrayList<String>();
	private Map<String, Integer> errorsMap = new HashMap<String, Integer>();

	private File configFile;
	private boolean validateConfigFile;
	
	public LayoutConfigFileParser(File file, boolean validateConfigFile) {
		this.configFile = file;
		this.validateConfigFile = validateConfigFile;
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if(errors != null && !errors.isEmpty()) {
			return;
		}
		if(TAG_TEMPLATES.equals(qName)) {
			templatesMap = new HashMap<String, Template>();
		} else if(TAG_TEMPLATE.equals(qName)) {
			String path = attributes.getValue(ATTRIBUTE_PATH);
			
			if(!CommonUtil.isEmpty(path)) {
				handleExternalLayout(path);
				return;
			}

			String id = attributes.getValue(ATTRIBUTE_ID);
			if(CommonUtil.isEmpty(id)) {
				addError("Attribute: id is required in the tag template.");
			}
			template = new Template();
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
	
	private void handleExternalLayout(String path) {
		File file = new File(path);
		if(path.startsWith(".") || !file.exists()) {
			try {
				file = new File(configFile.getParent() + File.separator + path).getCanonicalFile();
			} catch (IOException e) {
			}
		}
		if(file.isDirectory()) {
			File listFiles[] = file.listFiles();
			
			for(File externalFile : listFiles) {
				if(externalFile.isFile()) {
					parseExternalLayoutFile(externalFile);					
				}
			}
		} else if(file.isFile()) {
			parseExternalLayoutFile(file);
		} else {
			logger.error("Refrenced File/Directory not found: " + path);
		}
	}

	private void parseExternalLayoutFile(File externalFile) {
		if(externalFile.equals(configFile)) {
			logger.info("Skipping the self refrenced file to remove cyclic dependency: " + externalFile);
			return;
		}
		if(!externalFile.getName().toLowerCase().endsWith(".xml")) {
			logger.info("Skipping non-xml file: " + externalFile);
			return;
		}
		try {
			InputStream is = new FileInputStream(externalFile);
			SAXParserFactory spf = SAXParserFactory.newInstance();
			
			ExternalTemplateFileParser parser = new ExternalTemplateFileParser(externalFile.getName());
			spf.setNamespaceAware(validateConfigFile);
			spf.setValidating(validateConfigFile);
			
			SAXParser sp = spf.newSAXParser();
			if(validateConfigFile) {
				sp.setProperty(CommonConstants.JAXP_SCHEMA_LANGUAGE, CommonConstants.W3C_XML_SCHEMA); 
				
				InputStream in = ClassLoader.getSystemResourceAsStream("external_layout_template.xsd");
				sp.setProperty(CommonConstants.JAXP_SCHEMA_SOURCE, in);
			}
			
			sp.parse(is, parser);
			
			if(parser.hasErrorrs()) {
				ConfigException e = new ConfigException();
				e.setMessages(parser.getErrors());
				
				throw e;
//				errors.addAll(parser.getErrors());
			} else {
				Map<String, Template> externalTemplates = parser.getTemplatesMap();
				
				templatesMap.putAll(externalTemplates);				
			}
		} catch(Exception e) {
			logger.error("Error occurred while parsing file: " + externalFile + "\n\t\t\t\t" + e.getMessage());
		}
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
