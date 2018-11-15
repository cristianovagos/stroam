package es.stroam.authserver.reposiroties;

import org.springframework.data.repository.CrudRepository;

import es.stroam.authserver.model.User;


public interface UserRepo extends CrudRepository<User, Integer> {

}
