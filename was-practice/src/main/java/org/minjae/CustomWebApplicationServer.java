package org.minjae;


import org.minjae.calculator.Calculator;
import org.minjae.calculator.PositiveNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    private final ExecutorService executor = Executors.newFixedThreadPool(10);

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
                 * 사용자 요청 -> step2 사용자 요청이 들어올 때마다 Thread 새로 생성해서 사용자 요청을 처리하도록 한다.
                 * 생성시마다 독립적인 스택 메모리 할당 (메모리 할당 작업은 비싼 작업 ) => 성능 저하 유발
                 * 동시 접속자 수가 많아질 경우 많은 Thread 생성이 되는데 많아지면 CPU 컨텍스트 스위칭 횟수와 CPU 메모리 사용량이 증가
                 * ThreadPool을 사용해 안정적인 서비스를 구현할 수 있도록 변경
                 */
//                new Thread(new ClientRequestHandler(clientSocket)).start();

                //ThreadPool 적용
                executor.execute(new ClientRequestHandler(clientSocket));
            }
        }
    }
}
