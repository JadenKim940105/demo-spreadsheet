package com.spreadsheet.demospreadsheet.domain.account;

import lombok.*;

@Builder @NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public class AccountDto {

    private String email;

    private String password;

}
