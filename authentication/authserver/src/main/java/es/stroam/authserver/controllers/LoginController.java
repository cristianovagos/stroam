package es.stroam.authserver.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import es.stroam.authserver.model.Login;
import es.stroam.authserver.model.User;
import es.stroam.authserver.reposiroties.UserRepo;


@Controller
@RequestMapping(path="/api")
public class LoginController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

	@Autowired
	private UserRepo userRepository;
	
	@PostMapping(path="/v1/login")
	public ResponseEntity<User> login (@RequestBody Login login) {
        
        log.info("Searching for: "+ login.getUsername() +" "+ login.getPassword() );

        for(User u : userRepository.findAll()) {
            if (u.getName().equals(login.getUsername()) && u.getPassword().equals(login.getPassword())) {
                return new ResponseEntity<>(u, HttpStatus.OK);
            }
        }
		return new ResponseEntity<>( new User(), HttpStatus.OK);
	}
	
}
