package es1819.stroam.catalog;

import es1819.stroam.catalog.model.Episode;
import es1819.stroam.catalog.model.Production;
import es1819.stroam.catalog.model.SeriesSeason;
import es1819.stroam.catalog.model.retrofit.OmdbEpisodeSeasonResult;
import es1819.stroam.catalog.model.retrofit.OmdbSeasonResult;
import es1819.stroam.catalog.model.retrofit.OmdbService;
import es1819.stroam.catalog.repository.ProductionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
//@Component
//@Order(2)
public class OmdbSeriesRunner implements CommandLineRunner {

    private final String API_KEY = "79c1ed74";

    @Autowired
    OmdbService omdbService;

    @Autowired
    ProductionRepository repository;

    Production auxProd;
    SeriesSeason season;

    @Override
    public void run(String... args) throws Exception {
        log.info("Running OmdbSeriesRunner...");

//        List<Production> list = repository.findAll();
////        List<Production> list = repository.findAll().stream().filter(p -> p.getType().equals("series"))
////                .collect(Collectors.toList());
//
//        for(Production p : list) {
//            auxProd = p;
//            List<SeriesSeason> seasonList = new ArrayList<>();
//            for (int i = 1; i <= p.getSeasons(); i++) {
//                season = new SeriesSeason();
//                omdbService.getSeasonData(p.getImdbID(), i, API_KEY).enqueue(new Callback<OmdbSeasonResult>() {
//                    @Override
//                    public void onResponse(Call<OmdbSeasonResult> call, Response<OmdbSeasonResult> response) {
//                        if(response.isSuccessful()) {
//                            OmdbSeasonResult result = response.body();
//
//                            List<Episode> episodes = new ArrayList<>();
//                            for (OmdbEpisodeSeasonResult e : result.getEpisodes()) {
//                                Episode episode = new Episode();
//                                episode.setImdbID(e.getImdbID());
//                                episode.setReleaseDate(e.getReleased());
//                                episode.setSeason(season);
//                                episode.setTitle(e.getTitle());
//                                episode.setEpisode(e.getEpisode());
//
//                                episodes.add(episode);
//                            }
//
//                            season.setEpisodes(episodes);
//                            season.setSeason(result.getSeason());
//                            season.setProduction(auxProd);
//
//                            seasonList.add(season);
//                        } else {
//                            log.error("getSeasonData - response not successful");
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<OmdbSeasonResult> call, Throwable throwable) {
//                        log.error("getSeasonData - onFailure: " + throwable.getMessage());
//                    }
//                });
//            }
//
//            auxProd.setSeasonList(seasonList);
//            repository.save(auxProd);
//        }
    }
}
