:: Getting the port from the config.properties file
For /F "tokens=1* delims==" %%A IN (config.properties) DO (
    IF "%%A"=="port" set port=%%B
)

:: Starting chrome with that port and seperate userdata directory
start "" "C:\Program Files\Google\Chrome\Application\chrome.exe" --remote-debugging-port=%port% --user-data-dir=%cd%/userdatadir
