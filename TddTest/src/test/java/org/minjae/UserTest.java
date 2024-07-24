package org.minjae;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    @DisplayName("패스워드 초기화 여부를 판단 ")
    void initPassword() {
        //given
        User u = new User();
        //when
        u.initPassword();
        //then
        assertThat(u.getPassword())
                .isNotNull();
    }
}