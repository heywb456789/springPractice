package org.minjae;

import java.io.IOException;

/**
 * packageName       : org.minjae
 * fileName         : ${NAME}
 * author           : MinjaeKim
 * date             : 2024-07-24
 * description      :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-07-24        MinjaeKim       최초 생성
 */
// GET /calculate?operand1=11&operator=+&operand2=55
public class Main {
    public static void main(String[] args) throws IOException {
        new CustomWebApplicationServer(8080).start();
    }
}