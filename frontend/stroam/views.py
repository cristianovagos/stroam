from random import shuffle
from django.shortcuts import render, redirect, reverse
from django.http import HttpRequest, HttpResponse, Http404
from channels.layers import get_channel_layer
from asgiref.sync import async_to_sync

from .catalog import catalog
from .payment import payment
from .models import *
from .utils import *

SPACING = ' '
WEBSITE_TITLE = 'STROAM'
WEBSITE_SEPARATOR = '|'

MAIN_TITLE = WEBSITE_TITLE + SPACING + WEBSITE_SEPARATOR + SPACING

def home(request):
    title = 'Stream strong, anytime and anywhere.'
    numCartProducts = 0
    if 'productList' in request.session:
        numCartProducts = len(request.session.get('productList'))
    movies = catalog.getAllCatalog()
    if movies:
        shuffle(movies)
    tparams = {
        'title': MAIN_TITLE + title,
        'movies': movies,
        'numCart': numCartProducts
    }
    return render(request, 'pages/index.html', tparams)

def homeMovies(request):
    title = 'Our movies'
    numCartProducts = 0
    if 'productList' in request.session:
        numCartProducts = len(request.session.get('productList'))
    movies = [x for x in catalog.getAllCatalog() if x.type == 'movie']
    if movies:
        shuffle(movies)
    tparams = {
        'title': MAIN_TITLE + title,
        'movies': movies,
        'numCart': numCartProducts
    }
    return render(request, 'pages/index.html', tparams)

def homeSeries(request):
    title = 'Our TV Series'
    numCartProducts = 0
    if 'productList' in request.session:
        numCartProducts = len(request.session.get('productList'))
    movies = [x for x in catalog.getAllCatalog() if x.type == 'series']
    if movies:
        shuffle(movies)
    tparams = {
        'title': MAIN_TITLE + title,
        'movies': movies,
        'numCart': numCartProducts
    }
    return render(request, 'pages/index.html', tparams)

def singleMovie(request, id):
    assert isinstance(id, int)
    numCartProducts = 0
    if 'productList' in request.session:
        numCartProducts = len(request.session.get('productList'))

    p = Purchase_Production.objects.filter(purchase_id__user_id=1, purchase_id__purchase_production__production_id=id,
                                           purchase_id__payment_status=Purchase.PAYMENT_COMPLETED)
    moviePurchased = p.count() > 0

    movie = catalog.getSingleCatalog(id)
    if movie is not None:
        movieTitle = movie.title

    if request.method == 'POST':
        auxDict = {}
        if 'productList' in request.session:
            auxDict = request.session.get('productList')

        if request.POST.get('seasonID', False) and request.POST.get('seasonNum', False):
            auxDict[int(request.POST['productID'])] = {
                'season': int(request.POST['seasonNum']),
                'seasonID': int(request.POST['seasonID'])}
        else:
            auxDict[int(request.POST['productID'])] = {'season': None}
        request.session['productList'] = auxDict

        tparams = {
            'title': MAIN_TITLE + movieTitle,
            'movie': movie,
            'addedToCart': True,
            'numCart': numCartProducts,
            'purchased': moviePurchased
        }
        return render(request, 'base/single-movie.html', tparams)

    tparams = {
        'title': MAIN_TITLE + movieTitle,
        'movie': movie,
        'addedToCart': False,
        'numCart': numCartProducts,
        'purchased': moviePurchased
    }
    return render(request, 'base/single-movie.html', tparams)

def shoppingCart(request):
    title = 'Your shopping cart'
    numCartProducts = 0

    if request.method == 'POST':
        if 'productList' in request.session:
            auxDict = request.session.get('productList')
            auxDict.pop(request.POST['productID'], None)
            request.session['productList'] = auxDict
            return HttpResponse('')

    products = {}
    price = 0
    if 'productList' in request.session:
        numCartProducts = len(request.session.get('productList'))
        prodList = request.session.get('productList')
        for product in prodList:
            p = catalog.getSingleCatalog(int(product))
            if p:
                price += p.price
                products[p.id] = {}
                products[p.id]['product'] = p
                if 'season' in prodList[product] and prodList[product]['season'] is not None:
                    products[p.id]['season'] = prodList[product]['season']
                if 'seasonID' in prodList[product] and prodList[product]['seasonID'] is not None:
                    products[p.id]['seasonID'] = prodList[product]['seasonID']
            else:
                request.session['productList'] = {}
                numCartProducts = 0

    tparams = {
        'title': MAIN_TITLE + title,
        'products': products,
        'totalPrice': price,
        'numCart': numCartProducts
    }
    return render(request, 'pages/shopping-cart.html', tparams)

def checkoutCreate(request):
    checkURLOrigin(request, request.build_absolute_uri(reverse('shopping-cart')))

    products = {}
    price = 0
    if 'productList' in request.session:
        numCartProducts = len(request.session.get('productList'))
        prodList = request.session.get('productList')
        for product in prodList:
            p = catalog.getSingleCatalog(int(product))
            if p:
                price += p.price
                products[p.id] = {}
                products[p.id]['product'] = p
                if 'season' in prodList[product] and prodList[product]['season'] is not None:
                    products[p.id]['season'] = prodList[product]['season']
                if 'seasonID' in prodList[product] and prodList[product]['seasonID'] is not None:
                    products[p.id]['seasonID'] = prodList[product]['seasonID']
            else:
                request.session['productList'] = {}

    print(products)
    print(price)
    print(numCartProducts)
    return payment.createCheckout(list(prodList.keys()), price, request.build_absolute_uri(reverse('checkout')), request.build_absolute_uri(reverse('paymentError')))

def checkout(request):
    title = 'Checkout - Confirm Payment'
    checkURLOrigin(request, payment.PAYMENT_SERVICE_URL)

    if request.method == 'POST':
        checkoutToken = request.POST.get('checkoutToken', None)
        buyerID = request.POST.get('buyerID', None)
        if checkoutToken is not None and buyerID is not None:
            print('Executing checkout...')
            print(payment.executeCheckout(checkoutToken, buyerID))

            p = Purchase.objects.all().filter(token_payment=checkoutToken).first()
            if payment.executeCheckout(checkoutToken, buyerID):
                print('Redirecting to Payment Completed page')
                p.onCompletedPayment()
                return redirect('paymentCompleted')
            print('Redirecting to Payment Error page')
            p.onPaymentError()
            return redirect('paymentError')

    checkoutToken = request.GET.get('checkout_token', None)
    if checkoutToken is not None:
        p = Purchase.objects.all().filter(token_payment=checkoutToken).first()
        p.awaitPayment()

        data = payment.getCheckoutDetails(checkoutToken)
        buyerID = data.get('ID', None)

    tparams = {
        'title': MAIN_TITLE + title,
        'checkoutToken': checkoutToken,
        'buyerID': buyerID
    }
    return render(request, 'pages/checkout.html', tparams)

def paymentCompleted(request):
    title = 'Payment Completed'
    checkURLOrigin(request, payment.PAYMENT_SERVICE_URL)
    deleteProductListFromSession(request)

    tparams = {
        'title': MAIN_TITLE + title,
    }
    return render(request, 'pages/payment-completed.html', tparams)

def paymentError(request):
    title = 'Payment Canceled'
    checkURLOrigin(request, payment.PAYMENT_SERVICE_URL)
    deleteProductListFromSession(request)

    print("checkoutError")
    print(request.body)

    tparams = {
        'title': MAIN_TITLE + title,
    }
    return render(request, 'pages/payment-error.html', tparams)

# FOR DEBUGGING PURPOSES! THIS WILL DELETE ALL STUFF (SESSIONS + FRONTEND DATABASE DATA)
def deleteAll(request):
    title = 'All data deleted'
    request.session.flush()
    Purchase.objects.all().delete()
    Purchase_Production.objects.all().delete()

    tparams = {
        'title': MAIN_TITLE + title,
    }
    return render(request, 'pages/delete-all.html', tparams)
