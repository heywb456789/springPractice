package org.minjae;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * packageName       : org.minjae
 * fileName         : CookTest
 * author           : MinjaeKim
 * date             : 2024-07-05
 * description      :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-07-05        MinjaeKim       최초 생성
 */
public class CookTest {

    @DisplayName("요리를 생성한다.")
    @Test
    void createCook() {
        assertThatCode(()-> new Cook("만두", 5000))
                .doesNotThrowAnyException(); //요리를 생성하면 어떠한 익셉션도 밸생 x

    }
}
