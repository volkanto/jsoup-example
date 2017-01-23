### About
- - - -
This project creates some analysis of a given web page. These information are:
		* HTML version of page
		* Page title
		* Number of headings grouped by heading level
		* Number of internal hypermedia links
		* Number of external hypermedia links
		* The page contains login page or not

### How to run
- - - -
Go to project directory and run this command: `mvn clean install package`

The code above creates jar file of related project. Then, you can go to `target` directory,  you will see `immobilien-scout-0.0.1.jar` 

Now, you can run project with this command: `java -jar immobilien-scout-0.0.1.jar`

Finally, the project is ready to go. Open your favourite web browser and type `http://localhost:8080` to address bar.

### Notes
- - - -
		* Application returns error when you give unavailable web site or not valid url.
