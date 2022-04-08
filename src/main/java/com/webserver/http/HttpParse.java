package com.webserver.http;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Locale;

/**
 * @auther wy
 * @create 2022/4/5 20:26
 */
@Slf4j
public class HttpParse {
    private static volatile HttpParse httpParse;
    private static String resourcePath;
    HttpParse(){

    }

    public static String getResourcePath() {
        return resourcePath;
    }

    public static void setResourcePath(String resourcePath) {
        HttpParse.resourcePath = resourcePath;
    }

    public static HttpParse getHttpServlet(){
        if(httpParse==null){
            synchronized (HttpParse.class){
                if (httpParse==null){
                    resourcePath = HttpParse.class.getClassLoader().getResource("").getPath();
                    httpParse = new HttpParse();
                }
            }
        }
        return httpParse;
    }
//    public Response doServer(Request request, Response response){
////        String file = serletMap.get(request.getHost());
//
//        partialFile(request, response);
//        return  response;
//    }

    public void partialFile(Request request, Response response){
        log.debug(request.getHeader("Host")+"  "+resourcePath+request.getPath());
        File file = new File(resourcePath + request.getPath());
        response.setContentType(matchContentType(request.getPath()));


        try {
            InputStream inputStream =new FileInputStream(resourcePath+request.getPath());
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
            log.debug(resourcePath+request.getPath());
            byte[] buffer = new byte[1024];
            int len = 0;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            while((len = inputStream.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            bos.close();
            response.setBody(bos.toByteArray());
            response.setState("200");
        } catch (Exception e) {
            log.error("404", e);
            response.setState("404");
            response.setBody("<html>404<html>".getBytes(response.getCharset()));
        }
    }

    /*text/html ： HTML格式
    text/plain ：纯文本格式
    text/xml ： XML格式
    image/gif ：gif图片格式
    image/jpeg ：jpg图片格式
    image/png：png图片格式*/
    private final String TEXT_HTML = "text/html";
    private final String TEXT = "text/plain";
    private final String TEXT_XML = "text/xml";
    private final String IMAGE_GIF = "image/gif";
    private final String IMAGE_PNG = "image/png";
    private final String IMAGE_JEPG = "image/jpeg";
    private final String TEXT_CSS = "text/css";
    private final String TEXT_JS = "text/javascrip";
    private String matchContentType(String path){
        int i;
        path = path.trim();
        int len = path.length();
        String type = "";
        for(i = len-1;i>=0;i--){
            if(path.charAt(i)=='.'){
                type = path.substring(i+1, len);
                break;
            }
        }
        type = type.toLowerCase(Locale.ROOT);
        log.debug(type);
        if (type.equals("html") || type.equals("htm")){
            return TEXT_HTML;
        }
        if (type.equals("css")){
            return TEXT_CSS;
        }
        if (type.equals("js")){
            return TEXT_JS;
        }
        if (type.equals("jpg")){
            return IMAGE_JEPG;
        }
        if (type.equals("png")){
            return IMAGE_PNG;
        }
        if (type.equals("gif")){
            return IMAGE_JEPG;
        }
        return TEXT;
    }
}
