@echo off
echo Testing Banking App without DynamoDB Local...
echo Note: This will fail to connect to DynamoDB but will show if code compiles
mvn clean compile
echo.
echo To run with real DynamoDB:
echo 1. Download DynamoDB Local from AWS
echo 2. Extract DynamoDBLocal.jar to this folder
echo 3. Run: java -Djava.library.path=./DynamoDBLocal_lib -jar DynamoDBLocal.jar -sharedDb
pause