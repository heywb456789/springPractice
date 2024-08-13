package org.minjae;

import org.minjae.calculator.Calculator;
import org.minjae.calculator.PositiveNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * packageName       : org.minjae
 * fileName         : ClientRequestHandler
 * author           : MinjaeKim
 * date             : 2024-08-13
 * description      :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-08-13        MinjaeKim       최초 생성
 */
public class ClientRequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ClientRequestHandler.class);

    private final Socket clientSocket;

    public ClientRequestHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        /**
         * 사용자 요청 -> step2 사용자 요청이 들어올 때마다 Thread를 새로 생성해서 사용자 요청을 처리하도록 한다.
         */
        logger.info("Client request handler started {}", Thread.currentThread().getName());
        try (InputStream in = clientSocket.getInputStream(); OutputStream out = clientSocket.getOutputStream()) {

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
            if (httpRequest.isGetRequest() && httpRequest.matchPath("/calculate")) {
                QueryStrings queryStrings = httpRequest.getQueryString();

                int operand1 = Integer.parseInt(queryStrings.getValue("operand1"));
                String operator = queryStrings.getValue("operator");
                int operand2 = Integer.parseInt(queryStrings.getValue("operand2"));

                int result = Calculator.calculate(new PositiveNumber(operand1), operator, new PositiveNumber(operand2));
                byte[] body = String.valueOf(result).getBytes();


                HttpResponse response = new HttpResponse(dos);
                response.response200Header("application/json", body.length);
                response.response200Body(body);
            }

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
