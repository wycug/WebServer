package com.webserver.http;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @auther wy
 * @create 2022/4/7 15:24
 */
@Slf4j
public class RequestHandler implements Runnable{
    public SocketChannel socket;
    public Request request;
    public Response response;
    public ArrayBlockingQueue<SocketChannel> queue;
    @Override
    public void run() {
        ByteBuffer buffer = ByteBuffer.allocate(4096);
        int offset = 0;
        byte[] src = new byte[4096];
        try {
            while (true) {
                int read = socket.read(buffer);
                if (read>0){
                    buffer.flip();
                    if(src.length<offset+read){
                        src = extendBytes(src, offset);
                    }
                    buffer.get(src, offset, read);
                    buffer.clear();
                    log.debug(new String(src));
                    int end= HttpUtil.parseHttpRequest(src, 0, read, request);
                    if (end==-1){
                        offset = read;
                        continue;
                    }
                    offset = 0;
                    src = new byte[4096];
//                request.setHeader("Connection", "close");
                    HttpParse.getHttpServlet().partialFile(request, response);
                    byte[] bytes = response.getResponse(request, response);
                    log.debug(new String(bytes));
                    socket.write(ByteBuffer.wrap(bytes));
                }else{
                    socket.close();
                    queue.remove(socket);
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public byte[] extendBytes(byte[]src, int offset){
        byte[] newBytes = new byte[src.length + 4096];
        System.arraycopy(src, 0, newBytes, 0, offset);
        return newBytes;
    }

}
