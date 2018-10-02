package es1819.stroam.catalog.config;

import es1819.stroam.catalog.retrofit.OmdbService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Configuration
public class RetrofitConfig {

    @Bean
    public OmdbService omdbService() {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(JacksonConverterFactory.create())
                .baseUrl("http://www.omdbapi.com")
                .build();
        return retrofit.create(OmdbService.class);
    }
}
