JUnit4Converter
===============

Convert JUnit3 Tests modules to JUnit4

I needed to convert many test files to JUnit4, after a while, I considered that is a really painful task to do it manually. So I wrote a little program to automate the process.
I tested this application on a huge number of files wrote by various Persons and having various formats, therefore I hope that I works on every possible JUnit module.
Please send me a short comment if you found a case, that I didn't included in it.

you can call the application by entering this command on Command Line:

java -jar JUnit4Converter.jar -f <your JUnit3 Java file>

this will printout the converted file to Console, if you wanted to do an Inplace Conversion, add "-i" to commands.

java -jar JUnit4Converter.jar -i -f <your JUnit3 Java file>
