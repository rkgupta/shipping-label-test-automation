@echo off
echo.
echo =================================== Executing the LabelTestAutomation suite ==================================
echo.
java -jar LabelTestAutomation.jar -master C:\LabelDemo\master -test C:\LabelDemo\test -config C:\LabelDemo\config.xml -type all

echo.
echo ==================================== Label testing complete ====================================

echo.
echo Do you want to view report?

set /P reply=Press Y to view the report or N to skip [Y/N]

if /I "%reply%" EQU "y" GOTO YES
if /I "%reply%" EQU "n" GOTO NO

:YES
%SystemRoot%\explorer.exe "C:\LabelDemo\Report" 

:NO
exit