Download this entire folder. Make sure that Ngrams.java and mysql-connector-java-5.1.42-bin.jar are in the same folder.

Running Ngrams.java on a text file (On Windows):
	1. Open command prompt and navigate to the directory Ngrams.java is stored in.
	2. Once in the directory, execute the following commands:
		javac Ngrams.java
		java Ngrams
	3. The program will ask you for the max n-gram you want to find (N). This will find n!-grams.
	4. The program will ask you for the number of top results you want (T). The program will print T reults for n!-grams.
	5. The program will ask you if you want to run from a database. 
	6. Type n
	7. The program will ask for the name of the .txt file.
	8. Type the filename (e.g. test.txt) NOTE: THE .TXT FILE MUST BE IN THE SAME DIRECTORY AS THE .JAVA FILE
	9. The program will run and output your results to the command window. (stdout)

Running Ngrams.java on a database (On Windows):
	1. Download the database you want to use to a .sql file.
		On mySQL, click the database you want to use, click the EXPORT tab, select the tables you want to use
		and click "export to file". Choose .sql and click Go.
	2. Download a program called XAMPP
	3. Open XAMPP and run Apache and mySQL
	4. In your web browser, type localhost/phpmyadmin
	5. Create a new database on the left and name it test
	6. Select your new database and click the IMPORT tab
	7. Upload the .sql file you exported earlier and click Go
	8. Open command prompt and navigate to the directory Ngrams.java is stored in.
	9. Once in the directory, execute the following commands:
		javac -cp mysql-connector-java-5.1.42-bin.jar;. Ngrams.java
		java -cp mysql-connector-java-5.1.42-bin.jar;. Ngrams
	3. The program will ask you for the max n-gram you want to find (N). This will find n!-grams.
	4. The program will ask you for the number of top results you want (T). The program will print T reults for n!-grams.
	5. The program will ask you if you want to run from a database. 
	6. The program will ask you for the URL. Type localhost/test
	7. The program will ask you for your username. Type root
	8. The program will ask you for your password. Just press enter (Password is "")
	9. The program will ask you for the sql query you want to execute. 
		e.g. to get all messages from the post table, type: select message from posts
	10. The program will run and output your results to the command window. (stdout)
