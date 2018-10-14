package es1819.stroam.catalog.controller;

import es1819.stroam.catalog.errors.ProductionNotFoundException;
import es1819.stroam.catalog.model.Production;
import es1819.stroam.catalog.repository.ProductionRepository;
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
public class ProductionController {
    @Autowired
    private ProductionRepository productionRepository;

    @RequestMapping(value = "/catalog", method = GET)
    public List<Production> getAllCatalog() { return productionRepository.findAll(); }

    @RequestMapping(value = "/catalog", method = POST)
    public ResponseEntity<Object> addCatalog(@RequestBody Production production) {
        Production newProd = productionRepository.save(production);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .buildAndExpand(newProd.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @RequestMapping(value = "/catalog/{id}", method = GET)
    public Production getCatalogByID(@PathVariable Long id) {
        Optional<Production> production = productionRepository.findById(id);
        if(!production.isPresent())
            throw new ProductionNotFoundException("id=" + id);
        return production.get();
    }

    @RequestMapping(value = "/catalog/{id}", method = PUT)
    public ResponseEntity<Object> updateCatalogByID(@RequestBody Production prod, @PathVariable Long id) {
        Optional<Production> production = productionRepository.findById(id);
        if(!production.isPresent())
            throw new ProductionNotFoundException("id=" + id);
        prod.setId(id);
        productionRepository.save(prod);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/catalog/{id}", method = DELETE)
    public ResponseEntity<Object> removeCatalogByID(@PathVariable Long id) {
        productionRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
