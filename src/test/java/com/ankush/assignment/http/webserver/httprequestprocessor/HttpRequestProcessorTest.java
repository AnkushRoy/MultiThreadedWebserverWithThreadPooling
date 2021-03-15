package com.ankush.assignment.http.webserver.httprequestprocessor;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.Socket;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

// Unit test for httpRequestProcessorTest class

public class HttpRequestProcessorTest {
    @Test
    public void shouldReturnCorrectContentType() throws Exception {
        System.out.println("@Test - Should return the correct content type");

        Socket socket = mock(Socket.class);
        String _folder_path = ".";
        HttpRequestProcessor httpRequest = new HttpRequestProcessor(socket,_folder_path);

        // Get the private method of contentType and set it to public
        Method contentType = httpRequest.getClass().getDeclaredMethod("contentType", String.class);
        contentType.setAccessible(true);

        String testType_htm = (String)contentType.invoke(httpRequest, "test.htm");
        assertEquals(testType_htm, "text/html");

        String testType_html = (String)contentType.invoke(httpRequest, "test.html");
        assertEquals(testType_html, "text/html");

        String testType_text = (String)contentType.invoke(httpRequest, "test.txt");
        assertEquals(testType_text, "text/plain");

        String testType_jpg = (String)contentType.invoke(httpRequest, "test.jpg");
        assertEquals(testType_jpg, "image/jpg");

        String testType_gif = (String)contentType.invoke(httpRequest, "test.gif");
        assertEquals(testType_gif, "image/gif");

        String testType_svg = (String)contentType.invoke(httpRequest, "test.svg");
        assertEquals(testType_svg, "image/svg+xml");
    }

    @Test
    public void shouldNotSendDataIfFileIsEmpty() throws Exception {
        System.out.println("@Test - Should not send data");

        Socket socket = mock(Socket.class);
        String _folder_path = ".";
        HttpRequestProcessor httpRequest = new HttpRequestProcessor(socket,_folder_path);

        // Get the private method of sendBytes and set it to public
        Method sendBytes = httpRequest.getClass().getDeclaredMethod("sendBytes", FileInputStream.class, OutputStream.class);
        sendBytes.setAccessible(true);

        FileInputStream fis = mock(FileInputStream.class);

        when(fis.read(any(byte[].class))).thenReturn(-1);
        OutputStream ois = mock(OutputStream.class);

        sendBytes.invoke(httpRequest, fis, ois);
        verify(fis, times(1)).read(any(byte[].class));
        verify(ois, never()).write(any(byte[].class), anyInt(), anyInt());
    }

    @Test
    public void shouldSendDataIfFileIsNotEmpty() throws Exception {
        System.out.println("@Test - Should Send data");

        Socket socket = mock(Socket.class);
        String _folder_path = ".";
        HttpRequestProcessor httpRequest = new HttpRequestProcessor(socket,_folder_path);

        // Get the private method of sendBytes and set it to public
        Method sendBytes = httpRequest.getClass().getDeclaredMethod("sendBytes", FileInputStream.class, OutputStream.class);
        sendBytes.setAccessible(true);

        FileInputStream fis = mock(FileInputStream.class);

        when(fis.read(any(byte[].class))).thenReturn(1024).thenReturn(-1);
        OutputStream ois = mock(OutputStream.class);
        doNothing().when(ois).write(any(byte[].class), anyInt(), anyInt());

        sendBytes.invoke(httpRequest, fis, ois);
        verify(fis, times(2)).read(any(byte[].class));
        verify(ois, times(1)).write(any(byte[].class), anyInt(), anyInt());
    }
}
