@echo off
echo Compiling and running Banking System...
mvn clean compile exec:java -Dexec.mainClass="com.banking.app.BankingApp"
pause