INSTALLATION INSTRUCTIONS:

Environment Setup: Ensure the java runtime environment is installed. Install Java SDK 1.5 or above. Make sure JAVA_HOME variable is set. For detailed information on how to install java, refer to the following url: http://www.java.com/en/download/help/index_installing.xml

Label Setup: Copy the master labels in a directory, herein referred to as master. Also, copy the test labels in a directory, herein referred to as test. Please note that the tool uses labels with same name to compare against each other. For naming convention of the labels, please refer to the NextGen_Label_Automation Requirements document. 

Unzip LabelTestAutomation.zip file to a directory, herein referred to as root directory. It should contain the following files:
        
LabelTestAutomation.jar
label-test-automation.properties
run.bat
sample-config-file.xml
sample-master
sample-test
README.docx

Config File setup: For configuring maskable regions, please provide specifications in the configuration file, herein referred to as config file. A sample config file has been provided in the root directory. For measuring dimensions (in inches) in PDF labels, open source tools such as PDFXchangeViewer can be used for accurate results. For measuring dimensions (in px) in image labels, open source tools like ImageJ can be used for accurate results.

The tool can be invoked from command line by using following command

java –jar LabelTestAutomation.jar <options>
       
   <options>

    -master :  Master label file/directory       (Mandatory)
    -test   :  Test label file/directory         (Mandatory)
    -config :  The configuration file            (Mandatory)
    -type   :  Label type (pdf/image/all)        (Optional)

Optionally, the tool can also be invoked through run.bat file. For this the label and config file locations can be specified in the run.bat file or label-test-automation.properties file. Preference is given to inputs in the batch file.

After successful execution a directory named Report is generated in the directory containing the test labels. It contains three types of report.
 > a. Execution summary report: Highlights the summary of execution. This report is created for every execution with name including the timestamp.

 > b. Detailed report: Highlights the details of the execution. It indicates the textual and visual differences if any during comparison of labels.
 
 > c. DiffImage: Directory containing the diff images that are created for each label that is different with master label in case of comparison.