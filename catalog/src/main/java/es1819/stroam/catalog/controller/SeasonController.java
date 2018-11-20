package es1819.stroam.catalog.controller;

import es1819.stroam.catalog.errors.ProductionNotFoundException;
import es1819.stroam.catalog.errors.SeasonNotFoundException;
import es1819.stroam.catalog.model.Genre;
import es1819.stroam.catalog.model.Production;
import es1819.stroam.catalog.model.SeriesSeason;
import es1819.stroam.catalog.model.notifications.Email;
import es1819.stroam.catalog.model.notifications.NotificationMessage;
import es1819.stroam.catalog.repository.ProductionRepository;
import es1819.stroam.catalog.repository.SeasonRepository;
import es1819.stroam.catalog.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@Slf4j
@RestController
@RequestMapping("/api")
public class SeasonController {
    @Autowired
    private SeasonRepository seasonRepository;

    @Autowired
    private ProductionRepository productionRepository;

    @Autowired
    private NotificationService notificationService;

    @Value("${stroam.frontend.host}")
    private String FRONTEND_HOST;

    @Value("${stroam.frontend.port}")
    private String FRONTEND_PORT;

    @Value("${stroam.frontend.movie-path}")
    private String FRONTEND_MOVIE_PATH;

    @RequestMapping(value = "/v1/catalog/{id}/season", method = GET)
    public List<SeriesSeason> getAllSeasonsByProdID(@PathVariable("id") Long productionID) {
        return seasonRepository.findAllByProductionId(productionID);
    }

    @RequestMapping(value = "/v1/catalog/{prodID}/season", method = POST)
    public ResponseEntity<Object> addSeason(@PathVariable("prodID") Long productionID, @RequestBody SeriesSeason season) {
        Optional<Production> production = productionRepository.findById(productionID);
        if(!production.isPresent())
            throw new ProductionNotFoundException("id=" + productionID);

        // Check if the season to be added already exists
        Optional<SeriesSeason> existingSeason = seasonRepository.findByProductionIdAndSeason(productionID, season.getSeason());
        if(existingSeason.isPresent())
            return new ResponseEntity<>("Season already exists", HttpStatus.CONFLICT);

        season.setProduction(production.get());
        SeriesSeason newSeason = seasonRepository.save(season);

        try {
            notificationService.connect();

            NotificationMessage message = new NotificationMessage();
            message.setTitle(production.get().getName() + " - Season " + newSeason.getSeason() + " added:");
            message.setMessage("Season " + newSeason.getSeason() + " of " + production.get().getName() + " is now available at STROAM!");
            message.setUrl_path(FRONTEND_HOST + ":" + FRONTEND_PORT + FRONTEND_MOVIE_PATH + production.get().getId());
            notificationService.sendPushNotification("stroam-movie" + production.get().getId(), message);

            Email emailMessage = new Email();
            emailMessage.setSubject("[STROAM] " + production.get().getName() + " - Season " + newSeason.getSeason() + " added");
            emailMessage.setBody("Hi,\nJust to inform that there is a new season of " +
                " " + production.get().getName() + " added to STROAM library. Make sure you don't miss that!" +
                "\n\nYou can view more details on " + message.getUrl_path());
            notificationService.sendEmail("stroam-movie" + production.get().getId(), emailMessage);
        } catch (Exception e) {
            log.error("");
        }

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .buildAndExpand(newSeason.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @RequestMapping(value = "/v1/catalog/{prodID}/season/{num}", method = GET)
    public SeriesSeason getSeasonByProdID(@PathVariable("prodID") Long productionID, @PathVariable("num") int seasonNumber) {
        Optional<SeriesSeason> season = seasonRepository.findByProductionIdAndSeason(productionID, seasonNumber);
        if(!season.isPresent())
            throw new SeasonNotFoundException("prodID=" + productionID + ", seasonNum=" + seasonNumber);
        return season.get();
    }

    @RequestMapping(value = "/v1/catalog/{prodID}/season/{num}", method = PUT)
    public ResponseEntity<Object> updateSeasonByProdID(@PathVariable("prodID") Long productionID,
                                                   @PathVariable("num") int seasonNumber, @RequestBody SeriesSeason season) {
        Optional<SeriesSeason> season1 = seasonRepository.findByProductionIdAndSeason(productionID, seasonNumber);
        if(!season1.isPresent())
            throw new SeasonNotFoundException("prodID=" + productionID + ", seasonNum=" + seasonNumber);

        season.setId(season1.get().getId());
        seasonRepository.save(season);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/v1/catalog/{prodID}/season/{num}", method = DELETE)
    public ResponseEntity<Object> deleteSeasonByID(@PathVariable("prodID") Long productionID, @PathVariable("num") int seasonNumber) {
        seasonRepository.deleteByProductionIdAndSeason(productionID, seasonNumber);
        return ResponseEntity.ok().build();
    }
}
