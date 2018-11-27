package es.stroam.authserver.model;

public class OauthLogin {

    private String code;
    private String client_id;
    private String client_secret;

    public OauthLogin() {}

    /**
     * @return the client_secrect
     */
    public String getClientSecret() {
        return client_secret;
    }

    /**
     * @param client_secrect the client_secrect to set
     */
    public void setClientSecret(String client_secret) {
        this.client_secret = client_secret;
    }

    /**
     * @return the client_id
     */
    public String getClientId() {
        return client_id;
    }

    /**
     * @param client_id the client_id to set
     */
    public void setClientId(String client_id) {
        this.client_id = client_id;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(String code) {
        this.code = code;
    }

}