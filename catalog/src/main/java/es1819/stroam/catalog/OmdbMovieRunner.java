package es1819.stroam.catalog;

import es1819.stroam.catalog.model.Episode;
import es1819.stroam.catalog.model.Genre;
import es1819.stroam.catalog.model.Production;
import es1819.stroam.catalog.model.SeriesSeason;
import es1819.stroam.catalog.model.retrofit.OmdbEpisodeSeasonResult;
import es1819.stroam.catalog.model.retrofit.OmdbSeasonResult;
import es1819.stroam.catalog.repository.ProductionRepository;
import es1819.stroam.catalog.model.retrofit.OmdbResult;
import es1819.stroam.catalog.model.retrofit.OmdbService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Component
//@Order(1)
public class OmdbMovieRunner implements CommandLineRunner {

    private final String API_KEY = "79c1ed74";

    @Autowired
    OmdbService omdbService;

    @Autowired
    ProductionRepository repository;

    // Primeiros IDs são filmes, o resto séries
    String[] moviesIDs = {
            "tt1270797","tt3829266","tt7040874","tt1517451","tt5814060","tt3778644","tt2119543","tt4779682",
            "tt6998518","tt6781982","tt4881806","tt4154756","tt5052474","tt3104988","tt1502407",

            "tt5580146", "tt1844624","tt1586680","tt0944947","tt0898266","tt5071412","tt0413573","tt7235466",
            "tt7493974", "tt8421350","tt5057054","tt3032476","tt5555260","tt7491982","tt1632701"
    };

    String auxID;

    @Override
    public void run(String... args) throws Exception {
        log.info("Running OmdbMovieRunner...");

        for(int i = 0; i < moviesIDs.length; i++) {
            auxID = moviesIDs[i];

            omdbService.getMovieData(auxID, "full", API_KEY).enqueue(new Callback<OmdbResult>() {
                @Override
                public void onResponse(Call<OmdbResult> call, Response<OmdbResult> response) {
                    if (response.isSuccessful()) {
                        OmdbResult result = response.body();
                        Production production = new Production();
                        production.setName(result.getTitle());
                        production.setDescription(result.getPlot());
                        production.setDirector(result.getDirector());
                        production.setPoster(result.getPoster());
                        production.setReleaseDate(result.getReleased());
                        production.setYear(result.getYear());
                        production.setType(result.getType());
                        production.setRuntime(result.getRuntime());
                        production.setImdbID(result.getImdbID());

                        List<Genre> genreList = new ArrayList<>();
                        String[] genres = result.getGenre().split(",");
                        for (int j = 0; j < genres.length; j++) {
                            Genre genre = new Genre();
                            genre.setName(genres[j].trim());
                            genreList.add(genre);
                        }
                        production.setGenres(new HashSet<>(genreList));

                        try {
                            production.setSeasons(Integer.parseInt(result.getTotalSeasons()));
                        }
                        catch (NumberFormatException e) {
                            production.setSeasons(0);
                        }

                        repository.save(production);
//                        if(result.getTotalSeasons() != null) {
//                            try {
//                                production.setSeasons(Integer.parseInt(result.getTotalSeasons()));
//                            }
//                            catch (NumberFormatException e) {
//                                repository.save(production);
//                                return;
//                            }
//
//                            seasonList = new ArrayList<>();
//                            for (int j = 0; j < Integer.parseInt(result.getTotalSeasons()); j++) {
//                                season = new SeriesSeason();
//                                auxSeasonNumber = j+1;
////                                log.info("\nj = " + j);
//
//                                omdbService.getSeasonData(auxID, j+1, API_KEY).enqueue(new Callback<OmdbSeasonResult>() {
//                                    @Override
//                                    public void onResponse(Call<OmdbSeasonResult> call, Response<OmdbSeasonResult> response) {
//                                        if (response.isSuccessful()) {
//                                            OmdbSeasonResult seasonResult = response.body();
//
////                                            List<Episode> episodes = new ArrayList<>();
////                                            for (OmdbEpisodeSeasonResult e : seasonResult.getEpisodes()) {
////                                                Episode episode = new Episode();
////                                                episode.setImdbID(e.getImdbID());
////                                                episode.setReleaseDate(e.getReleased());
////                                                episode.setSeason(season);
////                                                episode.setTitle(e.getTitle());
////                                                episode.setEpisode(e.getEpisode());
////
////                                                episodes.add(episode);
////                                            }
////
////                                            season.setEpisodes(episodes);
////                                            season.setSeason(seasonResult.getSeason());
////                                            season.setProduction(production);
////
////                                            seasonList.add(season);
////
////                                            if(auxSeasonNumber == Integer.parseInt(result.getTotalSeasons())) {
////                                                production.setSeasonList(seasonList);
////                                                repository.save(production);
////                                            }
//                                        } else {
//                                            log.error("getSeasonData - response not successful");
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onFailure(Call<OmdbSeasonResult> call, Throwable throwable) {
//                                        log.error("getSeasonData - " + throwable.getMessage());
//                                    }
//                                });
//                            }
//                        } else {
//                            repository.save(production);
//                        }
                    } else {
                        log.error("getMovieData - response not successful");
                    }
                }

                @Override
                public void onFailure(Call<OmdbResult> call, Throwable throwable) {
                    log.error("getMovieData - " + throwable.getMessage());
                }
            });

//            omdbService.getMovieData(auxID, "short", API_KEY)
//                    .flatMap((Function<OmdbResult, ObservableSource<OmdbSeasonResult>>) omdbResult -> {
//                        if(omdbResult.getTotalSeasons() != null) {
//                            production = new Production();
//                            production.setName(omdbResult.getTitle());
//                            production.setDescription(omdbResult.getPlot());
//                            production.setDirector(omdbResult.getDirector());
//                            production.setPoster(omdbResult.getPoster());
//                            production.setReleaseDate(omdbResult.getReleased());
//                            production.setYear(omdbResult.getYear());
//                            production.setType(omdbResult.getType());
//                            production.setRuntime(omdbResult.getRuntime());
//                            production.setSeasons(Integer.parseInt(omdbResult.getTotalSeasons()));
//
//                            List<Genre> genreList = new ArrayList<>();
//                            String[] genres = omdbResult.getGenre().split(",");
//                            for (int j = 0; j < genres.length; j++) {
//                                Genre genre = new Genre();
//                                genre.setName(genres[j].trim());
//                                genreList.add(genre);
//                            }
//                            production.setGenres(new HashSet<>(genreList));
//
//                            for (int j = 1; j <= Integer.parseInt(omdbResult.getTotalSeasons()); j++) {
//                                seasonList = new ArrayList<>();
//                                return omdbService.getSeasonData(auxID, j, API_KEY);
//                            }
//                        }
//                        return null;
//                    })
//                    .subscribeWith(new DisposableObserver<OmdbSeasonResult>() {
//                        @Override
//                        public void onNext(OmdbSeasonResult omdbSeasonResult) {
//                            log.info("on Next");
//                            season = new SeriesSeason();
//                            List<Episode> episodes = new ArrayList<>();
//                            for (OmdbEpisodeSeasonResult e : omdbSeasonResult.getEpisodes()) {
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
//                            season.setSeason(omdbSeasonResult.getSeason());
//                            season.setProduction(production);
//
//                            seasonList.add(season);
//                        }
//
//                        @Override
//                        public void onError(Throwable throwable) {
//                            log.info("on Error");
//                        }
//
//                        @Override
//                        public void onComplete() {
//                            log.info("on Complete");
//                            production.setSeasonList(seasonList);
//                            repository.save(production);
//                        }
//                    });
        }
    }
}
