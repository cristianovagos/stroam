package es.stroam.authserver.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;


public class TokenService {

    public TokenService() {}

    public String create() {
        
        String token = "";

        LocalDateTime dateTime = LocalDateTime.now().plus(Duration.of(30, ChronoUnit.MINUTES));
        Date tmfn = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
        
        try {
            Algorithm algorithm = Algorithm.HMAC256("secret");
            token = JWT.create()
                .withIssuer("auth0").withExpiresAt(tmfn)
                .sign(algorithm);
            //System.out.println(token);
            
        } catch (JWTCreationException exception){
            //Invalid Signing configuration / Couldn't convert Claims.
        }
        return token;
    }

    public boolean verify(String token) {

        //String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXUyJ9.eyJpc3MiOiJhdXRoMCJ9.AbIJTDMFc7yUa5MhvcP03nJPyCPzZtQcGEp-zWfOkEE";
        try {
            Algorithm algorithm = Algorithm.HMAC256("secret");
            JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer("auth0")
                .build(); //Reusable verifier instance
            DecodedJWT jwt = verifier.verify(token);
            
            Date issuedAt = jwt.getIssuedAt();
            //System.out.println(issuedAt);
            
            if (new Date().after(issuedAt)) {
                return false;
            }
            
            /*System.out.println(jwt.getToken());
            System.out.println(jwt.getHeader());
            System.out.println( jwt.getPayload());
            System.out.println(jwt.getSignature());
            System.out.println(jwt.getIssuedAt());
            System.out.println(jwt.getIssuer());*/

        } catch (JWTVerificationException exception){
            //Invalid signature/claims
        }
        return true;
    }
}