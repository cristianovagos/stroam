package es.stroam.authserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import es.stroam.authserver.model.User;
import es.stroam.authserver.reposiroties.UserRepo;

@SpringBootApplication
public class AuthServer {

    private static final Logger log = LoggerFactory.getLogger(SpringApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(AuthServer.class, args);
    }

    @Bean
	public CommandLineRunner demo(UserRepo repository) {
		return (args) -> {
			// save a couple of customers
			repository.save(new User("Admin01", "pass", "admin01@ua.pt", "Administrador"));
			repository.save(new User("Admin02", "pass", "admin02@ua.pt", "Administrador"));
			repository.save(new User("User01", "pass", "user01@ua.pt", "User"));
			repository.save(new User("User01", "pass", "user02@ua.pt", "User"));

			// fetch all customers
			log.info("User added:");
			log.info("-------------------------------");
			for (User user : repository.findAll()) {
				log.info(user.toString());
			}
			log.info("");
		};
    }
    
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**").allowedOrigins("http://localhost:4200");
            }
        };
    }
}
