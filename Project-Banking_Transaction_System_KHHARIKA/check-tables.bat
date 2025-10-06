@echo off
echo Checking DynamoDB Local tables...
mvn clean compile exec:java -Dexec.mainClass="com.banking.util.TableChecker"
pause