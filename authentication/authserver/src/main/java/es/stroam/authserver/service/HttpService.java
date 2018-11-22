package es.stroam.authserver.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
/* import org.apache.http.HttpEntity;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients; */

import es.stroam.authserver.model.Token;

public class HttpService {

	private URL url ;
	private	HttpsURLConnection con;
	private	BufferedReader in;
	private	StringBuffer response;

    public HttpService() {

		this.url = null;
		this.con = null;
		this.in = null;
		this.response = null;
    }

    public String sendPost(String address, String body) {

		try {
			this.url = new URL(address);
			this.con = (HttpsURLConnection) url.openConnection();

			//add request header
			con.setRequestMethod("POST");
			con.setRequestProperty("User-Agent", "Mozilla/5.0");
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
			con.setRequestProperty("Content-Type", "application/json");
			con.setUseCaches(false);

			// with body
			con.setDoOutput(true);		// Indicate that we want to write to the HTTP request
			
			// Writing the post data to the HTTP request body
			BufferedWriter httpRequestBodyWriter = new BufferedWriter(new OutputStreamWriter( con.getOutputStream() ));
			httpRequestBodyWriter.write(body);
			httpRequestBodyWriter.close();
			
			//response
			int responseCode = con.getResponseCode();
			/* System.out.println("\nSending 'POST' request to URL : " + TOKEN);
			System.out.println("Post parameters : " + urlParameters);
			System.out.println("Post body : " + body);
			System.out.println("Response Code : " + responseCode); */

			in = new BufferedReader(new InputStreamReader( con.getInputStream() ));
			response = new StringBuffer();
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

		}catch ( MalformedURLException | ProtocolException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return response.toString();
	}

	public String sendGet(String address, String params) {

		try {
			this.url = new URL(address);
			this.con = (HttpsURLConnection) url.openConnection();

			//add request header
			con.setRequestMethod("GET");
			con.setRequestProperty("User-Agent", "Mozilla/5.0");
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
			con.setRequestProperty("Content-Type", "application/json");
			con.setUseCaches(false);

			// with params
			con.setDoOutput(true);		// Indicate that we want to write to the HTTP request
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(params);
			wr.flush();
			wr.close();

			//response
			int responseCode = con.getResponseCode();
			/* System.out.println("\nSending 'POST' request to URL : " + TOKEN);
			System.out.println("Post parameters : " + urlParameters);
			System.out.println("Post body : " + body);
			System.out.println("Response Code : " + responseCode); */

			in = new BufferedReader(new InputStreamReader( con.getInputStream() ));
			response = new StringBuffer();
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

		}catch ( MalformedURLException | ProtocolException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return response.toString();
	}

	public String sendAuthGet(String address, Token token) {
		try {
			this.url = new URL(address);
			this.con = (HttpsURLConnection) url.openConnection();

			//add request header
			con.setRequestMethod("GET");
			con.setRequestProperty("User-Agent", "Mozilla/5.0");
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Authorization", token.toString() );
			con.setUseCaches(false);

			//response
			int responseCode = con.getResponseCode();
			/* System.out.println("\nSending 'POST' request to URL : " + TOKEN);
			System.out.println("Post parameters : " + urlParameters);
			System.out.println("Post body : " + body);
			System.out.println("Response Code : " + responseCode); */

			in = new BufferedReader(new InputStreamReader( con.getInputStream() ));
			response = new StringBuffer();
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

		}catch ( MalformedURLException | ProtocolException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return response.toString();
	}
}