package es.stroam.authserver.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Client {

    @Id
    private String clientId;
    private String clientSecret;

    public Client() {}

    public Client(String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    @Override
	public String toString() {
		return "Client[id = "+ this.clientId+", secret = "+ this.clientSecret +"]";
	}
}