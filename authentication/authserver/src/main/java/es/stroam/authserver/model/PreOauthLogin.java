package es.stroam.authserver.model;

import javax.persistence.Entity;

public class PreOauthLogin {

    private String username;
    private String password;
    private String clientid;

    public PreOauthLogin() {}

    /**
     * @return the client_id
     */
    public String getClientid() {
        return clientid;
    }

    /**
     * @param client_id the client_id to set
     */
    public void setClientid(String clientid) {
        this.clientid = clientid;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

}