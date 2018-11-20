package es1819.stroam.catalog.controller;

import es1819.stroam.catalog.errors.ProductionNotFoundException;
import es1819.stroam.catalog.model.Genre;
import es1819.stroam.catalog.model.Production;
import es1819.stroam.catalog.model.notifications.Email;
import es1819.stroam.catalog.model.notifications.NotificationMessage;
import es1819.stroam.catalog.repository.ProductionRepository;
import es1819.stroam.catalog.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Autowired
    private NotificationService notificationService;

    @Value("${stroam.frontend.host}")
    private String FRONTEND_HOST;

    @Value("${stroam.frontend.port}")
    private String FRONTEND_PORT;

    @Value("${stroam.frontend.genre-path}")
    private String FRONTEND_GENRE_PATH;

    @RequestMapping(value = "/v1/catalog", method = GET)
    public List<Production> getAllCatalog() { return productionRepository.findAll(); }

    @RequestMapping(value = "/v1/catalog", method = POST)
    public ResponseEntity<Object> addCatalog(@RequestBody Production production) {
        Production newProd = productionRepository.save(production);

        try {
            notificationService.connect();
            for (Genre g : newProd.getGenres()) {
                NotificationMessage message = new NotificationMessage();
                message.setTitle("NEW " + g.getName().toUpperCase() + " " + newProd.getType().toUpperCase() + ":");
                message.setMessage(newProd.getName() + " is now available at STROAM!");
                message.setUrl_path(FRONTEND_HOST + ":" + FRONTEND_PORT + FRONTEND_GENRE_PATH + newProd.getId());
                notificationService.sendPushNotification("stroam-" + g.getName(), message);

                Email emailMessage = new Email();
                emailMessage.setSubject("[STROAM] NEW " + g.getName().toUpperCase() + " " + newProd.getType().toUpperCase());
                emailMessage.setBody("Hi,\nJust to inform that there is a new " + g.getName().toUpperCase() +
                        " " + newProd.getType() + " added to STROAM library. Make sure you don't miss that!" +
                        "\n\nYou can view more details on " + message.getUrl_path());
                notificationService.sendEmail("stroam-" + g.getName(), emailMessage);
            }
        } catch (Exception e) {
            log.error("");
        }

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .buildAndExpand(newProd.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @RequestMapping(value = "/v1/catalog/{id}", method = GET)
    public Production getCatalogByID(@PathVariable Long id) {
        Optional<Production> production = productionRepository.findById(id);
        if(!production.isPresent())
            throw new ProductionNotFoundException("id=" + id);
        return production.get();
    }

    @RequestMapping(value = "/v1/catalog/{id}", method = PUT)
    public ResponseEntity<Object> updateCatalogByID(@RequestBody Production prod, @PathVariable Long id) {
        Optional<Production> production = productionRepository.findById(id);
        if(!production.isPresent())
            throw new ProductionNotFoundException("id=" + id);
        prod.setId(id);
        productionRepository.save(prod);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/v1/catalog/{id}", method = DELETE)
    public ResponseEntity<Object> removeCatalogByID(@PathVariable Long id) {
        productionRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
