package es.stroam.authserver.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import es.stroam.authserver.model.User;
import es.stroam.authserver.reposiroties.UserRepo;


@Controller
@RequestMapping(path="/api")
public class UserController {

	@Autowired
	private UserRepo userRepository;
	
	@PostMapping(path="/user")
	public ResponseEntity<User> addNewUser (@RequestBody User user) {
		
		User newUser = userRepository.save(user);
		return new ResponseEntity<>(newUser, HttpStatus.CREATED);
	}
	
	@GetMapping(path="/all")
	public @ResponseBody Iterable<User> getAllUsers() {
		return userRepository.findAll();
	}
}
