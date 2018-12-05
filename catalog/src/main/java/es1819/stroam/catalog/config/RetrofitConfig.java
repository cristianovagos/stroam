package es1819.stroam.catalog.config;

import es1819.stroam.catalog.model.retrofit.authemail.AuthEmailService;
import es1819.stroam.catalog.model.retrofit.frontend.FrontendService;
import es1819.stroam.catalog.model.retrofit.omdb.OmdbService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
public class RetrofitConfig {

    @Value("${stroam.frontend.host}")
    private String FRONTEND_HOST;

    @Value("${stroam.frontend.port}")
    private String FRONTEND_PORT;

    @Value("${stroam.auth.host}")
    private String AUTH_HOST;

    @Value("${stroam.auth.port}")
    private String AUTH_PORT;

    @Bean
    public OmdbService omdbService() {
        final Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(JacksonConverterFactory.create())
                .client(new OkHttpClient.Builder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .addInterceptor(chain -> {
                            Request request = chain.request();
                            log.info("Sending request to url: {}", request.url());
                            Response response = chain.proceed(request);
                            log.info("Received response for call: {}", request.url());
                            return response;
                        })
                        .build()
                )
                .baseUrl("http://www.omdbapi.com")
                .build();
        return retrofit.create(OmdbService.class);
    }

    @Bean
    public FrontendService frontendService() {
        final Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(JacksonConverterFactory.create())
                .baseUrl("http://" + FRONTEND_HOST + ":" + FRONTEND_PORT)
                .build();
        return retrofit.create(FrontendService.class);
    }

    @Bean
    public AuthEmailService authEmailService() {
        final Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(JacksonConverterFactory.create())
                .baseUrl("http://" + AUTH_HOST + ":" + AUTH_PORT)
                .build();
        return retrofit.create(AuthEmailService.class);
    }
}
