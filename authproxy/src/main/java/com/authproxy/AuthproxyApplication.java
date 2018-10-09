package com.authproxy;

import java.util.Arrays;

import javax.sql.DataSource;

import com.authproxy.models.Account;
import com.authproxy.models.Roles;
import com.authproxy.service.AccountService;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class AuthproxyApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthproxyApplication.class, args);
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
                    acct.grantAuthority(Roles.ROLE_USER);
                    if (username.equals("admin"))
                        acct.grantAuthority(Roles.ROLE_ADMIN);
                    accountService.registerUser(acct);
                }
		);
	}

}
