![alt text](http://code.ua.pt/projects/es1819-stroam/repository/revisions/master/raw/payment/payment/static/images/logo.png)

### About
StrongPay is a payment gateway service based on PayPal. It's composed of a front-end interface and an API running on a flask framework.  


### API Endpoints

**URL** : `/CreateCheckout`
**Method** : `POST`
**Description** : Create a checkout for later to be paid by an user.

### What is done

- Index page with login form
- /CreateCheckout API call

### TODO

- Login/Register for Client and Merchant
- Redirect link for payment to be done
- Profile Management for the Client and Merchant
- API call for Checkout information
- Bonus features like credit card validation

### Deployment

* Start by installing the project requirements:
```shell
pip install -r requirements.txt
```

* Setting up Database:
```shell
sqlite3 StrongPay_database.db
SQLite version 3.25.0 2018-09-15 04:01:47
Enter ".help" for usage hints.
sqlite> .read StrongPay_schema.sql
```

* Run the app
```shell
python app.py
```
