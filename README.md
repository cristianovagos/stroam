![alt text](http://code.ua.pt/projects/es1819-stroam/repository/revisions/master/raw/logo.png)

STROAM is a media streaming platform built using Service Oriented Architecture, with multiple services.


## Services
* **Frontend service**  
Web-app responsible for orchestrating all services, and the main app gateway.
Built with Django (Python).

* **Catalog**  
Service which catalogs all Movies / TV Series available.
Built with Spring Boot framework (Java).

* **Auth**  
Authentication service which register and authenticate users into the app.
Built with Spring Boot framework (Java).

* **Payment**  
Payment gateway service which takes care of billing.
Built with Flask (Python).

* **Notification**  
Service responsible for notifying users upon multiple usage in app.  

* **Streaming CDN**  
A content delivery network which serves media sources based on user location.


### Server Ports
* **Frontend** - 8000
* **Catalog** - 4000
* **Auth** - 3000
* **Payment** - 5000
* **Notification** - (_to be known_)
* **Streaming CDN** - (_to be known_)


### Authors
* **Cristiano Vagos** ([GitHub](https://github.com/cristianovagos) / [cristianovagos@ua.pt](mailto:cristianovagos@ua.pt))  
_Frontend and Catalog_

* **António Silva** ([GitHub](https://github.com/asergios) / [asergio@ua.pt](mailto:asergio@ua.pt))  
_Payment_

* **André Cardoso** ([GitHub](https://github.com/aCard0s0) / [marquescardoso@ua.pt](mailto:marquescardoso@ua.pt))   
_Auth_

* **João Amaral** ([GitHub](https://github.com/joaoamaral28) / [joaop.amaral@ua.pt](mailto:joaop.amaral@ua.pt))  
_Streaming CDN_

* **João Verdasca**  
_Notification_