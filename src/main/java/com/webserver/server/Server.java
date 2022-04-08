package com.webserver.server;

import java.io.IOException;

/**
 * @auther wy
 * @create 2022/4/8 10:27
 */
public interface Server {
    int port = 8080;
    String resourcePath = "D:\\javaProject\\java-nio-server\\WebServer\\src\\main\\resources\\";
    public void start() throws IOException;
}
