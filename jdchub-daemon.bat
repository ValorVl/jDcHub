REM @echo off

chcp 1251

REM Detect users Desktop folder
for /f "tokens=*" %%a in ('mshta "javascript:new ActiveXObject('Scripting.FileSystemObject').GetStandardStream(1).Write(new ActiveXObject('WScript.Shell').SpecialFolders('Desktop'));close();"') do set "Desktop=%%a"

set APP_DIR=.
set CLASSPATH=%APP_DIR%;%APP_DIR%\*;%APP_DIR%\lib\*

REM Spreadsheet Application
set SPREADSHEET_APP=-Dspreadsheet_app="start excel.exe"

REM Reports Dir
set REPORTS_DIR=-Dreport_dir="%Desktop%"
REM set REPORTS_DIR=-Dreport_dir="%USERPROFILE%"
REM set REPORTS_DIR=-Dreport_dir="C:\������"

REM Reports Prefix
REM set REPORTS_PREFIX=-Dreport_prefix="payment-"

REM set properties here
set PROPERTIES=%SPREADSHEET_APP% %REPORTS_DIR% %REPORTS_PREFIX%

start javaw %PROPERTIES% -cp %CLASSPATH% ru.sincore.Main
