package com.ankush.assignment.http.webserver.httprequestprocessor;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Http Request Processor Class
 */
public final class HttpRequestProcessor implements Runnable {


    final static String CRLF = "\r\n";
    public static final int HTTP_BAD_METHOD = 405;
    private final Socket socket;
    private final String _folder_path;

    /**
     * Constructor Method
     * @param socket Socket to run the web server
     * @param folder_path Folder Path for the web server
     */
    public HttpRequestProcessor(Socket socket, String folder_path) {
        this.socket = socket;
        this._folder_path = folder_path;
    }

    /**
     * Method implementing the run() method of the Runnable interface.
     */
    public void run() {
        try {
            processRequest();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to processRequest
     * @throws Exception in case of any exception
     */
    private void processRequest() throws Exception {
        //Get Input and Output Stream
        InputStream is = socket.getInputStream();
        DataOutputStream os = new DataOutputStream(socket.getOutputStream());

        // Set up input stream.
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        // In http 1.1, by default keep alive is on
        boolean keepAlive = true;

        do {
            String requestLine = br.readLine();
            if (requestLine == null || requestLine.equals(""))
            {
                // If the coming request is null, stop serving this request and move on to the next one
                break;
            }

            // String Tokenizer is used to extract file name from this class.
            StringTokenizer tokens = new StringTokenizer(requestLine);
            // Get the request type
            String requestType = tokens.nextToken();

            String HTTP_VERB_GET = "GET";
            String HTTP_VERB_HEAD = "HEAD";
            if (!HTTP_VERB_GET.equalsIgnoreCase(requestType) && !(HTTP_VERB_HEAD.equalsIgnoreCase(requestType))) {
                os.writeBytes("HTTP/1.0 "+ HTTP_BAD_METHOD + " unsupported method type: ");
                os.writeBytes(requestLine.substring(0, 5));
                os.writeBytes(CRLF);
                os.flush();
                socket.close();
            }

            String fileName = tokens.nextToken();
            // Process the header to check if keep alive is disabled
            String headerLine;
            while ((headerLine = br.readLine()).length() != 0) {
                if (headerLine.equalsIgnoreCase("Connection: close")) {
                    keepAlive = false;
                }
            }

            // Open the requested file.
            File fileRequested = null;
            FileInputStream fis = null;
            boolean fileExists;
            String fileWithPath = _folder_path + File.separator + fileName;
            try {
                fileRequested = new File(fileWithPath);
                fis = new FileInputStream(fileRequested);
                fileExists = true;
            } catch (FileNotFoundException e) {
                fileExists = false;
            }

            // Construct the response message
            StringBuilder header = new StringBuilder();
            String statusLine; // Set initial values to null
            String contentTypeLine;
            String entityBody = null;

            if (fileExists) {
                statusLine = "HTTP/1.1 200 OK" + CRLF;
                contentTypeLine = "Content-Type: " + contentType(fileName) + CRLF;

            } else {
                statusLine = "HTTP/1.1 404 Not Found" + CRLF;
                contentTypeLine = "Content-Type: text/html" + CRLF;
                entityBody = "<HTML> <HEAD><TITLE>Not Found</TITLE></HEAD> <BODY>Not Found on the java WebServer</BODY></HTML>";
            }
            // End of response message construction

            // Send the status line.
            header.append(statusLine);
            // Send the content type line.
            header.append(contentTypeLine);

            if (keepAlive) {
                header.append("Connection: keep-alive" + CRLF);
            } else {
                header.append("Connection: close" + CRLF);
            }

            // Send the entity body.
            if (fileExists) {
                header.append("Content-Length: ").append(fileRequested.length()).append(CRLF);
                // Send a blank line to indicate the end of the header lines.
                header.append(CRLF);
                os.writeBytes(header.toString());
                sendBytes(fis, os);
                os.flush();
                fis.close();
            } else {
                header.append("Content-Length: ").append(entityBody.length()).append(CRLF);
                // Send a blank line to indicate the end of the header lines.
                header.append(CRLF);
                os.writeBytes(header.toString());
                os.writeBytes(entityBody);
                os.flush();
            }

            if (!keepAlive) {
                os.close(); // Close streams and socket.
                br.close();
                socket.close();
            }
        }
        while (keepAlive);
    }

    /**
     * Method to send bytes
     * @param fis File Input Stream
     * @param os Output Stream
     * @throws Exception exception
     */
    private static void sendBytes(FileInputStream fis, OutputStream os) throws Exception {
        // Construct a 1K buffer to hold bytes on their way to the socket.
        byte[] buffer = new byte[1024];
        int bytes;

        // Copy requested file into the socket’s output stream.
        while ((bytes = fis.read(buffer)) != -1) {
            os.write(buffer, 0, bytes);
        }
    }

    /**
     * Method to get the content type
     * @param fileName File NAme
     * @return content type
     */
    private static String contentType(String fileName) {
        String contentType = "application/octet-stream";
        if (fileName.endsWith(".htm") || fileName.endsWith(".html")) {
            contentType = "text/html";
        } else if (fileName.endsWith(".txt")) {
            contentType = "text/plain";
        }else if (fileName.endsWith(".jpg")) {
            contentType = "image/jpg";
        } else if (fileName.endsWith(".gif")) {
            contentType = "image/gif";
        } else if (fileName.endsWith(".png")) {
            contentType = "image/png";
        } else if (fileName.endsWith(".svg")) {
            contentType = "image/svg+xml";
        }

        return contentType;
    }
}