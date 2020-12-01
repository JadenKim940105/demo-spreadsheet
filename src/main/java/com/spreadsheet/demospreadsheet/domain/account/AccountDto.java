package com.spreadsheet.demospreadsheet.domain.account;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Builder @NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public class AccountDto {

    @NotEmpty @Email(message = "이메일 형식이어야 합니다.")
    private String email;

    @NotEmpty
    @Pattern(regexp="(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,20}",
            message = "비밀번호는 영문 대,소문자와 숫자, 특수기호가 적어도 1개 이상씩 포함된 8자 ~ 20자의 비밀번호여야 합니다.")
    private String password;

}
