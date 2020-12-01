package com.spreadsheet.demospreadsheet.domain.account;

import com.spreadsheet.demospreadsheet.commons.EmailUniqueValidator;
import org.hibernate.EntityMode;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Controller
@RequestMapping(value = "/api/accounts", produces = MediaTypes.HAL_JSON_VALUE)
public class AccountController {

    private final AccountRepository accountRepository;

    private final ModelMapper modelMapper;

    private final EmailUniqueValidator emailUniqueValidator;

    private final AccountService accountService;

    AccountController(AccountRepository accountRepository, ModelMapper modelMapper, EmailUniqueValidator emailUniqueValidator, AccountService accountService){
        this.accountRepository = accountRepository;
        this.modelMapper = modelMapper;
        this.emailUniqueValidator = emailUniqueValidator;
        this.accountService = accountService;

    }

  @PostMapping
    public ResponseEntity createAccount(@RequestBody @Valid AccountDto accountDto, Errors errors){
        if(errors.hasErrors()){
            return ResponseEntity.badRequest().body(errors);
        }
        emailUniqueValidator.validate(accountDto, errors);
      if(errors.hasErrors()){
          return ResponseEntity.badRequest().body(errors);
      }

      Account account = modelMapper.map(accountDto, Account.class);
      Account newAccount = this.accountService.createAccount(account);
      URI createdUri = AccountResource.getCreatedUri(newAccount);
      EntityModel<Account> accountResource = AccountResource.of(newAccount)
              .add(AccountResource.selfLinkBuilder.slash(newAccount.getId()).withRel("update-account"))
              .add(Link.of("/docs/index.html#resources-accounts-create").withRel("profile"));
      return ResponseEntity.created(createdUri).body(accountResource); //newAccount 는 objectMapper 를 사용해 json 형태로 변환된다.
  }

}
