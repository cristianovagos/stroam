package com.authproxy.controllers;

import com.authproxy.models.Account;
import com.authproxy.service.AccountService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Collection;

@RestController
public class HomeController {
    
    @Autowired
    private AccountService accountService;

    @RequestMapping(value = "/api/", method = RequestMethod.GET)
    protected ResponseEntity<String> home() {

        return new ResponseEntity<String>("Hello", HttpStatus.OK);
    }

    @GetMapping(value = "/api/hello", produces = "application/json")
    public RestMsg helloUser(){
        // The authenticated user can be fetched using the SecurityContextHolder
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return new RestMsg(String.format("Hello '%s'!", username));
    }

    @GetMapping("/api/admin")
    // If a controller request asks for the Principal user in
    // the method declaration Spring security will provide it.
    public RestMsg helloAdmin(Principal principal){
        return new RestMsg(String.format("Welcome '%s'!", principal.getName()));
    }



    // TODO: formalize this.
    public static class RestMsg {
        private String msg;
        public RestMsg(String msg) {
            this.msg = msg;
        }
        public String getMsg() {
            return msg;
        }
        public void setMsg(String msg) {
            this.msg = msg;
        }
    }

    @PostMapping("/api/account/register")
    public ResponseEntity<Account> registerAccount(@RequestBody Account account) {
        account = accountService.registerUser(account);
        return new ResponseEntity<>(account, HttpStatus.CREATED);
    }

    @RequestMapping(value = "api/account/all", 
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity< Collection<Account> > getAll() {

        return new ResponseEntity<>( accountService.findAll(), HttpStatus.OK);
    }
}