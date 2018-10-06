package es1819.stroam.catalog.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class SeasonNotFoundException extends RuntimeException {
    public SeasonNotFoundException(String exception) { super(exception); }
}
