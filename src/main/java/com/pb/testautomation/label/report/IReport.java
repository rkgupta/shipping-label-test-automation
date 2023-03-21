package com.pb.testautomation.label.report;
/**
 * Report interface. Each report that is generated during label comparison
 * will implement IReport interface.
 * @author RA013GU
 *
 */
public interface IReport {
	/*
	 * Abstract method. To be implemented by all the implementing classes.
	 */
	abstract String generateReport();

}
