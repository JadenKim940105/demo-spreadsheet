package com.spreadsheet.demospreadsheet.domain.account;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class AccountResource extends EntityModel<Account> {

    public static WebMvcLinkBuilder selfLinkBuilder = linkTo(AccountController.class);

    public static EntityModel<Account> of(Account account){
        List<Link> links = getSelfLink(account);
        return EntityModel.of(account, links);
    }

    private static List<Link> getSelfLink(Account account){
        var selfLink = selfLinkBuilder.slash(account.getId());
        List<Link> links = new ArrayList<>();
        links.add(selfLink.withSelfRel());
        return links;
    }

    public static URI getCreatedUri(Account account){
        return selfLinkBuilder.slash(account.getId()).toUri();
    }




}
