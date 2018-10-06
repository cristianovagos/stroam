package es1819.stroam.catalog;

import es1819.stroam.catalog.model.Genre;
import es1819.stroam.catalog.model.Production;
import es1819.stroam.catalog.model.SeriesSeason;
import es1819.stroam.catalog.model.retrofit.OmdbResult;
import es1819.stroam.catalog.model.retrofit.OmdbSeasonResult;
import es1819.stroam.catalog.model.retrofit.OmdbService;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Slf4j
//@Component
public class OmdbRxRunner implements CommandLineRunner {

    private final String API_KEY = "79c1ed74";

    @Autowired
    OmdbService omdbService;

    // Primeiros IDs são filmes, o resto séries
    String[] moviesIDs = {
            "tt1270797","tt3829266","tt7040874","tt1517451","tt5814060","tt3778644","tt2119543","tt4779682",
            "tt6998518","tt6781982","tt4881806","tt4154756","tt5052474","tt3104988","tt1502407",

            "tt5580146", "tt1844624","tt1586680","tt0944947","tt0898266","tt5071412","tt0413573","tt7235466",
            "tt7493974", "tt8421350","tt5057054","tt3032476","tt5555260","tt7491982","tt1632701"
    };

    String auxID;
    Production production;
    List<SeriesSeason> seasonList;

    @Override
    public void run(String... args) throws Exception {
        for(String movie : moviesIDs) {
            auxID = movie;

//            omdbService.getMovieData(auxID, "short", API_KEY)
//                    .flatMapIterable((Function<OmdbResult, List<ObservableSource<OmdbSeasonResult>>>) omdbResult -> {
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
//                            List<ObservableSource<OmdbSeasonResult>> observables = new ArrayList<>();
//
//                            for (int j = 1; j <= Integer.parseInt(omdbResult.getTotalSeasons()); j++) {
//                                seasonList = new ArrayList<>();
//                                observables.add(omdbService.getSeasonData(omdbResult.getImdbID(), j, API_KEY));
//                            }
//                            return observables;
//                        }
//                        return null;
//                    })
//                    .flatMap(new F)
//            Observable<OmdbSeasonResult> observable = omdbService.getMovieData(auxID, "short", API_KEY)
//                    .doOnNext(omdbResult -> {
//                        log.info("accept");
//                    })
//                    .flatMapIterable(omdbResult -> {
//                        List<Observable<OmdbSeasonResult>> list = new ArrayList<>();
//
//                        try {
//                            for (int i = 0; i < Integer.parseInt(omdbResult.getTotalSeasons()); i++) {
//                                list.add(omdbService.getSeasonData(omdbResult.getImdbID(), i, API_KEY));
//                            }
//                        } catch (Exception e) {
//                            return null;
//                        }
//
//                        return list;
//                    })
//
//            observable.subscribeWith(new Observer<OmdbSeasonResult>() {
//                @Override
//                public void onSubscribe(Disposable disposable) {
//                    log.info("subscribe");
//                }
//
//                @Override
//                public void onNext(OmdbSeasonResult omdbSeasonResult) {
//                    log.info("subscribe");
//                }
//
//                @Override
//                public void onError(Throwable throwable) {
//                    log.info("subscribe");
//                }
//
//                @Override
//                public void onComplete() {
//                    log.info("subscribe");
//                }
//            });
        }
    }
}
