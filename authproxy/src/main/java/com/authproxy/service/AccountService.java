package com.authproxy.service;

import java.util.Collection;
import java.util.Optional;

import com.authproxy.models.Account;
import com.authproxy.models.Roles;
import com.authproxy.repositories.AccountRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AccountService implements UserDetailsService {

    @Autowired
    private AccountRepo accountRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Account registerUser(Account account) {

        account.setPassword(passwordEncoder.encode(account.getPassword()));
        account.grantAuthority(Roles.ROLE_USER);
        return accountRepo.save( account );
}

    public Collection<Account> findAll() { 
        return accountRepo.findAll();
    }

    public Integer count() { 
        return accountRepo.findAll().size();
    }

    @Bean
	public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<Account> account = accountRepo.findByUsername( username );
        
        if ( account.isPresent() ) {
            return account.get();
        } else {
            throw new UsernameNotFoundException(
                String.format("Username[%s] not found", username));
        }
	}
}