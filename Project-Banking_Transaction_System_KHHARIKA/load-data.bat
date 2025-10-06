@echo off
echo Loading sample data into Banking System...
mvn clean compile exec:java -Dexec.mainClass="com.banking.service.DataLoaderService"
pause