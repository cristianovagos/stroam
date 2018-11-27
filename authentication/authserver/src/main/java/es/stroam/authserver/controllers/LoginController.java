package es.stroam.authserver.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import es.stroam.authserver.model.Login;
import es.stroam.authserver.model.Token;
import es.stroam.authserver.model.User;
import es.stroam.authserver.reposiroties.UserRepo;
import es.stroam.authserver.service.HttpService;
import es.stroam.authserver.social.github.GithubLogin;


@Controller
@RequestMapping(path="/api/v1")
public class LoginController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

	@Autowired
    private UserRepo userRepository;
        
	@PostMapping(path="/login")
	public ResponseEntity<User> login (@RequestBody Login login) {
        
        log.info("Searching for: "+ login.getUsername() +" "+ login.getPassword() );

        // TODO: user service & improve code
        for(User u : userRepository.findAll()) {
            if (u.getName().equals(login.getUsername()) && u.getPassword().equals(login.getPassword())) {
                return new ResponseEntity<>( u, HttpStatus.OK);
            }
        }
        // new UserSession("User credentials are not valid")
		return new ResponseEntity<>( new User(), HttpStatus.OK);
	}
    
    
    @GetMapping(path="/github")
    public ResponseEntity<User> github (@RequestParam String code) {

        // github LOGIN manager
        GithubLogin gitLogin = new GithubLogin();
        gitLogin.setCode(code);

        // http
        HttpService service = new HttpService();
        String token_response = service.sendPost(gitLogin.URL_GET_TOKEN, gitLogin.getBody());

        // create user
        Token token = new Token();
        token.fromGithub(token_response);
        //System.out.println(token);
        String user_response = service.sendAuthGet(gitLogin.URL_GET_USER, token);
        String email_response = service.sendAuthGet(gitLogin.URL_GET_EMAIL, token);
        gitLogin.setUser( user_response );
        gitLogin.setEmail( email_response );
        gitLogin.setToken( token.toString() );

        User user = gitLogin.getUser();
        user = userRepository.save(user);

        //service.sendPost("", user.getBody() );
        //System.out.println(user.getBody());
        return new ResponseEntity<>(user , HttpStatus.OK);

        // create session
        //UserSession uSession = new UserSession(user, token);
	}
    
   
}
