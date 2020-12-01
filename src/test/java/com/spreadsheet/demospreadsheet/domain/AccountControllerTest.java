package com.spreadsheet.demospreadsheet.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spreadsheet.demospreadsheet.common.RestDocsConfig;
import com.spreadsheet.demospreadsheet.domain.account.Account;
import com.spreadsheet.demospreadsheet.domain.account.AccountDto;
import com.spreadsheet.demospreadsheet.domain.account.AccountRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Stream;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Transactional
@Import(RestDocsConfig.class)
class AccountControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @ParameterizedTest
    @MethodSource("paramsForRightForm")
    @DisplayName("정상적인 입력값일 때 아이디 생성, 201 status 반환")
    void createAccount(String email, String password) throws Exception {
        AccountDto account = AccountDto.builder()
                .email(email)
                .password(password)
                .build();

        mockMvc.perform(post("/api/accounts")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaTypes.HAL_JSON)
            .content(objectMapper.writeValueAsString(account)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE + ";charset=UTF-8"))
                .andDo(document("create-account",
                        links(halLinks(),
                                linkWithRel("self").description("Link to self"),
                                linkWithRel("update-account").description("Link to update the account"),
                                linkWithRel("profile").description("Link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description(MediaTypes.HAL_JSON_VALUE),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description(MediaType.APPLICATION_JSON)
                        ),
                        requestFields(
                                fieldWithPath("email").description("생성할 계정의 이메일"),
                                fieldWithPath("password").description("생성할 계정의 비밀번호")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("새로운 계정을 조회할 URL"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description(MediaTypes.HAL_JSON_VALUE)
                        ),
                        responseFields(
                                fieldWithPath("id").description("생성된 계정의 PK"),
                                fieldWithPath("email").description("생성된 계정의 이메일"),
                                fieldWithPath("password").description("생성된 계정의 비밀번호"),
                                fieldWithPath("roles").description("생성된 계정의 권한정보"),
                                fieldWithPath("_links.self.href").description("Link to self").optional(),
                                fieldWithPath("_links.update-account.href").description("Link to update the Account").optional(),
                                fieldWithPath("_links.profile.href").description("Link to profile")
                        )
                ));

    }

    @Test
    @DisplayName("잘못된 입력값이 포함된 경우 BadRequest")
    void createAccount_BadRequest() throws Exception {
        Account account = Account.builder()
                .id(987654321L)
                .email("jaden@email.com")
                .password("pass")
                .roles(Set.of(AccountRole.ADMIN))
                .build();

        mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(account)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("입력값이 비어있는 경우 BadRequest")
    void createAccount_BadRequest_Empty_Input() throws Exception {
        AccountDto accountDto = AccountDto.builder().build();
        mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(this.objectMapper.writeValueAsString(accountDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("입력값의 형식이 올바르지 않은 경우 BadRequest")
    void createAccount_Wrong_Value() throws Exception {
        // Given 잘못된 이메일 형식
        AccountDto wrongDto = AccountDto.builder()
                .email("notAnEmailForm")
                .password("Abcd1234!@")
                .build();

        // Then
        mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(wrongDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].objectName").value("accountDto"))
                .andExpect(jsonPath("$[0].field").value("email"))
                .andExpect(jsonPath("$[0].rejectedValue").value("notAnEmailForm"))
                .andExpect(jsonPath("$[0].defaultMessage").value("이메일 형식이어야 합니다."));

        // Given 잘못된 비밀번호 형식
        wrongDto.setEmail("email@email.com");
        wrongDto.setPassword("123");

        // Then
        mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(wrongDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].objectName").value("accountDto"))
                .andExpect(jsonPath("$[0].field").value("password"))
                .andExpect(jsonPath("$[0].rejectedValue").value("123"))
                .andExpect(jsonPath("$[0].defaultMessage").value("비밀번호는 영문 대,소문자와 숫자, 특수기호가 적어도 1개 이상씩 포함된 8자 ~ 20자의 비밀번호여야 합니다."));
    }

    @Test
    @DisplayName("중복된 이메일")
    void duplicated_email() throws Exception {
        AccountDto account = AccountDto.builder()
                .email("jaden@email.com")
                .password("PassWord123123!")
                .build();
        mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(this.objectMapper.writeValueAsString(account)))
                .andDo(print())
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(this.objectMapper.writeValueAsString(account)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


    private static Stream<Arguments> paramsForRightForm() {
        return Stream.of(
                Arguments.of("test@email.com", "AaBbCc123!@"),
                Arguments.of("test123@email.com", "Aa123!!!"),
                Arguments.of("test@google.com", "Aa12345678901234567!")
        );
    }

}