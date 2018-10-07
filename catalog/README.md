# STROAM Catalog Service

This is the movies and series Catalog service of the STROAM system.
Built with Spring Boot framework, using JPA to persist data and a REST API to expose data.

## What is already done
This section will be updated as soon as development goes on.
* Basic entities created (Production, Genre, SeriesSeason and Episode), persisting to H2 database
* CRUD capabilities for each entity
* REST API for Productions (movies + series), Seasons and Episodes (series specific)
* Initial database loader with basic data (from OMDb API): 15 movies + 15 series, as well as the series seasons and episodes

## TODO
This section will (_also_) be updated as soon as development goes on.
* Build more APIs for frontend consumption (_as needed_)
* Migrate service into Docker containers (in the end...)

### APIs
This section will (_also_) be updated as soon as development goes on.

* Get all catalog: /api/catalog
* Get specific movie/series by ID: /api/catalog/< id >
* Put specific movie/series by ID: /api/catalog/< id >
* Delete specific movie/series by ID: /api/catalog/< id >

### How to run (locally)

* Run Spring Boot app
```
$ ./mvnw spring-boot:run
```

### Author
* **Cristiano Vagos** - [GitHub](https://github.com/cristianovagos)