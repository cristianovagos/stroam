package com.authproxy.service;

import java.util.Collection;

import javax.security.auth.login.AccountException;

import com.authproxy.models.Account;
import com.authproxy.repositories.AccountRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    @Autowired
    private AccountRepo accountRepo;

    /* @Autowired
    private PasswordEncoder passwordEncoder; */

    public Account register(Account account){
        /* account.setPassword(passwordEncoder.encode(account.getPassword())); */
        return accountRepo.save( account );
    }

    public Collection<Account> findAll() { 
        return accountRepo.findAll();
    }

    public Integer count() { 
        return accountRepo.findAll().size();
    }
}