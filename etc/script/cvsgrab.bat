@echo off

if not "%OS%"=="Windows_NT" goto win9xStart
:winNTStart
@setlocal

rem %~dp0 is name of current script under NT
set DEFAULT_CVSGRAB_HOME=%~dp0

if "%CVSGRAB_HOME%"=="" set CVSGRAB_HOME=%DEFAULT_CVSGRAB_HOME%
set DEFAULT_CVSGRAB_HOME=

rem Need to check if we are using the 4NT shell...
if "%@eval[2+2]" == "4" goto setup4NT

rem On NT/2K grab all arguments at once
set ANT_CMD_LINE_ARGS=%*
goto doneStart

:setup4NT
set ANT_CMD_LINE_ARGS=%$
goto doneStart

:win9xStart
rem Slurp the command line arguments.  This loop allows for an unlimited number of 
rem agruments (up to the command line limit, anyway).

set ANT_CMD_LINE_ARGS=

:setupArgs
if %1a==a goto doneStart
set ANT_CMD_LINE_ARGS=%ANT_CMD_LINE_ARGS% %1
shift
goto setupArgs

:doneStart
java -classpath %CVSGRAB_HOME%\lib\hotsax.jar;%CVSGRAB_HOME%\lib\jcvsii-light.jar;%CVSGRAB_HOME%\lib\cvs-grab.jar net.sourceforge.cvsgrab.CVSGrab %ANT_CMD_LINE_ARGS%

if not "%OS%"=="Windows_NT" goto end
@endlocal

:end
