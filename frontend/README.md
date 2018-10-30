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
* Added PostgreSQL database support
* Models created (_Purchase_ - purchases information and _PurchaseInformation_ - N-to-M connection table into Catalog IDs) for database usage
* Payment service (StroamPay) integrated (_view payment folder for more info_)
* Streaming CDN service integrated (_view cdn folder for more info_)
* Added basic GET+POST protection of hardcoded URLs (p.e. accessing certain pages out of cycle)
* Added a user panel, on which one can see their orders (history, etc), and let resume payments and cancel orders
* Added a personal library page, which one can see straight away the purchased productions

## TODO
This section will (_also_) be updated as soon as development goes on.
* Add user authentication (Sign-in/Register, store user data, etc) (_needs Auth service_)
* Add notification support (_needs Notification service_)
* _More_ error handling (_to be found during tests_)
* Migrate into Docker containers for ease of deployment

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
* Run Payment service (check its README)

* Start Django server
```
$ python manage.py runserver 0.0.0.0:8000
```

* Go to http://localhost:8000 or http://127.0.0.1:8000

### Author
* **Cristiano Vagos** - [GitHub](https://github.com/cristianovagos)