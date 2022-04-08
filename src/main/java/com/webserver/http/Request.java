package com.webserver.http;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * @auther wy
 * @create 2022/4/5 16:39
 */
public class Request {
    private Charset charset = Charset.defaultCharset();
    private String path;
    private final Map<String, String> headers = new HashMap<>();
    private String HttpMethod;
    private String body;
    private String protocol;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Map<String, String> getHeaders(){return headers;}

    public String getHeader(String key) {
        return headers.get(key);
    }

    public void setHeader(String key, String value) {
        this.headers.put(key, value);
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getHttpMethod() {
        return HttpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        HttpMethod = httpMethod;
    }

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
}
