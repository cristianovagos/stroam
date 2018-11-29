/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.stroam.authserver.controllers;

import es.stroam.authserver.model.Client;
import es.stroam.authserver.model.Oauth;
import es.stroam.authserver.model.OauthLogin;
import es.stroam.authserver.model.PreOauthLogin;
import es.stroam.authserver.model.User;
import es.stroam.authserver.reposiroties.ClientRepo;
import es.stroam.authserver.reposiroties.OauthRepo;
import es.stroam.authserver.reposiroties.UserRepo;
import es.stroam.authserver.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author aCard0s0
 */
@Controller
@RequestMapping(path="/api/v1/oauth")
public class OauthController {
    
    @Autowired
    private ClientRepo clientRepository;
    
    @Autowired
    private UserRepo userRepository;
    
    @Autowired
    private OauthRepo oauthRepository;
    
    @PostMapping(path="/login")
    public ResponseEntity<String> create (@RequestBody PreOauthLogin login) {
        
        Oauth oauth;
        boolean hasClient;
        
        System.out.println(
                login.getClientid() +" "+
                login.getUsername()+" "+
                login.getPassword()
        );
        
        // client exist ?
        hasClient = false;
        for(Client c : clientRepository.findAll()) {
            if (c.getClientId().equals(login.getClientid())) {
                hasClient = true;
            }
        }

        if (!hasClient) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); 
        }

        // If User exist, generate code
        for(User u : userRepository.findAll()) {
            if (u.getName().equals(login.getUsername()) && u.getPassword().equals(login.getPassword())) {
                oauth = new Oauth(login.getUsername());
                return new ResponseEntity<>( oauth.getCodeJSON(), HttpStatus.OK);       // return the code
            }
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
    
    @PostMapping(path="/login/access_token")
    public ResponseEntity<String> create (@RequestBody OauthLogin login) {
        
        TokenService ts;
        String response;
        int id = 0;
        String name = null;
        String token = null;
        
        // Client exist ?
        for (Client c : clientRepository.findAll()) {
            if (c.getClientId().equals(login.getClientId()) && c.getClientSecret().equals(login.getClientSecret())) {
                
                for (Oauth auth : oauthRepository.findAll()) {
                    if (auth.getCode().equals(login.getCode())) {
                        ts = new TokenService();
                        token = ts.create();
                        name = auth.getUser();
                        
                        for(User u :userRepository.findAll()) {
                            if (u.getName().equals(name)) {
                                id = u.getId();
                                u.setToken(token);
                                userRepository.save(u);
                            }
                        }
                    }
                }
                response = "{\"id\":\""+id+"\", \"name\":\""+name+"\", \"token\":\""+token+"\"}";
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
    
    @GetMapping(path="/login/authorize")
    public ResponseEntity<String> verify (@RequestHeader HttpHeaders headers) {
        
        TokenService ts = new TokenService();
        
        //get header
        String token = headers.getFirst("Authorization");
        
        if(ts.verify(token)) {
            return new ResponseEntity<>(ts.create(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(ts.create(), HttpStatus.OK);
    }
}
