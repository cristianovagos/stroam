![alt text](http://code.ua.pt/projects/es1819-stroam/repository/revisions/master/raw/payment/static/images/logo.png)

## About
StrongPay is a payment gateway service based on PayPal. It's composed of a front-end interface and an API running on a flask framework.  


## API Endpoints

> **URL** : '/api/v1/Checkout'
**Method** : `POST`
**Description** : Create a checkout for later to be paid by an user.

> **URL** : '/api/v1/Checkout'
**Method** : `GET`
**Description** : Gets checkout details such as who paid and billing address.

> **URL** : '/api/v1/Checkout'
**Method** : `PUT`
**Description** : Replaces all information of checkout.

> **URL** : '/api/v1/Checkout'
**Method** : `DELETE`
**Description** : Deletes checkout.

> **URL** : '/api/v1/ExecuteCheckout'
**Method** : `GET`
**Description** : Confirm and execute payment. Return Success or Failure.

## What is done

- Index page with login form
- API Call to Create Checkout
- API Call with Checkout information
- API Call to Update/Delete Checkout
- Redirect link for payment to be done
- API Call to execute checkout
- Basic credit card Validation
- Private API Calls for user management
- Ability to store used Credit Card and Billing Address
- Profile Management for the Client and Merchant
- Register for Client and Merchant

## What is left

- Improve JavaScript validation of payment form
- Minor security features

## Deployment

* Start by installing the project requirements:
```
pip install -r requirements.txt
```

* Setting up Database:
```
sqlite3 StrongPay_database.db
SQLite version 3.25.0 2018-09-15 04:01:47
Enter ".help" for usage hints.
sqlite> .read StrongPay_schema.sql
```

* Run the app
```
python app.py
```

## How it works

![alt text](http://code.ua.pt/projects/es1819-stroam/repository/revisions/fb91fa2acbf7cd7709990e52f526e0c8e535fd2c/raw/payment/static/images/strongpay_payment.png)

# **API documentation is available at /docs**
