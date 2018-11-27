package es.stroam.authserver.controllers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import es.stroam.authserver.model.Client;
import es.stroam.authserver.model.User;
import es.stroam.authserver.reposiroties.ClientRepo;
import es.stroam.authserver.reposiroties.UserRepo;

/** 
 * 		Debug class
 */
@Controller
@RequestMapping(path="/api")
public class UserController {

	@Autowired
	private UserRepo userRepository;
        
        @Autowired
	private ClientRepo clientRepository;
	
	@PostMapping(path="/user")
	public ResponseEntity<User> addNewUser (@RequestBody User user) {
		
		User newUser = userRepository.save(user);
		return new ResponseEntity<>(newUser, HttpStatus.CREATED);
	}
	
	@GetMapping(path="/user/all")
	public @ResponseBody Iterable<User> getAllUsers() {
            
		return userRepository.findAll();
	}

	@GetMapping(path="/email/all")
	public ResponseEntity<String> getAllEmails() {

		JSONArray jArr = new JSONArray();
		JSONObject jObj = new JSONObject();

		for(User u : userRepository.findAll()) {
			jArr.put( u.getEmail() );
		}
		try {
			jObj.put("emails", jArr);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return new ResponseEntity<>( jObj.toString() , HttpStatus.OK );
	}

	@GetMapping(path="/email/{id}")
	public ResponseEntity<String> getAllEmails(@PathVariable("id") Integer id) {

		JSONArray jArr = new JSONArray();
		JSONObject jObj = new JSONObject();

		for(User u : userRepository.findAll()) {
			if (u.getId().equals(id)) {
				try {
					jObj.put("email", u.getEmail());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			jArr.put( u.getEmail() );
		}
		return new ResponseEntity<>( jObj.toString() , HttpStatus.OK );
	}

        @GetMapping(path="/client/all")
	public @ResponseBody Iterable<Client> getAllClients() {
            
		return clientRepository.findAll();
	}
}
