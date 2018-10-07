package com.authproxy.repositories;

import java.util.Collection;

import com.authproxy.models.Account;

import org.springframework.data.repository.Repository;

public interface AccountRepo extends Repository<Account, Long> {

    Integer countByUsername(String username);
    Account save(Account account);
    Collection<Account> findAll();
}