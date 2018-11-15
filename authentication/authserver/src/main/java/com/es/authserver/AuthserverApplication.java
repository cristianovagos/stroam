package com.es.authserver;

import java.util.Arrays;

import javax.sql.DataSource;

import com.es.authserver.security.AuthoritiesConstants;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

@SpringBootApplication
public class AuthserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthserverApplication.class, args);
	}

	@Bean @Qualifier("mainDataSource")
	public DataSource dataSource(){

		EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
		EmbeddedDatabase db = builder
				.setType(EmbeddedDatabaseType.H2)
				.build();
		return db;
	}

	@Bean
	CommandLineRunner init( AccountService accountService) {
        return (evt) -> 
            Arrays.asList("admin,user".split(","))
                .forEach(username -> {
                    Account acct = new Account();
                    acct.setUsername(username);
                    acct.setPassword("password");
                    acct.grantAuthority(AuthoritiesConstants.USER);
                    if (username.equals("admin"))
                        acct.grantAuthority(AuthoritiesConstants.ADMIN);
                    accountService.registerUser(acct);
                }
		);
    }
}
