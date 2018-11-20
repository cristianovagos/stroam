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
* Send push notifications to STROAM Frontend and emails to users subscribed on adding Productions, Seasons and Episode, and on REST endpoint

## TODO
This section will (_also_) be updated as soon as development goes on.
* Get emails from users subscribed based on user ID (_needs Auth service_)

### APIs
See this own Swagger UI (<catalog_host:catalog_port>/swagger-ui.html) for API information and testing

### How to run

Use Docker and Docker Compose (_read project README_) to create a container and run STROAM as a whole.
The STROAM Catalog is already depending and relying on multiple services, so it's better to deploy and run the whole system and enjoy it from that.

Other info that may be useful for deployment on Docker:

**Environment Variables**:
* NOTIFICATION_SERVER_HOST: IP host of the Notification Server (_NotTheService_) (127.0.0.1 by default)
* NOTIFICATION_SERVER_PORT: port of the Notification Server (_NotTheService_) (1884 by default)
* FRONTEND_HOST: IP host of the Frontend (127.0.0.1 by default)
* FRONTEND PORT: port of the Frontend (8000 by default)
* FRONTEND_MOVIE_URL: url path for the movies (/movie/ by default)
* FRONTEND_GENRE_URL: url path for the genre (/genre/ by default)

### How to run (locally) - NOT RECOMMENDED

* Run Spring Boot app
```
$ ./mvnw spring-boot:run
```

### Author
* **Cristiano Vagos** - [GitHub](https://github.com/cristianovagos)