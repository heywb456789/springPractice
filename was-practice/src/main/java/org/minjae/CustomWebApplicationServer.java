package org.minjae;


import org.minjae.calculator.Calculator;
import org.minjae.calculator.PositiveNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * packageName       : org.minjae
 * fileName         : CustomWebApplicationServer
 * author           : MinjaeKim
 * date             : 2024-07-24
 * description      :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-07-24        MinjaeKim       최초 생성
 */
public class CustomWebApplicationServer {

    private final int port;

    private static final Logger logger = LoggerFactory.getLogger(CustomWebApplicationServer.class);

    public CustomWebApplicationServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        try(ServerSocket serverSocket = new ServerSocket(port)){
            logger.info("Custom Web Application Server started on port {}" , port);

            Socket clientSocket;
            logger.info("Custom Web Application Server waiting for Client");

            //클라이언트를 기다리고 널이 아니면 while 문으로 접속
            while ((clientSocket = serverSocket.accept()) != null) {
                logger.info("client connected");

                /**
                 * 사용자 요청
                 */
                try(InputStream in = clientSocket.getInputStream(); OutputStream out = clientSocket.getOutputStream()){

                    BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
                    DataOutputStream dos = new DataOutputStream(out);

                    /**
                     * GET / HTTP/1.1
                     * User-Agent: IntelliJ HTTP Client/IntelliJ IDEA 2024.1
                     * Accept-Encoding: br, deflate, gzip, x-gzip
                     * Accept:
                     * host:
                     * localhost:8080
                     * content - length:0
                     */
//                    String line;
//                    while((line = reader.readLine()) != null) {
//                        System.out.println(line);
//                    }
                    HttpRequest httpRequest = new HttpRequest(reader);
                    if(httpRequest.isGetRequest() && httpRequest.matchPath("/calculate")){
                        QueryStrings queryStrings = httpRequest.getQueryString();

                        int operand1 = Integer.parseInt(queryStrings.getValue("operand1"));
                        String operator = queryStrings.getValue("operator");
                        int operand2 = Integer.parseInt(queryStrings.getValue("operand2"));

                        int result = Calculator.calculate(new PositiveNumber(operand1), operator, new PositiveNumber(operand2));
                        byte[] body = String.valueOf(result).getBytes();


                        HttpResponse response = new HttpResponse(dos);
                        response.response200Header("application/json", body.length);
                    }

                }

            }
        }
    }
}
