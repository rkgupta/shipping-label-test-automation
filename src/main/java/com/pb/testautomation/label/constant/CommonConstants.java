package com.pb.testautomation.label.constant;

/**
 * Defines application level constants.
 * @author ta013ba
 */
public interface CommonConstants {
	public static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
	public static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema"; 
	public static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
	
	public static final String UOM_CM = "cm";
	public static final String UOM_MM = "mm";
	public static final float CONVERSION_FACTOR_CM_TO_INCH = 0.393700787f;
	public static final float CONVERSION_FACTOR_MM_TO_INCH = 0.0393700787f;
	
	public static final String UOM_INCH = "in";
	public static final String UOM_PIXELS = "px";
	public static final String PDF_PAGE_NUM_TO_IMG_NUM_PREFIX = "_";
	public static final String MASKABLE_REGION_TYPE_IMAGE = "image";
	public static final String MASKABLE_REGION_TYPE_TEXT = "text";
	public static final String DIR_TOP_TO_BOTTOM = "top-to-bottom";
	public static final String DIR_LEFT_TO_RIGHT = "left-to-right";
	
	/*
	 * Defines application level error message keys
	 */
	public static final String CONFIG_EXCEPTION = "config.exception";
	public static final String CHECKING_LAYOUT_EXCEPTION = "layout.checking.exception";
	public static final String IO_EXCEPTION = "io.exception";
	public static final String INVOCATION_EXCEPTION = "invocation.exception";
	public static final String MISSING_FILE_EXCEPTION = "missing.file.exception";
	public static final String LABEL_NAME_MISMATCH_EXCEPTION = "label.name.mismatch";
	public static final String CLASS_NOT_FOUND_EXCEPTION = "class.notfound.exception";
	public static final String PDF_TO_IMAGE_CONVERSION_EXCEPTION = "pdf2image.conversion.exception";
	public static final String INCOMPATIBLE_LABEL_TYPE_EXCEPTION = "incompatible.labeltype.exception";
	public static final String MASKING_EXCEPTION = "masking.exception";
	public static final String NUMBER_FORMAT_EXCEPTION = "number.format.exception";
	public static final String NO_SUCH_ALGORITHM_EXCEPTION = "no.such.algorithm.exception";
	public static final String TEXT_COMPARISON_REPORT_EXCEPTION = "text.comparison.report.exception";
	public static final String LABEL_COMPARISON_EXCEPTION = "label.comparison.exception";
	public static final String IMAGE_COMPARISON_REPORT_EXCEPTION = "image.comparison.report.exception";
	public static final String STATUS_FILE_WRITE_EXCEPTION = "status.write.exception";
	
	
	/*
	 * Defines application level info message keys
	 */
	public static final String INFO_MASTER_LABEL_IMAGE_MASKING = "master.label.image.masking.info";
	public static final String INFO_TEST_LABEL_IMAGE_MASKING = "test.label.image.masking.info";
	public static final String INFO_NO_CONFIGURATION_FOUND_IN_CONFIG_FILE = "no.config.found.info";
	public static final String INFO_COMPARING_TEST_LABEL_WITH_MASTER = "compare.test.label.with.master.info";
	public static final String INFO_CREATE_MASTER_LABEL_IMAGE = "create.master.label.image.info";
	public static final String INFO_PROCESS_MASTER_LABEL = "process.master.label.info";
	public static final String INFO_CREATE_TEST_LABEL_IMAGE = "create.test.label.image.info";
	public static final String INFO_IMAGES_MATCH = "images.match.info";
	public static final String INFO_IMAGES_MISMATCH = "images.mismatch.info";
	public static final String INFO_WRITING_MASTER_LABEL_HASH_DATA_IN_CACHE = "writing.master.data.cache.info";
	public static final String INFO_UPDATING_CONFIG_FILE_TIMESTAMP = "update.config.file.timestamp.info";
	public static final String INFO_UPDATING_MASTER_LABEL_TIMESTAMP = "update.master.label.timestamp.info";
	public static final String INFO_CONFIG_FILE_TIMESTAMP_MODIFIED = "config.file.timestamp.modified.info";
	public static final String INFO_MASTER_LABEL_TIMESTAMP_MODIFIED = "master.label.timestamp.modified.info";
	public static final String INFO_LOADING_MASTER_LABEL_HASH_DATA_FROM_CACHE = "master.label.hash.data.load.info";
	public static final String INFO_COMPARING_LABEL_DIRECTORIES = "compare.label.directories.info";
	public static final String INFO_COMPARING_LABEL_FILES = "compare.label.files.info";
	public static final String INFO_COMPARISON_EXECUTION_TIME = "comparison.exection.time.info";
	public static final String INFO_LOADING_PROPEERTIES_FILE = "load.properties.file.info";
	public static final String INFO_LOADING_CONFIGURATION_FILE = "load.config.file.info";
	public static final String INFO_WRITING_IMAGE = "write.image.info";
	/*
	 * INFO constants related to layout testing.
	 */
	public static final String INFO_TESTING_LAYOUT = "layout.testing.info";
	public static final String INFO_LAYOUT_TEST_EXECUTION_TIME =  "layout.exection.time.info";
	public static final String INFO_VALIDATING_LABEL_LAYOUT = "layout.validation.info";
	public static final String INFO_COMPARING_PDF_LABEL_LAYOUT = "pdf.label.layout.comparison.info";
	public static final String INFO_COMPARING_IMAGE_LABEL_LAYOUT = "image.label.layout.comparison.info";
}
