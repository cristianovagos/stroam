/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.stroam.authserver.controllers;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import es.stroam.authserver.model.User;
import es.stroam.authserver.reposiroties.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author aCard0s0
 */
@Controller
@RequestMapping(path="/api/v1/gauth2fa")
public class GAuth2FA {
    
    @Autowired
    private UserRepo userRepository;
    
    @GetMapping(path="active/{user}")
    public ResponseEntity<String> active(@PathVariable("user") String name) {
        
        GoogleAuthenticator gAuth;
        GoogleAuthenticatorKey gAuthKey;
        
        for(User u : userRepository.findAll()) {
            if (u.getName().equals(name)) {
                gAuth = new GoogleAuthenticator();
                gAuthKey = gAuth.createCredentials();
                u.setGauthKey2fa(gAuthKey.getKey());
                u.setGauth2faActive(true);
                userRepository.save(u);
                return new ResponseEntity<>(u.getGauthKey2faJSON() ,HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    
    @GetMapping(path="confrim/{user}/{code}")
    public ResponseEntity<String> condirm(@PathVariable("user") String name, @PathVariable("code") String code) {
        
        GoogleAuthenticator gAuth;
        GoogleAuthenticatorKey gAuthKey;
        
        for(User u : userRepository.findAll()) {
            if (u.getName().equals(name)) { 
                gAuth = new GoogleAuthenticator();
                gAuthKey = gAuth.createCredentials();
                if (gAuth.authorize(u.getGauthKey2fa(), Integer.parseInt(code) )) {
                    return new ResponseEntity<>("{\"code\":\"success\"}", HttpStatus.OK);
                }
            }
        }
        return new ResponseEntity<>("{\"code\":\"fail\"}", HttpStatus.OK);
    }
}
