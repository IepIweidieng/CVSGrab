@echo off
if [%ANT_HOME%] == [] goto install_ant

call %ANT_HOME%\bin\ant.bat

goto eof

echo Please install Ant (from jakarta.apache.org/ant) and define the ANT_HOME variable

:eof