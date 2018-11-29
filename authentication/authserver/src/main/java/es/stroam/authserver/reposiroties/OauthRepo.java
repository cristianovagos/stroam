package es.stroam.authserver.reposiroties;

import es.stroam.authserver.model.Oauth;
import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author aCard0s0
 */
public interface OauthRepo extends CrudRepository<Oauth, String>{
    
}
