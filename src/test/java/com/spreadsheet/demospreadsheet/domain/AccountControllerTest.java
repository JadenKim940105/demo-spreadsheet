package com.spreadsheet.demospreadsheet.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spreadsheet.demospreadsheet.domain.account.Account;
import com.spreadsheet.demospreadsheet.domain.account.AccountDto;
import com.spreadsheet.demospreadsheet.domain.account.AccountRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("정상적인 입력값일 때 아이디 생성")
    void createAccount() throws Exception {
        AccountDto account = AccountDto.builder()
                .email("jaden@email.com")
                .password("pass")
                .build();

        mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(account)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("id").exists())
                    .andExpect(jsonPath("roles").exists())
                    .andExpect(header().exists(HttpHeaders.LOCATION))
                    .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE));
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

}