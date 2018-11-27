package es.stroam.authserver.reposiroties;

import org.springframework.data.repository.CrudRepository;

import es.stroam.authserver.model.Client;

public interface ClientRepo extends CrudRepository<Client, String> {

}