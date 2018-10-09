![alt text](http://code.ua.pt/projects/es1819-stroam/repository/revisions/master/raw/payment/payment/static/images/logo.png)

### About
StrongPay is a payment gateway service based on PayPal. It's composed of a front-end interface and an API running on a flask framework.  


### API Endpoints

| Method        | Call           | Header |Description  | Parameters |
| ------------- | :------------: | :----------: | :----------: | :--------- |
| POST    | /CreateCheckout | **content-type:** <br> application/x-www-form-urlencoded | Creates Checkout <br> for later to be paid by a client | **AMOUNT** - Amount to be paid. <br> **MERCHANT** - Token that identifies the merchant. <br> **RETURN_URL** - URL to where the client is redirected to after payment. <br> **CANCEL_URL** - URL to where the client is redirected to in case the payment in cancelled.
