# STROAM Frontend

This is the frontend of the STROAM system. It was build to consume the STROAM services.
Built with Django, using a PostgreSQL database.

## What is already done
This section will be updated as soon as development goes on.
* Index page with all movies and series from Catalog
* Movies and series page (basic sorting) from Catalog
* Single movie/series page with info from Catalog (seasons info, episodes, etc)
* Shopping Cart and Checkout pages and logic (add and remove products, user prompts, etc)
* Added Django Channels support to go async
* Added PostgreSQL database support, to store purchases information, etc

## TODO
This section will (_also_) be updated as soon as development goes on.
* Add async notification support (_needs Notification service_)
* Add play feature (_needs user database + Streaming CDN service_) to play movies
* _More_ error handling (_to be found during tests_)
* Migrate into Docker containers (in the end...)

### How to run (locally)

* Install Python3.7

* Install PostgreSQL

* Install Python libraries
```
$ pip install -r requirements.txt
```

* Start PostgreSQL service
```
$ systemctl start postgresql.service
```

* Make Django migrations into database
```
$ python manage.py makemigrations
$ python manage.py migrate
```

* Run Catalog service (check its README)

* Start Django server
```
$ python manage.py runserver 0.0.0.0:8000
```

* Go to http://localhost:8000 or http://127.0.0.1:8000

### Author
* **Cristiano Vagos** - [GitHub](https://github.com/cristianovagos)