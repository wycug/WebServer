package com.webserver.server;

import com.webserver.http.HttpParse;
import com.webserver.http.Request;
import com.webserver.http.RequestHandler;
import com.webserver.http.Response;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @auther wy
 * @create 2022/4/7 15:15
 */
public class WebServer implements Server {
    @Override
    public void start() throws IOException {
        HttpParse.getHttpServlet().setResourcePath(resourcePath);
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(port));
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(8, 12, 100, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(100));
        while (true){
            SocketChannel socketChannel = serverSocketChannel.accept();
            RequestHandler requestHandler = new RequestHandler();
            requestHandler.socket = socketChannel;
            requestHandler.request = new Request();
            requestHandler.response = new Response();
            threadPoolExecutor.execute(requestHandler);
        }
    }

}
