import json
import requests
from django.shortcuts import redirect
from urllib.request import urlopen

from ..models import *

PAYMENT_SERVICE_URL = "http://localhost:5000"

def createCheckout(productList, price, returnURL, cancelURL, merchant="tokensample123"):
    headers = {"content-type": "application/x-www-form-urlencoded"}
    r = requests.post(PAYMENT_SERVICE_URL + "/CreateCheckout",
                  data={
                      "AMOUNT": price,
                      "RETURN_URL": returnURL,
                      "CANCEL_URL": cancelURL,
                      "MERCHANT": merchant
                  }, headers=headers)
    responseObj = json.loads(r.text)

    if "CHECKOUT_TOKEN" in responseObj:
        checkoutToken = responseObj["CHECKOUT_TOKEN"]
        p = Purchase(user_id=1, token_payment=checkoutToken)
        p.save()
        for product in productList:
            purchaseInfo = Purchase_Production(production_id=product)
            purchaseInfo.save()
            purchaseInfo.purchase_id.add(p)
            purchaseInfo.save()
        return redirect(PAYMENT_SERVICE_URL + "/pay?checkout_token=" + checkoutToken)
    else:
        print(responseObj)

def getCheckoutDetails(checkoutToken):
    try:
        url = urlopen(PAYMENT_SERVICE_URL + "/GetCheckoutDetails?checkout_token=" + checkoutToken)
    except Exception as e:
        print(e)
    return json.loads(url.read().decode())

def executeCheckout(checkoutToken, buyerID):
    try:
        url = urlopen(PAYMENT_SERVICE_URL + "/ExecuteCheckout?checkout_token=" + checkoutToken + "&buyer_id=" + buyerID)
    except Exception as e:
        print(e)
    data = json.loads(url.read().decode())
    return data["SUCCESS"]