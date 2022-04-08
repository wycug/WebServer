package com.webserver;

import com.webserver.server.Server;
import com.webserver.server.WebNIOServer;

import java.io.IOException;

/**
 * @auther wy
 * @create 2022/4/8 10:33
 */
public class main {
    public static void main(String[] args) throws IOException {
        Server server = new WebNIOServer();
        server.start();
    }
}
