package org.minjae;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatCode;


/**
 * 
 */
public class PasswordValidatorTest {

    public static final String WRONG_PASSWORD_LENGTH_EXCEPTION_METHOD = "serverwizard";

    @DisplayName("비밀번호가 최소 8자 이상 , 12자 이하면 예외가 발생 x")
    @Test
    void validatePasswordTest() {
        assertThatCode(() -> PasswordValidator.validate(WRONG_PASSWORD_LENGTH_EXCEPTION_METHOD))
                .doesNotThrowAnyException();
    }

    @DisplayName("비밀번호가 8자 미만 또는 12자 초과하는 경우 IllegalArgumetnException")
    @ParameterizedTest
    @ValueSource(strings = {"aabbccd", "aaddccaaddccd"})
    void validatePasswordTest2(String password){
        assertThatCode(() -> PasswordValidator.validate(password))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Password length must be between 8 and 12");
    }
}
