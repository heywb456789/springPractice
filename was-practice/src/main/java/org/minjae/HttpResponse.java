package org.minjae;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * packageName       : org.minjae
 * fileName         : HttpResponse
 * author           : MinjaeKim
 * date             : 2024-07-29
 * description      :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-07-29        MinjaeKim       최초 생성
 */
public class HttpResponse {
    private static final Logger logger = LoggerFactory.getLogger(HttpResponse.class);

    private final DataOutputStream out;

    public HttpResponse(DataOutputStream out) {
        this.out = out;
    }

    public void response200Header(String contentType, int lengthOfBodyContent){
        try{
            out.writeBytes("HTTP/1.1 200 OK \r\n");
            out.writeBytes("Content-Type: "+ contentType + ";charset=utf-8\r\n");
            out.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            out.writeBytes("\r\n");
        }catch (IOException e){
            logger.error(e.getMessage());
        }
    }
    public void response200Body(byte[] data){
        try {
            out.write(data, 0, data.length);
            out.flush();
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }
}
