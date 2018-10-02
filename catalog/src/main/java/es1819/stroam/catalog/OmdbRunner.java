package es1819.stroam.catalog;

import es1819.stroam.catalog.genre.Genre;
import es1819.stroam.catalog.production.Production;
import es1819.stroam.catalog.production.ProductionRepository;
import es1819.stroam.catalog.retrofit.OmdbResult;
import es1819.stroam.catalog.retrofit.OmdbService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Component
public class OmdbRunner implements CommandLineRunner {

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

    @Override
    public void run(String... args) throws Exception {
        for(int i = 0; i < moviesIDs.length; i++) {
            omdbService.getMovieData(moviesIDs[i], "79c1ed74").enqueue(new Callback<OmdbResult>() {
                @Override
                public void onResponse(Call<OmdbResult> call, Response<OmdbResult> response) {
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

                    List<Genre> genreList = new ArrayList<>();
                    String[] genres = result.getGenre().split(",");
                    for (int j = 0; j < genres.length; j++) {
                        Genre genre = new Genre();
                        genre.setName(genres[j].trim());
                        genreList.add(genre);
                    }
                    production.setGenres(new HashSet<>(genreList));
                    repository.save(production);
                }

                @Override
                public void onFailure(Call<OmdbResult> call, Throwable throwable) {
                    log.error(throwable.getMessage());
                }
            });
        }
    }
}
