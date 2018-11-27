package es.stroam.authserver.model;

public class Token {

    private String token;
    private String scope;
    private String type;

    public Token() {
        this.token = "";
        this.scope = "";
        this.type = "";
    }

    public void fromGithub(String rsp) {
        
        String[] tmp = rsp.split("&");
        this.token = tmp[0].split("=")[1];
        this.scope = ""; //tmp[1].split("=")[1];
        this.type = tmp[2].split("=")[1];
        
        // TODO: json handling improvement
        //Map<String, Object> map = new JsonParser().parseMap(json);
    }

    @Override
    public String toString() {
        return type +" "+ token;
    }

    public String getToken() {
        return token;
    }

    public String getScope() {
        return scope;
    }

    public String getType() {
        return type;
    }
}