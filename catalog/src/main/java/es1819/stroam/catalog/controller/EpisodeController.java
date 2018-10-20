package es1819.stroam.catalog.controller;

import es1819.stroam.catalog.errors.EpisodeNotFoundException;
import es1819.stroam.catalog.errors.SeasonNotFoundException;
import es1819.stroam.catalog.model.Episode;
import es1819.stroam.catalog.model.SeriesSeason;
import es1819.stroam.catalog.repository.EpisodeRepository;
import es1819.stroam.catalog.repository.ProductionRepository;
import es1819.stroam.catalog.repository.SeasonRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
