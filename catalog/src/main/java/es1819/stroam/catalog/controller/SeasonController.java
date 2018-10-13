package es1819.stroam.catalog.controller;

import es1819.stroam.catalog.errors.ProductionNotFoundException;
import es1819.stroam.catalog.errors.SeasonNotFoundException;
import es1819.stroam.catalog.model.Production;
import es1819.stroam.catalog.model.SeriesSeason;
import es1819.stroam.catalog.repository.ProductionRepository;
import es1819.stroam.catalog.repository.SeasonRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @RequestMapping(value = "/catalog/{id}/season", method = GET)
    public List<SeriesSeason> getAllSeasonsByID(@PathVariable("id") Long productionID) {
        return seasonRepository.findAllByProductionId(productionID);
    }

    @RequestMapping(value = "/catalog/{prodID}/season/{num}", method = GET)
    public SeriesSeason getSeasonByID(@PathVariable("prodID") Long productionID, @PathVariable("num") int seasonNumber) {
        Optional<SeriesSeason> season = seasonRepository.findByProductionIdAndAndSeason(productionID, seasonNumber);
        if(!season.isPresent())
            throw new SeasonNotFoundException("prodID=" + productionID + ", seasonNum=" + seasonNumber);
        return season.get();
    }
}
