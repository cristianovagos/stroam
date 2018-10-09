# STROAM Frontend

This is the Authentication Proxy Server of the STROAM system. It was build to protect the STROAM services.
Built with Springboot Security, using MySQL database.

## What is already done
This section will be updated as soon as development goes on.
* Springboot - Security Authorization and Resource Configuration Adapters setted  
* JWT Token Store and (dev) Asymmetric Keys added
* Add authorization between trusted server 
* Added in memory database support

## TODO
This section will (_also_) be updated as soon as development goes on.
* Define API, Swagger
* Add loggin UI ? 
* Upgrade Account services
* Messages exchange workflow diagrams

### How to run (locally)

* Install Java 1.8

* Install Maven or use the wrapper "mvnw" file

* Install Java libraries
```
$ mvn install
```

* Start Project
```
$ mvn spting-boot:run
```

* Build

* Publish

* Go to http://localhost:65069 or http://127.0.0.1:65069

### Author
* **André Cardoso** - [GitHub](https://github.com/aCard0s0)