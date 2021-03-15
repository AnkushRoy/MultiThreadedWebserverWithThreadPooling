# Java based Webserver
A multi-threaded file-based web server with thread pooling implemented in java.
Thread pooling has been implemented by leveraging Java Executor.

## Application details:

* A multi-threaded file-based web server with thread pool and Executor Service.
* Displays supported content types(txt/image) in a browser and download un-supported files.
* Supports only two http methods `HEAD` and `GET`.
* Supports following file extensions : `.txt`, `.html`, `.htm`, `.jpg`, `.png`, `.gif` and `.svg`. 
* Supports keep alive.

### Instructions on how to run the web server
* This project assumes java and maven are installed in the system where the project is being tested.
* Go to the main folder and run `mvn clean package`. This will run the test and compile the code.
* Run `mvn exec:java` to run the main file of the application.
* The default `port` has been set to `80` and default `folder_path` is `.` 
  * Use cases:
    * Visit `http://localhost/src/main/resources/content/random.txt`.
      * Displays the content of the text file in the browser.
    * Visit `http://localhost/src/main/resources/content/AnkushRoy.jpg`.
      * Displays the jpg file in the browser.
    * Visit `http://localhost/src/main/resources/content/NotFound.html`.
      * Displays error message `Not Found on the java WebServer` in the browser.
    * Visit `http://localhost`.
        * Displays error message `Not Found on the java WebServer` in the browser
* The default properties `port`, `threads` and `folderpath` is set in `src\main\resources\config.properties`.
* To exit the application, use `CTRl +C` and type `Y` when asked to stop the batch process.