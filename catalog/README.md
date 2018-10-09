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

## TODO
This section will (_also_) be updated as soon as development goes on.
* Migrate service into Docker containers (in the end...)

### APIs
See this own [Swagger UI](localhost:4000/swagger-ui.html) for API information and testing

### How to run (locally)

* Run Spring Boot app
```
$ ./mvnw spring-boot:run
```

### Author
* **Cristiano Vagos** - [GitHub](https://github.com/cristianovagos)