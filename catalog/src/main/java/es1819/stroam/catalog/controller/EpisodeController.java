package es1819.stroam.catalog.controller;

import es1819.stroam.catalog.errors.EpisodeNotFoundException;
import es1819.stroam.catalog.errors.SeasonNotFoundException;
import es1819.stroam.catalog.model.Episode;
import es1819.stroam.catalog.model.SeriesSeason;
import es1819.stroam.catalog.model.notifications.Email;
import es1819.stroam.catalog.model.notifications.NotificationMessage;
import es1819.stroam.catalog.repository.EpisodeRepository;
import es1819.stroam.catalog.repository.ProductionRepository;
import es1819.stroam.catalog.repository.SeasonRepository;
import es1819.stroam.catalog.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api")
public class EpisodeController {
    @Autowired
    EpisodeRepository episodeRepository;

    @Autowired
    SeasonRepository seasonRepository;

    @Autowired
    ProductionRepository productionRepository;

    @Autowired
    private NotificationService notificationService;

    @Value("${stroam.frontend.host}")
    private String FRONTEND_HOST;

    @Value("${stroam.frontend.port}")
    private String FRONTEND_PORT;

    @Value("${stroam.frontend.movie-path}")
    private String FRONTEND_MOVIE_PATH;

    @RequestMapping(value = "/v1/catalog/{prodID}/season/{seasonNum}/episode", method = RequestMethod.GET)
    public List<Episode> getAllEpisodesFromProductionSeason(@PathVariable("prodID") Long productionID,
                                                            @PathVariable("seasonNum") int seasonNum) {
        Optional<SeriesSeason> season = seasonRepository.findByProductionIdAndSeason(productionID, seasonNum);
        if(!season.isPresent())
            throw new SeasonNotFoundException("prodID=" + productionID + ", seasonNum=" + seasonNum);

        return season.get().getEpisodes();
    }

    @RequestMapping(value = "/v1/catalog/{prodID}/season/{seasonNum}/episode", method = RequestMethod.POST)
    public ResponseEntity<Object> addEpisodeToSeasonFromProduction(@PathVariable("prodID") Long productionID,
                                                                   @PathVariable("seasonNum") int seasonNum,
                                                                   @RequestBody Episode episode) {
        Optional<SeriesSeason> season = seasonRepository.findByProductionIdAndSeason(productionID, seasonNum);
        if(!season.isPresent())
            throw new SeasonNotFoundException("prodID=" + productionID + ", seasonNum=" + seasonNum);

        // Check if the episode to be added already exists
        Optional<Episode> existingEpisode = episodeRepository.findBySeasonAndEpisode(season.get().getId(), episode.getEpisode());
        if(existingEpisode.isPresent())
            return new ResponseEntity<>("Episode already exists in this season", HttpStatus.CONFLICT);

        episode.setSeason(season.get());
        Episode newEpisode = episodeRepository.save(episode);

        try {
            notificationService.connect();

            NotificationMessage message = new NotificationMessage();
            message.setTitle(season.get().getProduction().getName() + " - New Episode added:");
            message.setMessage("Episode " + newEpisode.getEpisode() + " of Season " + newEpisode.getSeason().getSeason()
                + ", from " + newEpisode.getSeason().getProduction().getName() + " is now available at STROAM!");
            message.setUrl_path(FRONTEND_HOST + ":" + FRONTEND_PORT + FRONTEND_MOVIE_PATH + newEpisode.getSeason().getProduction().getId());
            notificationService.sendPushNotification("stroam-movie" + newEpisode.getSeason().getProduction().getId(), message);

            Email emailMessage = new Email();
            emailMessage.setSubject("[STROAM] " + newEpisode.getSeason().getProduction().getName() + " - Episode "
                + newEpisode.getEpisode() + " of Season " + newEpisode.getSeason().getSeason() + " added");
            emailMessage.setBody("Hi,\nJust to inform that there is a new episode of Season " + newEpisode.getSeason().getSeason() +
                ", from " + newEpisode.getSeason().getProduction().getName() + " added to STROAM library. Make sure you don't miss that!" +
                "\n\nYou can view more details on " + message.getUrl_path());
            notificationService.sendEmail("stroam-movie" + newEpisode.getSeason().getProduction().getId(), emailMessage);
        } catch (Exception e) {
            log.error("");
        }

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .buildAndExpand(newEpisode.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @RequestMapping(value = "/v1/catalog/{prodID}/season/{seasonNum}/episode/{episodeNum}", method = RequestMethod.GET)
    public Episode getEpisodeByNumFromSeasonAndProductionID(@PathVariable("prodID") Long productionID,
                                                            @PathVariable("seasonNum") int seasonNum,
                                                            @PathVariable("episodeNum") int episodeNum) {
        Optional<SeriesSeason> season = seasonRepository.findByProductionIdAndSeason(productionID, seasonNum);
        if(!season.isPresent())
            throw new SeasonNotFoundException("prodID=" + productionID + ", seasonNum=" + seasonNum);

        Optional<Episode> episode = episodeRepository.findBySeasonAndEpisode(season.get().getId(), episodeNum);
        if(!episode.isPresent())
            throw new EpisodeNotFoundException("prodID=" + productionID + ", seasonNum=" + seasonNum + ", episodeNum=" + episodeNum);
        return episode.get();
    }

    @RequestMapping(value = "/v1/catalog/{prodID}/season/{seasonNum}/episode/{episodeNum}", method = RequestMethod.PUT)
    public ResponseEntity<Object> updateEpisodeByNumFromSeasonAndProductionID(@PathVariable("prodID") Long productionID,
                                                                              @PathVariable("seasonNum") int seasonNum,
                                                                              @PathVariable("episodeNum") int episodeNum,
                                                                              @RequestBody Episode newEpisode) {
        Optional<SeriesSeason> season = seasonRepository.findByProductionIdAndSeason(productionID, seasonNum);
        if(!season.isPresent())
            throw new SeasonNotFoundException("prodID=" + productionID + ", seasonNum=" + seasonNum);

        Optional<Episode> episode = episodeRepository.findBySeasonAndEpisode(season.get().getId(), episodeNum);
        if(!episode.isPresent())
            throw new EpisodeNotFoundException("prodID=" + productionID + ", seasonNum=" + seasonNum + ", episodeNum=" + episodeNum);

        newEpisode.setId(episode.get().getId());
        episodeRepository.save(newEpisode);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/v1/catalog/{prodID}/season/{seasonNum}/episode/{episodeNum}", method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteEpisodeByNumFromSeasonAndProductionID(@PathVariable("prodID") Long productionID,
                                                            @PathVariable("seasonNum") int seasonNum,
                                                            @PathVariable("episodeNum") int episodeNum) {
        Optional<SeriesSeason> season = seasonRepository.findByProductionIdAndSeason(productionID, seasonNum);
        if(!season.isPresent())
            throw new SeasonNotFoundException("prodID=" + productionID + ", seasonNum=" + seasonNum);

        episodeRepository.deleteBySeasonAndEpisode(season.get().getId(), episodeNum);
        return ResponseEntity.ok().build();
    }
}
