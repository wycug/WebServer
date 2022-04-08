package com.webserver.http;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * @auther wy
 * @create 2022/4/5 20:32
 */
public class Response {

    private final char[] CONTEXT_LENGTH = new char[]{'C', 'o', 'n', 't', 'e', 'n', 't', '-', 'L', 'e', 'n', 'g', 't', 'h'};


    private Charset charset = Charset.defaultCharset();
    private String host;
    private final Map<String, String> headers = new HashMap<>();
    private String state;
    private byte[] body;
    private String protocol;
    private String contentType;

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }


    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getHeaders(String key) {
        return headers.get(key);
    }
    public void setHeaders(String key, String value) {
        headers.put(key, value);
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public  byte[] getResponse(Request request, Response response){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(request.getProtocol()+" "+response.getState()+" Ok\r\n");

        for(String key: request.getHeaders().keySet()){
            stringBuilder.append(key+": "+request.getHeader(key)+"\r\n");
        }
        stringBuilder.append("Content-Length: "+response.getBody().length+"\r\n");

        stringBuilder.append("Content-Type: "+ response.getContentType()+"\r\n");
        stringBuilder.append("\r\n");

        byte [] headbyes = stringBuilder.toString().getBytes(response.getCharset());
        byte [] responseBytes = new byte[headbyes.length + response.getBody().length];
        System.arraycopy(headbyes, 0, responseBytes, 0, headbyes.length);
        System.arraycopy(response.getBody(), 0, responseBytes, headbyes.length, response.getBody().length);
        return responseBytes;
    }
}
