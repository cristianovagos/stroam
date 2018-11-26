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
Built with Java

* **Streaming CDN**  
A content delivery network which serves media sources based on user location.
Built with Node.js and WebRTC API


### Server Ports
* **Frontend** - 8000
* **Catalog** - 4000
* **Auth** - 3000
* **Payment** - 5000
* **Notification** - 1884
* **Streaming CDN** - 1935 


### Authors
* **Cristiano Vagos** ([GitHub](https://github.com/cristianovagos) / [cristianovagos@ua.pt](mailto:cristianovagos@ua.pt))  
_Frontend and Catalog_

* **António Silva** ([GitHub](https://github.com/asergios) / [asergio@ua.pt](mailto:asergio@ua.pt))  
_Payment_

* **André Cardoso** ([GitHub](https://github.com/aCard0s0) / [marquescardoso@ua.pt](mailto:marquescardoso@ua.pt))   
_Auth_

* **João Amaral** ([GitHub](https://github.com/joaoamaral28) / [joaop.amaral@ua.pt](mailto:joaop.amaral@ua.pt))  
_Streaming CDN_

* **João Verdasca** ([GitHub](https://github.com/jfrverdasca) / [jfrverdasca@ua.pt](mailto:jfrverdasca@ua.pt))  
_Notification_




## How to run STROAM

There is a docker-compose file available with all services and containers for STROAM deployment, 
as well as a Ansible playbook file available for quick remote installation of the deployment environment.

#### Requirements
* **_Ansible_** must be installed on YOUR machine, and Python must be installed on the REMOTE machine (later described as host)
* In our case, the machine where the deployment is done is on a Ubuntu Server 18.04
* It may be required UA/IT VPN access into the deployment host

The Ansible playbook will install all software and packages needed for the Dockerized environment, such as Docker and Docker Compose
as well as pull the code from our repository (code.ua.pt), build and run the containers described in the Docker Compose file.

#### Usage
On **YOUR** machine, copy and paste the hosts file on Ansible config folder ( _/etc/ansible/_ ):
```
$ sudo cp hosts /etc/ansible/hosts
```

(_**OR**, if you already have Ansible hosts installed, just append the contents from this hosts file into your Ansible hosts_):
```
$ sudo cat hosts >> /etc/ansible/hosts
```

Then run the Ansible playbook, requesting password prompts:
```
$ ansible-playbook vm-playbook.yml --ask-pass --ask-become-pass
```
The user password and sudo password of the REMOTE machine will be prompted, as well as YOUR user and password from git repository.

Enjoy STROAM!