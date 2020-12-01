package com.spreadsheet.demospreadsheet.commons;

import com.spreadsheet.demospreadsheet.domain.account.AccountDto;
import com.spreadsheet.demospreadsheet.domain.account.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
@RequiredArgsConstructor
public class EmailUniqueValidator {

    private final AccountRepository accountRepository;

    public void validate(AccountDto accountDto, Errors errors){
        // 입력받은 이메일이 이미 존재할 경우
        if(accountRepository.existsByEmail(accountDto.getEmail())){
            errors.rejectValue("email", "email duplication", "이미 존재하는 이메일 입니다.");
        }
    }
}
