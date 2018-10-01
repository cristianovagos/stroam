package es1819.stroam.catalog.production;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProductionNotFoundException extends RuntimeException {
    public ProductionNotFoundException(String exception) { super(exception); }
}
