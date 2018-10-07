package com.authproxy.controllers;

import com.authproxy.models.Account;
import com.authproxy.service.AccountService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
public class HomeController {
    
    @Autowired
    private AccountService accountService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    protected ResponseEntity<String> home() {

        return new ResponseEntity<String>("Hello", HttpStatus.OK);
    }

    @RequestMapping(path = "/account/register", 
        method = RequestMethod.POST, 
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> register(@RequestBody Account account) {
        
        account.setRole("ROLE_USER");
        return new ResponseEntity<Object>(accountService.register( account ), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/account/all", 
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity< Collection<Account> > getAll() {

        return new ResponseEntity<>( accountService.findAll(), HttpStatus.OK);
    }

    @RequestMapping(path = "/account/size", 
        method = RequestMethod.GET, 
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> count() {

        return new ResponseEntity<Object>(accountService.count(), HttpStatus.OK);
    }
}