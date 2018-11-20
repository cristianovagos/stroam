package es1819.stroam.catalog.model.retrofit.frontend;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface FrontendService {
    @GET("/api/v1/subscriptions/{channel}")
    Call<FrontendChannelInfo> getUserSubscriptions(@Path("channel") String channel_name);
}
