package com.ankush.assignment.http.webserver;

import org.junit.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Properties;
import java.util.concurrent.ExecutorService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

// Unit test for WebServer class
public class WebServerTest {
    @Test
    public void shouldLoadProperties() throws Exception{
        System.out.println("@Test - Should load properties from config.properties");

        Object webserver = WebServer.getInstance();
        // Get the private method of loadProperties and set it to public
        Method loadProperties = webserver.getClass().getDeclaredMethod("loadProperties");
        loadProperties.setAccessible(true);

        Properties expected = new Properties();
        expected.put("port","80");
        expected.put("threads","40");
        expected.put("root",".");

        Properties properties = (Properties)loadProperties.invoke(webserver);
        assertNotNull(properties);
        assertEquals(expected, properties);
    }

    @Test
    public void shouldInit() throws Exception{
        System.out.println("@Test - Init the web server");

        Object webserver = WebServer.getInstance();
        // Get the public method of init
        Method init = webserver.getClass().getDeclaredMethod("init");

        init.invoke(webserver);
        // Get the private executor and set it to public
        Field threadPool = webserver.getClass().getDeclaredField("_executor");
        threadPool.setAccessible(true);

        ExecutorService pool = (ExecutorService)threadPool.get(webserver);
        assertNotNull(pool);
    }
}