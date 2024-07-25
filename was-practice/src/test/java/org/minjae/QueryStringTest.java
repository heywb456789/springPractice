package org.minjae;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * packageName       : org.minjae
 * fileName         : QueryStringTest
 * author           : MinjaeKim
 * date             : 2024-07-24
 * description      :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-07-24        MinjaeKim       최초 생성
 */
public class QueryStringTest {

    //operand1=11&operator=+&operand2=55
    //List<QueryString> 을 담기 위해 1급 컬렉션
    @Test
    void createTest() {
        QueryString queryString = new QueryString("operand","11");

        assertThat(queryString).isNotNull();
    }

    @Test
    void createListTest() {

    }
}
