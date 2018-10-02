package es1819.stroam.catalog.retrofit;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OmdbService {
    @GET("/")
    Call<OmdbResult> getMovieData(@Query("i") String movieID, @Query("apikey") String apiKey);
}
