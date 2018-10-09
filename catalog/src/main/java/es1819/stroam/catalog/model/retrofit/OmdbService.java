package es1819.stroam.catalog.model.retrofit;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OmdbService {
    @GET("/")
    Call<OmdbResult> getMovieData(@Query("i") String movieID, @Query("plot") String plot, @Query("apikey") String apiKey);

    @GET("/")
    Call<OmdbSeasonResult> getSeasonData(@Query("i") String seriesID, @Query("Season") int season, @Query("apikey") String apiKey);
}
