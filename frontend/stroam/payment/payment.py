import logging
import json
import requests
from django.conf import settings
from django.http import Http404
from django.shortcuts import redirect
from urllib.request import urlopen

from ..models import Purchase, Purchase_Production

LOGGER = logging.getLogger(__name__)
PAYMENT_SERVICE_URL = "http://engserv-3-aulas.ws.atnog.av.it.pt/pay" if settings.USE_DOCKER else "http://localhost:5000"
PAYMENT_SERVICE_API_URL = "http://payment:5000/api/" if settings.USE_DOCKER else "http://localhost:5000/api/"

def createCheckout(user_id, price, returnURL, cancelURL, items, currency="EUR", merchant="tokensample123"):
    # TODO auth
    # if not user_id: return Http404("Bad Request:\n\n" + "User id not valid")
    user_id = 1

    jsonItems = []
    for item in items:
        jsonItems.append({
            "NAME": item['name'],
            "PRICE": item['price'],
            "QUANTITY": item['quantity'],
            "IMAGE": item['image'],
            "URL": item['url']
        })
    jsonObj = {
      "AMOUNT": price,
      "RETURN_URL": returnURL,
      "CANCEL_URL": cancelURL,
      "MERCHANT": merchant,
      "CURRENCY": currency,
      "ITEMS": jsonItems,
    }

    headers = {"content-type": "application/json"}
    r = requests.post(PAYMENT_SERVICE_API_URL + "v1/Checkout", data=json.dumps(jsonObj), headers=headers)
    try:
        responseObj = json.loads(r.text)
    except Exception:
        responseObj = None

    if responseObj and "CHECKOUT_TOKEN" in responseObj:
        checkoutToken = responseObj["CHECKOUT_TOKEN"]
        p = Purchase(user_id=user_id, token_payment=checkoutToken)
        p.save()
        for product in items:
            purchaseInfo = Purchase_Production(production_id=int(product['id']))
            purchaseInfo.save()
            if product['season'] is not None:
                purchaseInfo.season_num = int(product['season'])
            purchaseInfo.purchase_id.add(p)
        return redirect(PAYMENT_SERVICE_URL + "/pay?checkout_token=" + checkoutToken)
    else:
        LOGGER.error(str(responseObj))
        raise Http404("Bad Request:\n\n" + str(responseObj))

def getCheckoutDetails(checkoutToken):
    try:
        url = urlopen(PAYMENT_SERVICE_API_URL + "v1/Checkout?checkout_token=" + checkoutToken)
    except Exception as e:
        url = None
        LOGGER.error(e)
        print(e)
    return json.loads(url.read().decode())

def executeCheckout(checkoutToken, buyerID):
    try:
        url = urlopen(PAYMENT_SERVICE_API_URL + "v1/ExecuteCheckout?checkout_token=" + checkoutToken + "&buyer_id=" + buyerID)
    except Exception as e:
        url = None
        LOGGER.error(e)
        print(e)
    data = json.loads(url.read().decode())
    return data["SUCCESS"]

def deleteCheckout(checkoutToken):
    r = requests.delete(PAYMENT_SERVICE_API_URL + "v1/Checkout", params={'checkoutToken': checkoutToken})
    try:
        data = json.loads(r.text)
        return data.get("SUCCESS", False)
    except Exception:
        return False
