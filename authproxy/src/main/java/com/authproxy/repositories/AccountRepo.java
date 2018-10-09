package com.authproxy.repositories;

import java.util.Collection;
import java.util.Optional;

import com.authproxy.models.Account;

import org.springframework.data.repository.Repository;

/**
 *  All the operation in CrudRepository are avaiable in Repository,
 *  just need to declare it.
 */

public interface AccountRepo extends Repository<Account, Long> { 

    Integer countByUsername(String username);
    Account save(Account account);
    Collection<Account> findAll();
    
	Optional<Account> findByUsername(String username);
}