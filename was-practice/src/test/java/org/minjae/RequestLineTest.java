package org.minjae;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * packageName       : PACKAGE_NAME
 * fileName         : org.minjae.RequestLineTest
 * author           : MinjaeKim
 * date             : 2024-07-24
 * description      :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-07-24        MinjaeKim       최초 생성
 */
public class RequestLineTest {

    @DisplayName("생성")
    @Test
    void create() {
        RequestLine requestLine = new RequestLine("GET /calculate?operand1=11&operator=+&operand2=55 HTTP/1.1");

        assertThat(requestLine).isNotNull();
        assertThat(requestLine).isEqualTo(new RequestLine("GET","/calculate" , "operand1=11&operator=+&operand2=55"));
    }
}
