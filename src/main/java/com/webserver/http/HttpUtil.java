package com.webserver.http;

import java.io.UnsupportedEncodingException;

/**
 * Created by jjenkov on 19-10-2015.
 * modified by wy on 06-04-2022
 */
public class HttpUtil {

    private static final byte[] GET    = new byte[]{'G','E','T'};
    private static final byte[] POST   = new byte[]{'P','O','S','T'};
    private static final byte[] PUT    = new byte[]{'P','U','T'};
    private static final byte[] HEAD   = new byte[]{'H','E','A','D'};
    private static final byte[] DELETE = new byte[]{'D','E','L','E','T','E'};

    private static final byte[] HOST           = new byte[]{'H','o','s','t'};
    private static final byte[] CONTENT_LENGTH = new byte[]{'C','o','n','t','e','n','t','-','L','e','n','g','t','h'};

    public static int parseHttpRequest(byte[] src, int startIndex, int endIndex, Request request) {



//        int endOfHttpMethod = findNext(src, startIndex, endIndex, (byte) ' ');
//        if(endOfHttpMethod == -1) return -1;
//        resolveHttpMethod(src, startIndex, request);


        //parse HTTP request line
//        int endOfFirstLine = findNextLineBreak(src, 0, endIndex);
        int endOfFirstLine = praseFirstLine(src, startIndex,endIndex, request);
        if(endOfFirstLine == -1) return -1;


        //parse HTTP headers
        int prevEndOfHeader = endOfFirstLine + 1;
        int endOfHeader = findNextLineBreak(src, prevEndOfHeader, endIndex);
        int contextLength = 0;
        while(endOfHeader != -1 && endOfHeader != prevEndOfHeader + 1){    //prevEndOfHeader + 1 = end of previous header + 2 (+2 = CR + LF)

            if(matches(src, prevEndOfHeader, CONTENT_LENGTH)){
                try {
                    contextLength = findContentLength(src, prevEndOfHeader, endIndex, request);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }else{
                int endOfKey = findNext(src, prevEndOfHeader, endOfHeader, (byte) ':');
                request.setHeader(new String(src, prevEndOfHeader, endOfKey - prevEndOfHeader,request.getCharset()), new String(src, endOfKey+2, endOfHeader - endOfKey-2,request.getCharset()));
            }

            prevEndOfHeader = endOfHeader + 1;
            endOfHeader = findNextLineBreak(src, prevEndOfHeader, endIndex);
        }

        if(endOfHeader == -1){
            return -1;
        }



        //check that byte array contains full HTTP message.
        int bodyStartIndex = endOfHeader + 1;
//        int bodyEndIndex  = bodyStartIndex + httpHeaders.contentLength;
        int bodyEndIndex = bodyStartIndex + contextLength;


        if(bodyEndIndex <= endIndex){
            //byte array contains a full HTTP request
//            httpHeaders.bodyStartIndex = bodyStartIndex;
//            httpHeaders.bodyEndIndex   = bodyEndIndex;
            request.setBody(new String(src, bodyStartIndex, bodyEndIndex- bodyStartIndex, request.getCharset()));
            return bodyEndIndex;
        }


       return -1;
    }

    private static int findContentLength(byte[] src, int startIndex, int endIndex, Request request) throws UnsupportedEncodingException {
        int indexOfColon = findNext(src, startIndex, endIndex, (byte) ':');

        //skip spaces after colon
        int index = indexOfColon +1;
        while(src[index] == ' '){
            index++;
        }

        int valueStartIndex = index;
        int valueEndIndex   = index;
        boolean endOfValueFound = false;

        while(index < endIndex && !endOfValueFound){
            switch(src[index]){
                case '0' : ;
                case '1' : ;
                case '2' : ;
                case '3' : ;
                case '4' : ;
                case '5' : ;
                case '6' : ;
                case '7' : ;
                case '8' : ;
                case '9' : { index++;  break; }

                default: {
                    endOfValueFound = true;
                    valueEndIndex = index;
                }
            }
        }

//        httpHeaders.contentLength = Integer.parseInt(new String(src, valueStartIndex, valueEndIndex - valueStartIndex, "UTF-8"));
//        request.setBody(new String(src, valueStartIndex, valueEndIndex - valueStartIndex, request.getCharset()));
        return Integer.parseInt(new String(src, valueStartIndex, valueEndIndex - valueStartIndex, request.getCharset()));
    }


    public static int findNext(byte[] src, int startIndex, int endIndex, byte value){
        for(int index = startIndex; index < endIndex; index++){
            if(src[index] == value) return index;
        }
        return -1;
    }

    public static int findNextLineBreak(byte[] src, int startIndex, int endIndex) {
        for(int index = startIndex; index < endIndex; index++){
            if(src[index] == '\n'){
                if(src[index - 1] == '\r'){
                    return index;
                }
            };
        }
        return -1;
    }

    public static int praseFirstLine(byte[]src, int startIndex, int endIndex,Request request){

        int endOfMethod = findNext(src, startIndex, endIndex, (byte) ' ');

        if(endOfMethod==-1){
            return -1;
        }
        request.setHttpMethod(new String(src,startIndex, endOfMethod, request.getCharset()));
        startIndex = endOfMethod + 1;
        int endOfPath = findNext(src, startIndex, endIndex, (byte) ' ');
        if (endOfPath==-1){
            return -1;
        }
        request.setPath(new String(src, startIndex+1,endOfPath- startIndex, request.getCharset()));
        startIndex = endOfPath + 1;
        int endOfProtocol = findNext(src, startIndex, endIndex, (byte) '\r');
        if (endOfProtocol==-1){
            return -1;
        }
        request.setProtocol(new String(src, startIndex, endOfProtocol - startIndex, request.getCharset()));
        startIndex = endOfProtocol+1;
        if(src[startIndex] == '\n'){
            if(src[startIndex - 1] == '\r'){
                return startIndex;
            }
        }
        return -1;
    }



    public static boolean matches(byte[] src, int offset, byte[] value){
        for(int i=offset, n=0; n < value.length; i++, n++){
            if(src[i] != value[n]) return false;
        }
        return true;
    }
}
