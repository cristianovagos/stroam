import json
import requests
from django.http import Http404
from django.shortcuts import redirect
from urllib.request import urlopen

from ..models import *

PAYMENT_SERVICE_URL = "http://localhost:5000"

def createCheckout(productList, price, returnURL, cancelURL, items, currency="EUR", merchant="tokensample123"):
    jsonItems = []
    auxSeasons = {}
    for item in items:
        jsonItems.append({
            "NAME": item['name'],
            "PRICE": item['price'],
            "QUANTITY": item['quantity'],
            "URL": item['url']
        })
        auxSeasons[int(item['id'])] = item.get('season', None)
    jsonObj = {
      "AMOUNT": price,
      "RETURN_URL": returnURL,
      "CANCEL_URL": cancelURL,
      "MERCHANT": merchant,
      "CURRENCY": currency,
      "ITEMS": jsonItems,
    }

    headers = {"content-type": "application/json"}
    r = requests.post(PAYMENT_SERVICE_URL + "/CreateCheckout", data=json.dumps(jsonObj), headers=headers)
    try:
        responseObj = json.loads(r.text)
    except Exception:
        responseObj = None

    if responseObj and "CHECKOUT_TOKEN" in responseObj:
        checkoutToken = responseObj["CHECKOUT_TOKEN"]
        p = Purchase(user_id=1, token_payment=checkoutToken)
        p.save()
        for product in productList:
            purchaseInfo = Purchase_Production(production_id=product)
            purchaseInfo.save()
            purchaseInfo.season_num = auxSeasons.get(int(product))
            purchaseInfo.purchase_id.add(p)
            purchaseInfo.save()
        return redirect(PAYMENT_SERVICE_URL + "/pay?checkout_token=" + checkoutToken)
    else:
        raise Http404("Bad Request:\n\n" + str(responseObj))

def getCheckoutDetails(checkoutToken):
    try:
        url = urlopen(PAYMENT_SERVICE_URL + "/GetCheckoutDetails?checkout_token=" + checkoutToken)
    except Exception as e:
        url = None
        print(e)
    return json.loads(url.read().decode())

def executeCheckout(checkoutToken, buyerID):
    try:
        url = urlopen(PAYMENT_SERVICE_URL + "/ExecuteCheckout?checkout_token=" + checkoutToken + "&buyer_id=" + buyerID)
    except Exception as e:
        url = None
        print(e)
    data = json.loads(url.read().decode())
    return data["SUCCESS"]