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

import es.stroam.authserver.model.Client;
import es.stroam.authserver.model.User;
import es.stroam.authserver.reposiroties.ClientRepo;
import es.stroam.authserver.reposiroties.UserRepo;

@SpringBootApplication
public class AuthServer {

    private static final Logger log = LoggerFactory.getLogger(SpringApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(AuthServer.class, args);
    }

    @Bean
	public CommandLineRunner adduser(UserRepo userepo) {
		return (args) -> {
			// save a couple of customers
			userepo.save(new User("Admin", "pass", "admin@ua.pt"));
			userepo.save(new User("User", "pass", "user@ua.pt"));

			// fetch all customers
			log.info("User added:");
			log.info("-------------------------------");
			for (User user : userepo.findAll()) {
				log.info(user.toString());
			}
            log.info("");
		};
    }

    @Bean
    public CommandLineRunner addclient(ClientRepo clientrepo) {
		return (args) -> {
            clientrepo.save(new Client("es-stroam-test", "super_screct001"));
            clientrepo.save(new Client("es-stroam-frontend", "super_screct001"));
            
            // fetch all customers
			log.info("Client added:");
			log.info("-------------------------------");
			for (Client client : clientrepo.findAll()) {
				log.info(client.toString());
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
