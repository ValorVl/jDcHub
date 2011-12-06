REM @echo off

chcp 1251

set APP_DIR=.
set CLASSPATH=%APP_DIR%;%APP_DIR%\*;%APP_DIR%\lib\*

REM Reports Prefix
REM set REPORTS_PREFIX=-Dreport_prefix="payment-"

REM set properties here
set PROPERTIES=%SPREADSHEET_APP% %REPORTS_DIR% %REPORTS_PREFIX%

start javaw %PROPERTIES% -cp %CLASSPATH% ru.sincore.Main
