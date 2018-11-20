# STROAM Catalog Service

This is the movies and series Catalog service of the STROAM system.
Built with Spring Boot framework, using JPA to persist data and a REST API to expose data.

## What is already done
This section will be updated as soon as development goes on.
* Basic entities created (Production, Genre, SeriesSeason and Episode), persisting to H2 database
* CRUD capabilities for each entity
* REST API for Productions (movies + series), Seasons and Episodes (series specific)
* Initial database loader with basic data (from OMDb API): 15 movies + 15 series, as well as the series seasons and episodes
* Swagger 2 support
* Already 'Dockerized' (read the root project README, and this 'How to run' for more info)
* Send push notifications to STROAM Frontend

## TODO
This section will (_also_) be updated as soon as development goes on.
* Complete notification system (on adding productions to Genres, new Season, new Episode, etc)

### APIs
See this own Swagger UI (<catalog_host:catalog_port>/swagger-ui.html) for API information and testing

### How to run

Use Docker and Docker Compose (_read project README_) to create a container and run STROAM as a whole.
The STROAM Catalog is already depending and relying on multiple services, so it's better to deploy and run the whole system and enjoy it from that.

Other info that may be useful for deployment on Docker:

**Environment Variables**:
* NOTIFICATION_SERVER_HOST: IP host of the Notification Server (_NotTheService_) (127.0.0.1 by default)
* NOTIFICATION_SERVER_PORT: port of the Notification Server (_NotTheService_) (1884 by default)

### How to run (locally) - NOT RECOMMENDED

* Run Spring Boot app
```
$ ./mvnw spring-boot:run
```

### Author
* **Cristiano Vagos** - [GitHub](https://github.com/cristianovagos)