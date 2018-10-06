package es1819.stroam.catalog.config;

import es1819.stroam.catalog.model.retrofit.OmdbService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Configuration
public class RetrofitConfig {

    @Bean
    public OmdbService omdbService() {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl("http://www.omdbapi.com")
                .build();
        return retrofit.create(OmdbService.class);
    }
}
