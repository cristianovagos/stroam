package es.stroam.authserver.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import es.stroam.authserver.service.HttpService;

/**
 *      https://developer.github.com/apps/building-oauth-apps/authorizing-oauth-apps/
 * 
 *      emails:
 *          https://stackoverflow.com/questions/35373995/github-user-email-is-null-despite-useremail-scope
 */
public class GithubLogin {

    public final String URL_GET_AUTH = "https://github.com/login/oauth/authorize";    
    public final String URL_GET_TOKEN = "https://github.com/login/oauth/access_token";
    public final String URL_GET_USER = "https://api.github.com/user";
    public final String URL_GET_EMAIL = "https://api.github.com/user/emails";

    private String code;
    private User user;

    public GithubLogin() {
        this.code = "";
        this.user = new User();
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getBody() {
        return "{\"client_id\":\"1a6064af05f1ade02e7b\", \"client_secret\":\"abad734eb94714bebe81639e6486aef17a3e19b6\", \"code\":\""+ code +"\"}";
    }
	
	public void setUser(String json) {

        JSONObject jObj;
        
        try {
            jObj = new JSONObject(json);
            this.user.setName( jObj.getString("login") );

        } catch (JSONException e) {
            e.printStackTrace();
		}
	}

	public void setEmail(String json) {

        // [{"email":"marquescardoso@ua.pt","primary":true,"verified":true,"visibility":"public"}]
        JSONArray jArr;
        JSONObject jObj;

        try {
            jArr = new JSONArray(json);
            jObj = jArr.getJSONObject(0);

            this.user.setEmail( jObj.getString("email") );

        } catch (JSONException e) {
            e.printStackTrace();
		}
    }
    
    public void setToken(String token) {
        this.user.setToken(token);
    }

	public User getUser() {
		return this.user;
	}
}