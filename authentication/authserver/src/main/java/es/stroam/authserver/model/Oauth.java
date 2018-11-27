package es.stroam.authserver.model;

import java.util.Random;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Oauth {

    @Id
    private String code;
    private String user;
    private String client;

    public Oauth(String client, String user) {
        generateCode();
        this.user = user;
        this.client = client;
    }

    public void generateCode() {

        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random r = new Random();
        this.code = "";
        
        // prints 10 random characters from alphabet
        for (int i = 0; i < 10; i++) {
            this.code += alphabet.charAt(r.nextInt(alphabet.length()));
        }
    }

    public String getCodeJSON() {
        return "{\"code\": \""+ this.code +"\"}";
    }
    
    public String getCode() {
        return this.code;
    }

    public String getUser() {
        return this.user;
    }

    public String getClient() {
        return this.client;
    }
}