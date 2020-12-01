package com.spreadsheet.demospreadsheet.domain.account;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class AccountDtoTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void init(){
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void close(){
        factory.close();
    }

    @Test
    @DisplayName("정상적인 이메일주소와 비밀번호 형식인 경우")
    void right_email_password_form(){
        AccountDto accountDto = AccountDto.builder()
                .email("jaden@email.com")
                .password("ABC123!!@")
                .build();
        Set<ConstraintViolation<AccountDto>> validate = validator.validate(accountDto);
        assertThat(validate).isEmpty();
    }


    @ParameterizedTest
    @MethodSource("paramsForWrongEmail")
    @DisplayName("잘못된 이메일 주소")
    void wrong_email_form(String email, String password, String errorMessage){
        AccountDto accountDto = AccountDto.builder()
                .email(email)
                .password(password)
                .build();
        Set<ConstraintViolation<AccountDto>> validate = validator.validate(accountDto);

        assertThat(validate.stream().findFirst().get().getMessage()).isEqualTo(errorMessage);
    }

    @ParameterizedTest
    @MethodSource("paramsForWrongPasswordForm")
    @DisplayName("잘못된 비밀번호 형식")
    void wrong_password_form(String email, String password, String errorMessage){
        AccountDto accountDto = AccountDto.builder()
                .email(email)
                .password(password)
                .build();
        Set<ConstraintViolation<AccountDto>> validate = validator.validate(accountDto);

        assertThat(validate.stream().findFirst().get().getMessage()).isEqualTo(errorMessage);

    }


    private static Stream<Arguments> paramsForWrongEmail(){
        return Stream.of(
                Arguments.of("notAnEmailForm", "ABC123!!@", "이메일 형식이어야 합니다."),
                Arguments.of("email!zz", "ABC123!!@", "이메일 형식이어야 합니다."),
                Arguments.of("!@#@$@#!", "ABC123!!12@", "이메일 형식이어야 합니다.")
        );
    }

    private static Stream<Arguments> paramsForWrongPasswordForm(){
        String errorMessage = "비밀번호는 영문 대,소문자와 숫자, 특수기호가 적어도 1개 이상씩 포함된 8자 ~ 20자의 비밀번호여야 합니다.";
        String email = "test@email.com";
        return Stream.of(
                Arguments.of(email, "AAAABBBBA", errorMessage),
                Arguments.of(email, "ThisisToOLooooooong!!!!!!!!!!!!!!!!!!!!!!!!!!!!@#@#@#@#@", errorMessage),
                Arguments.of(email, "onlylowercase", errorMessage)
        );
    }

}