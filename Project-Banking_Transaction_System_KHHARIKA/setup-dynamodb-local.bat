@echo off
echo Setting up DynamoDB Local...
echo.
echo Step 1: Download DynamoDB Local
echo Go to: https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DynamoDBLocal.DownloadingAndRunning.html
echo Download the .zip file
echo.
echo Step 2: Extract to your project folder
echo Extract DynamoDBLocal.jar and DynamoDBLocal_lib folder to: %cd%
echo.
echo Step 3: Start DynamoDB Local
echo Run: java -Djava.library.path=./DynamoDBLocal_lib -jar DynamoDBLocal.jar -sharedDb
echo.
echo Step 4: Run your banking app
echo Your app will connect to local DynamoDB at http://localhost:8000
echo.
pause