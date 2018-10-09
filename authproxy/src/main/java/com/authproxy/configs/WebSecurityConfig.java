package com.authproxy.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

/** 
 *  Functions description:
 * 
 *  protected void configure(HttpSecurity http): 
 *      It allows configuring web-based security for specific http requests.
 * protected void configure(AuthenticationManagerBuilder auth): 
 *      The place to configure the default authenticationManager @Bean.
 * public void configure(WebSecurity web):  
 *      Override this method to expose the default AuthenticationManager as a Bean.
 * protected UserDetailsService userDetailsService(): 
 *      Allows modifying and accessing the UserDetailsService, a core interface which loads user-specific data.
 */

@Configuration
@EnableWebSecurity
/* @Order(SecurityProperties.BASIC_AUTH_ORDER) */
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    public UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder( passwordEncoder );
        provider.setUserDetailsService( userDetailsService() );
        return provider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .formLogin().disable()                              // disable form authentication
            .anonymous().disable()                              // disable anonymous user
            //.csrf().disable();
            .authorizeRequests().anyRequest().authenticated();        // denying all access
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        // provides the default AuthenticationManager as a Bean
        return super.authenticationManagerBean();
    }

}
