package com.ankush.assignment.http.webserver;

import com.ankush.assignment.http.webserver.httprequestprocessor.HttpRequestProcessor;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Th WebServer class
 */
public class WebServer {
    // Key for web server port in config properties
    public final static String PORT = "port";
    // Default Web server port
    private int _port = 80;
    // property indicating if server is running
    public boolean _running = true;
    // Key for maximum count of the threads allowed in web server pool
    public final static String THREADS = "threads";
    // Default maximum count of the threads in web server pool
    public int _threads = 40;

    // Key for folder path in config properties
    public final static String FOLDER_PATH= "root";
    // Default Folder Path
    public String _folder_path = ".";

    // Singleton instance of the web server
    private static final WebServer _instance = new WebServer();

    // Executor service to handle threads
    private ExecutorService _executor;

    /**
     * The Constructor method
     */
    private WebServer() {
        // initializing web server
        init();
    }

    /**
     * Method to Initialize web server
     */
    public void init() {
        // load the config properties
        Properties prop = loadProperties();
        // Initialize web server port
        _port = (Integer.parseInt((String) (prop.getOrDefault(PORT, "80"))));

        // Initialize threads
        _threads = (Integer.parseInt((String) (prop.getOrDefault(THREADS, "40"))));

        // Initialize folder path
        _folder_path = (String) (prop.getOrDefault(FOLDER_PATH, _folder_path));

        // Initialize executorService
        _executor = Executors.newFixedThreadPool(_threads);
    }

    /**
     * Method to start web server
     * @throws Exception when port is unavailable
     */
    public void start() throws Exception {

        // The socket which serves the clients
        ServerSocket _server_socket = new ServerSocket(_port);
        _running = true;

        while (_running) {
            // Listen for a TCP connection request.
            Socket connectionSocket = _server_socket.accept();
            // Construct object to process HTTP request message
            HttpRequestProcessor request = new HttpRequestProcessor(connectionSocket,_folder_path);

            Thread thread = new Thread(request);
            _executor.execute(thread);
        }

        _executor.shutdown();
    }

    /**
     * Method to load the properties from config
     * @return Properties from config.properties
     */
    private Properties loadProperties() {

        InputStream inputStream;
        Properties prop = new Properties();

        try {
            String propFileName = "config.properties";
            inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

            if (inputStream != null) {
                prop.load(inputStream);
                inputStream.close();
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }

        return prop;
    }

    /**
     * Method to get instance of the web server
     * @return instance of the web server
     */
    public static WebServer getInstance() {
        return _instance;
    }

    /**
     * main method - starting point for the app
     * @param args arguments
     * @throws Exception exception
     */
    public static void main(String[] args) throws Exception {
        WebServer webServer = WebServer.getInstance();
        webServer.start();
    }
}