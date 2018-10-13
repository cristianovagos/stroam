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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import retrofit2.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Component
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
    List<SeriesSeason> seasonList;

    @Value("${spring.jpa.hibernate.ddl-auto}")
    String databaseMethod;

    @Override
    public void run(String... args) throws Exception {
        if (databaseMethod.equals("create")) {
            log.info("Running OmdbMovieRunner...");

            for (int i = 0; i < moviesIDs.length; i++) {
                auxID = moviesIDs[i];

                Response<OmdbResult> response = omdbService.getMovieData(auxID, "full", API_KEY).execute();
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
                        if (production.getSeasons() > 0) {
                            seasonList = new ArrayList<>();
                            for (int j = 1; j <= production.getSeasons(); j++) {
                                production = getSeasonsData(production, j);
                            }
                            production.setSeasonList(seasonList);
                        }
                    } catch (NumberFormatException e) {
                        production.setSeasons(0);
                    }
                    repository.save(production);
                } else {
                    log.error("getMovieData - response not successful");
                }
            }
        } else {
            log.info("Initial data already on database, no need to add");
        }
    }

    private Production getSeasonsData(Production production, int seasonNumber) {
        try {
            Response<OmdbSeasonResult> response = omdbService.getSeasonData(production.getImdbID(), seasonNumber, API_KEY).execute();
            if(response.isSuccessful()) {
                OmdbSeasonResult seasonResult = response.body();
                SeriesSeason season = new SeriesSeason();

                List<Episode> episodes = new ArrayList<>();
                for (OmdbEpisodeSeasonResult e : seasonResult.getEpisodes()) {
                    Episode episode = new Episode();
                    episode.setImdbID(e.getImdbID());
                    episode.setReleaseDate(e.getReleased());
                    episode.setSeason(season);
                    episode.setTitle(e.getTitle());
                    episode.setEpisode(e.getEpisode());

                    episodes.add(episode);
                }

                season.setEpisodes(episodes);
                season.setSeason(seasonResult.getSeason());
                season.setProduction(production);

                seasonList.add(season);
            } else {
                log.error("getSeasonsData onResponse - something failed");
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return production;
    }
}
