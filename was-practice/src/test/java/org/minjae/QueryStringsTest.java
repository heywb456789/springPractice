package org.minjae;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * packageName       : org.minjae
 * fileName         : QueryStringsTest
 * author           : MinjaeKim
 * date             : 2024-07-24
 * description      :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-07-24        MinjaeKim       최초 생성
 */
class QueryStringsTest {

    @Test
    void createTest() {
        QueryStrings qs = new QueryStrings("operand1=11&operator=+&operand2=55");
        assertThat(qs).isNotNull();
    }
}