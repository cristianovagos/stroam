package es.stroam.authserver.service;

import es.stroam.authserver.model.Token;
import es.stroam.authserver.model.User;

public class UserSession {

    private User user;
    private Token token;
    private String error;

    public UserSession(User user) {
        this.user = user;
        this.token = null;
        this.error = null;
    }

    public UserSession(User user, Token token) {
        this.user = user;
        this.token = null;
        this.error = null;
    }

    public UserSession(String error) {
        this.user = null;
        this.token = null;
        this.error = error;
    }
    
}