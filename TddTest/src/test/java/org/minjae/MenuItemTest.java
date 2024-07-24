package org.minjae;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * packageName       : org.minjae
 * fileName         : MenuItemTest
 * author           : MinjaeKim
 * date             : 2024-07-05
 * description      :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-07-05        MinjaeKim       최초 생성
 */
public class MenuItemTest {

    @DisplayName("메뉴항목을 생성한다")
    @Test
    void createMenu() {
        assertThatCode(()-> new MenuItem("만두",5000))
                .doesNotThrowAnyException();
    }
}
