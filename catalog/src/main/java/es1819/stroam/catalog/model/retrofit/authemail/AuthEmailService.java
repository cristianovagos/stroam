package es1819.stroam.catalog.model.retrofit.authemail;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface AuthEmailService {
    @GET("/api/email/{userID}")
    Call<AuthEmailInfo> getEmailByUserID(@Path("userID") int userID);
}
