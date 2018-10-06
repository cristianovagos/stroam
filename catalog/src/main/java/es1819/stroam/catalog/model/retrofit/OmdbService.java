package es1819.stroam.catalog.model.retrofit;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OmdbService {
    @GET("/")
    Call<OmdbResult> getMovieData(@Query("i") String movieID, @Query("plot") String plot, @Query("apikey") String apiKey);
//    Observable<OmdbResult> getMovieData(@Query("i") String movieID, @Query("plot") String plot, @Query("apikey") String apiKey);

    @GET("/")
    Call<OmdbSeasonResult> getSeasonData(@Query("i") String seriesID, @Query("Season") int season, @Query("apikey") String apiKey);
//    Observable<OmdbSeasonResult> getSeasonData(@Query("i") String seriesID, @Query("Season") int season, @Query("apikey") String apiKey);

}
