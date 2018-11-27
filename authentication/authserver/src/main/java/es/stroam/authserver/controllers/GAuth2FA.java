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
    public ResponseEntity<String> active(@RequestParam("user") String name) {
        
        GoogleAuthenticator gAuth;
        GoogleAuthenticatorKey gAuthKey;
        
        for(User u : userRepository.findAll()) {
            if (u.getName().equals(name)) {
                gAuth = new GoogleAuthenticator();
                gAuthKey = gAuth.createCredentials();
                u.setGauthKey2fa(gAuthKey.getKey());
                u.setGauth2faActive(true);
                userRepository.save(u);
                return new ResponseEntity<>(u.getGauthKey2fa() ,HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    
    @GetMapping(path="confrim/{user}/{code}")
    public ResponseEntity<String> condirm(@RequestParam("user") String name,
            @RequestParam("code") Integer code) {
        
        GoogleAuthenticator gAuth;
        GoogleAuthenticatorKey gAuthKey;
        
        for(User u : userRepository.findAll()) {
            if (u.getName().equals(name)) { 
                gAuth = new GoogleAuthenticator();
                gAuthKey = gAuth.createCredentials();
                if (gAuth.authorize(u.getGauthKey2fa(), code)) {
                    return new ResponseEntity<>("success", HttpStatus.OK);
                }
            }
        }
        return new ResponseEntity<>("fail", HttpStatus.BAD_REQUEST);
    }
}
