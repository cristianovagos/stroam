package es1819.stroam.catalog.config;

import es1819.stroam.catalog.model.retrofit.OmdbService;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.concurrent.TimeUnit;

@Configuration
public class RetrofitConfig {

    @Bean
    public OmdbService omdbService() {
        final Logger logger = LoggerFactory.getLogger(RetrofitConfig.class);
        final Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(JacksonConverterFactory.create())
                .client(new OkHttpClient.Builder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .addInterceptor(chain -> {
                            Request request = chain.request();
                            logger.info("Sending request to url: {}", request.url());
                            Response response = chain.proceed(request);
                            logger.info("Received response for call: {}", request.url());
                            return response;
                        })
                        .build()
                )
                .baseUrl("http://www.omdbapi.com")
                .build();
        return retrofit.create(OmdbService.class);
    }
}
