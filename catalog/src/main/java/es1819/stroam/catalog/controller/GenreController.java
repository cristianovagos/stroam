package es1819.stroam.catalog.controller;

import es1819.stroam.catalog.errors.GenreNotFoundException;
import es1819.stroam.catalog.errors.ProductionNotFoundException;
import es1819.stroam.catalog.model.Genre;
import es1819.stroam.catalog.model.Production;
import es1819.stroam.catalog.repository.GenreRepository;
import es1819.stroam.catalog.repository.ProductionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.xml.ws.Response;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@Slf4j
@RestController
@RequestMapping("/api")
public class GenreController {

    @Autowired
    private ProductionRepository productionRepository;

    @Autowired
    private GenreRepository genreRepository;

    @RequestMapping(value = "/v1/catalog/genre", method = GET)
    public List<Genre> getAllGenres(@RequestParam(value = "name", required = false) String name) {
        if(name != null) {
            Optional<Genre> genreOptional = genreRepository.findByName(name);
            if(!genreOptional.isPresent())
                throw new GenreNotFoundException("name=" + name);
            return Collections.singletonList(genreOptional.get());
        }

        return genreRepository.findAll();
    }

    @RequestMapping(value = "/v1/catalog/genre", method = POST)
    public ResponseEntity<Object> addGenre(@RequestBody Genre genre) {
        Genre newGenre = genreRepository.save(genre);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .buildAndExpand(newGenre.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @RequestMapping(value = "/v1/catalog/genre/{id}", method = DELETE)
    public ResponseEntity<Object> removeGenreByID(@PathVariable Long id) {
        genreRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/v1/catalog/genre/{id}", method = GET)
    public Genre getGenreByID(@PathVariable("id") Long id) {
        Optional<Genre> genre = genreRepository.findById(id);
        if(!genre.isPresent())
            throw new GenreNotFoundException("id=" + id);
        return genre.get();
    }

    @RequestMapping(value = "/v1/catalog/genre/{id}", method = PUT)
    public ResponseEntity<Object> updateGenreByID(@PathVariable Long id, @RequestBody Genre genre) {
        Optional<Genre> genre1 = genreRepository.findById(id);
        if(!genre1.isPresent())
            throw new GenreNotFoundException("id=" + id);
        genre.setId(genre1.get().getId());
        genreRepository.save(genre);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/v1/catalog/genre/{id}/productions", method = GET)
    public List<Production> getProductionsByGenreID(@PathVariable("id") Long id) {
        Optional<Genre> genre = genreRepository.findById(id);
        if(!genre.isPresent())
            throw new GenreNotFoundException("id=" + id);

        Optional<List<Production>> productionList = productionRepository.findAllByGenres_id(id);
        return productionList.orElse(Collections.emptyList());
    }
}
