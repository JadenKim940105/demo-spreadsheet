package com.spreadsheet.demospreadsheet.domain.account;

import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Controller
@RequestMapping(value = "/api/accounts", produces = MediaTypes.HAL_JSON_VALUE)
public class AccountController {

    private final AccountRepository accountRepository;

    AccountController(AccountRepository accountRepository){
        this.accountRepository = accountRepository;
    }

  @PostMapping
    public ResponseEntity createAccount(@RequestBody Account account){
      Account newAccount = this.accountRepository.save(account);
      URI createdUri = linkTo(AccountController.class).slash(newAccount.getId()).toUri();
      return ResponseEntity.created(createdUri).body(account);
  }

}
