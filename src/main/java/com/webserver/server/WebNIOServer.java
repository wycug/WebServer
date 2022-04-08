package com.webserver.server;

import com.webserver.http.HttpParse;
import com.webserver.http.Request;
import com.webserver.http.RequestHandler;
import com.webserver.http.Response;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @auther wy
 * @create 2022/4/7 21:28
 */
public class WebNIOServer implements Server {
    @Override
    public void start() throws IOException {
        HttpParse.getHttpServlet().setResourcePath(resourcePath);
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
//        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress(port));
        Selector selector = Selector.open();

        Thread thread = new Thread(() -> {
            ThreadPoolExecutor readThreadPoolExecutor = new ThreadPoolExecutor(8, 12, 100, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(100));
            ArrayBlockingQueue<SocketChannel> queue  =new ArrayBlockingQueue<>(100);
            while (true){
                try {
                    if (selector.selectNow()>0){
                        Set<SelectionKey> selectionKeys = selector.selectedKeys();
                        Iterator<SelectionKey> iterator = selectionKeys.iterator();
                        while (iterator.hasNext()){
                            SelectionKey next = iterator.next();
                            if (next.isReadable()&&!queue.contains((SocketChannel)next.channel())){
                                RequestHandler requestHandler = new RequestHandler();
                                SocketChannel sc = (SocketChannel) next.channel();
                                requestHandler.socket = sc;
                                requestHandler.request = new Request();
                                requestHandler.response = new Response();
                                requestHandler.queue = queue;
//                            requestHandler.run();
                                queue.add(sc);
                                readThreadPoolExecutor.execute(requestHandler);
                            }
                            if (!queue.contains((SocketChannel)next.channel())){
                                iterator.remove();
                            }
//

                        }
//                        selectionKeys.clear();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        thread.start();
        while (true){
            try {
                SocketChannel socketChannel = serverSocketChannel.accept();
                socketChannel.configureBlocking(false);
                SelectionKey key = socketChannel.register(selector, SelectionKey.OP_READ);
//                key.attach(socketChannel);
            }catch (Exception e){
//                e.printStackTrace();
            }
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            }catch (Exception e){

            }

        }
    }

}
