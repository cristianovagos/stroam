package es.stroam.authserver.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String name;
    private String email;
    @JsonIgnore
    private String password;
    private String token;
    private boolean accActive;
    private String gauthKey2fa;
    private boolean gauth2faActive;

    public User() {
    }

    public User(String name, String password, String email) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.token = "";
        this.accActive = false;
        this.gauth2faActive = false;
        this.gauthKey2fa = "";
    }

    @Override
    public String toString() {
        return "User[id = " + this.id + ", name = " + this.name + "]";
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    /* public void setPassword(String password) {
		this.password = password;
	} */
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    /* @JsonIgnore
	public String getBody() {
		return "{"+
			"\"id\" : \""+ this.id +"\","+
			"\"name\" : \""+ this.name +"\", "+
			"\"email\" : \""+ this.email +"\", "+
			"\"token\" : \""+ this.token +"\", "+
		"}";
	} */

    public void setAccActive(boolean accActive) {
        this.accActive = accActive;
    }

    public void setGauth2faActive(boolean gauth2faActive) {
        this.gauth2faActive = gauth2faActive;
    }

    public void setGauthKey2fa(String gauthKey2fa) {
        this.gauthKey2fa = gauthKey2fa;
    }

    public String getGauthKey2fa() {
        return gauthKey2fa;
    }
    
}
