@echo off
del *.log
%ANT_HOME%\bin\ant.bat -logfile update.log -buildfile update.xml %1 %2 %3 %4 %5 %6 %7
pause
