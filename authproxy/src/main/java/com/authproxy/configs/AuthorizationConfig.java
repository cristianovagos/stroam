package com.authproxy.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Functions description:
 * 
 * void configure(AuthorizationServerSecurityConfigurer security): Configure the
 * security of the Authorization Server, which means in practical terms the
 * /oauth/token endpoint. void configure(ClientDetailsServiceConfigurer
 * clients): Configure the ClientDetailsService, declaring individual clients
 * and their properties. void configure(AuthorizationServerEndpointsConfigurer
 * endpoints): Configure the non-security features of the Authorization Server
 * endpoints, like token store, token customizations, user approvals and grant
 * types.
 * 
 * Note: set application.properties file security.oauth2.resource.id=oauth2_id
 */

@Configuration
@EnableAuthorizationServer
public class AuthorizationConfig extends AuthorizationServerConfigurerAdapter {

    private int accessTokenValiditySeconds = 10000;
    private int refreshTokenValiditySeconds = 30000;

    @Value("${security.oauth2.resource.id}")
    private String resourceId;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(this.authenticationManager)
                .tokenServices( tokenServices() )
                .tokenStore( tokenStore() )
                .accessTokenConverter( accessTokenConverter() );
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        // we're allowing access to the token only for clients with
        // 'ROLE_TRUSTED_CLIENT' authority
        oauthServer.tokenKeyAccess("hasAuthority('ROLE_TRUSTED_CLIENT')")
                .checkTokenAccess("hasAuthority('ROLE_TRUSTED_CLIENT')");
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        // doesn't work without configure frist AuthorizationServerEndpointsConfigurer
        clients.inMemory().withClient("trusted-app")
                .authorizedGrantTypes("client_credentials", "password", "refresh_token")
                .authorities("ROLE_TRUSTED_CLIENT")
                .scopes("read", "write")
                .resourceIds(resourceId)
                .accessTokenValiditySeconds(accessTokenValiditySeconds)
                .refreshTokenValiditySeconds(refreshTokenValiditySeconds)
                .secret(passwordEncoder.encode("secret"));
    }

    /**
     * JSON Web Tokens, which contains some advantages over the standard one. The
     * JWT contains all information necessary to validate the user on the token
     * itself, meaning that it doesn’t depend on a back-end storage to conclude the
     * authentication process. Nonetheless, you must pay special attention to the
     * revocation process of the JWT.
     */
    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        // symmetric key in the JwtAccessTokenConverter this isn’t the best approach for a production environment. 
        // We should create an asymmetric key and use it to sign the converter.
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        // https://github.com/spring-cloud/spring-cloud-config/issues/98
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(
                new ClassPathResource("mykeys.jks"), "mypass".toCharArray());
                
        converter.setKeyPair(keyStoreKeyFactory.getKeyPair("mykeys"));
        return converter;
    }

    @Bean
    @Primary
    public DefaultTokenServices tokenServices() {

        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        defaultTokenServices.setSupportRefreshToken(true);
        defaultTokenServices.setTokenEnhancer(accessTokenConverter());
        return defaultTokenServices;
    }

    
}