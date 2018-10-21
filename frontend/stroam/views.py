from random import shuffle
from django.shortcuts import render, redirect, reverse
from django.http import HttpRequest, HttpResponse, Http404

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
    seasonsPurchased = []
    if movie is not None:
        movieTitle = movie.title

        if movie.type == 'series' and moviePurchased:
            seasonsPurchased = list(p.all().values_list('season_num', flat=True).distinct())

    if request.method == 'POST':
        auxDict = {}
        if 'productList' in request.session:
            auxDict = request.session.get('productList')

        if request.POST.get('seasonID', False) and request.POST.get('seasonNum', False):
            if request.POST['productID'] in auxDict:
                auxDict[request.POST['productID']].append({
                    'season': int(request.POST['seasonNum']),
                    'seasonID': int(request.POST['seasonID'])})
            else:
                auxDict[int(request.POST['productID'])] = []
                auxDict[int(request.POST['productID'])].append({
                    'season': int(request.POST['seasonNum']),
                    'seasonID': int(request.POST['seasonID'])})
        else:
            auxDict[int(request.POST['productID'])] = []
            auxDict[int(request.POST['productID'])].append({'season': None})
        request.session['productList'] = auxDict
        numCartProducts += 1

        tparams = {
            'title': MAIN_TITLE + movieTitle,
            'movie': movie,
            'addedToCart': True,
            'numCart': numCartProducts,
            'purchased': moviePurchased,
            'seasonsPurchased': seasonsPurchased
        }
        return render(request, 'base/single-movie.html', tparams)

    tparams = {
        'title': MAIN_TITLE + movieTitle,
        'movie': movie,
        'addedToCart': False,
        'numCart': numCartProducts,
        'purchased': moviePurchased,
        'seasonsPurchased': seasonsPurchased
    }
    return render(request, 'base/single-movie.html', tparams)

def shoppingCart(request):
    title = 'Your shopping cart'
    numCartProducts = 0

    if request.method == 'POST':
        if 'productList' in request.session:
            auxDict = request.session.get('productList')
            if request.POST.get('seasonID', False):
                prod = auxDict.get(request.POST['productID'], None)
                auxList = [p for p in prod if p['seasonID'] != int(request.POST['seasonID'])]
                if len(auxList) > 0:
                    auxDict[request.POST['productID']] = auxList
                    request.session['productList'] = auxDict
                    return HttpResponse('')
            auxDict.pop(request.POST['productID'], None)
            request.session['productList'] = auxDict
            return HttpResponse('')

    products = {}
    price = 0
    if 'productList' in request.session:
        prodList = request.session.get('productList')
        for product in prodList:
            p = catalog.getSingleCatalog(int(product))
            if p:
                if p.type == 'movie':
                    numCartProducts += 1
                    price += p.price
                products[p.id] = {}
                auxObj = {}
                auxObj['product'] = p
                auxObj['seasons'] = []
                for obj in prodList[product]:
                    auxObj['seasons'].append(obj)
                    if obj['season'] is not None:
                        price += p.price
                        numCartProducts += 1
                products[p.id] = auxObj
            else:
                deleteProductListFromSession(request)
                numCartProducts = 0

    tparams = {
        'title': MAIN_TITLE + title,
        'products': products,
        'totalPrice': price,
        'numCart': numCartProducts
    }
    return render(request, 'pages/shopping-cart.html', tparams)

def checkoutCreate(request):
    # checkURLOrigin(request, request.build_absolute_uri(reverse('shopping-cart')))

    products = []
    price = 0
    if 'productList' in request.session:
        prodList = request.session.get('productList')
        for product in prodList:
            p = catalog.getSingleCatalog(int(product))
            if p:
                season = ""
                seasonAnchor = ""
                for obj in prodList[product]:
                    if 'season' in obj and obj['season'] is not None:
                        season = " - Season " + str(obj['season'])
                        seasonAnchor = "#season" + str(obj['season'])
                    products.append({
                        "name": p.title + season,
                        "price": p.price,
                        "quantity": 1,
                        "url": request.build_absolute_uri(reverse('movie-single', kwargs={'id':p.id})) + seasonAnchor,
                        "season": obj.get('season', None),
                        "id": p.id
                    })
                    price += p.price
            deleteProductListFromSession(request)
    return payment.createCheckout(price, request.build_absolute_uri(reverse('checkout')),
                                  request.build_absolute_uri(reverse('paymentError')), products)

def checkout(request):
    title = 'Checkout - Confirm Payment'
    # checkURLOrigin(request, payment.PAYMENT_SERVICE_URL)

    if request.method == 'POST':
        checkoutToken = request.POST.get('checkoutToken', None)
        buyerID = request.POST.get('buyerID', None)
        if checkoutToken is not None and buyerID is not None:
            p = Purchase.objects.all().filter(token_payment=checkoutToken).first()
            if payment.executeCheckout(checkoutToken, buyerID):
                p.onCompletedPayment()
                return redirect('paymentCompleted')
            p.onPaymentError()
            return redirect('paymentError')

    checkoutToken = request.GET.get('checkout_token', None)
    if checkoutToken is not None:
        data = payment.getCheckoutDetails(checkoutToken)
        error = data.get('ERROR', None)

        if error is None:
            totalPrice = data.get('CHECKOUT', 0)['AMOUNT']
            products = data.get('ITEMS', [])
            buyerID = data.get('BUYER', {})
        else:
            raise Http404('Bad Request: \n\n' + str(error))

    tparams = {
        'title': MAIN_TITLE + title,
        'checkoutToken': checkoutToken,
        'buyerID': buyerID,
        'products': products,
        'totalPrice': totalPrice
    }
    return render(request, 'pages/checkout.html', tparams)

def paymentCompleted(request):
    title = 'Payment Completed'
    # checkURLOrigin(request, payment.PAYMENT_SERVICE_URL)
    deleteProductListFromSession(request)

    tparams = {
        'title': MAIN_TITLE + title,
    }
    return render(request, 'pages/payment-completed.html', tparams)

def paymentError(request):
    title = 'Payment Canceled'
    # checkURLOrigin(request, payment.PAYMENT_SERVICE_URL)
    deleteProductListFromSession(request)

    print("checkoutError")
    print(request.body)

    tparams = {
        'title': MAIN_TITLE + title,
    }
    return render(request, 'pages/payment-error.html', tparams)

def userPanel(request):
    title = 'User Panel'
    numCartProducts = 0

    if 'productList' in request.session:
        numCartProducts = len(request.session.get('productList'))

    if request.method == 'POST':
        p = Purchase.objects.get(id=request.POST['orderID'])
        p.onOrderCancelled()
        return HttpResponse('')

    data = {}
    allPurchases = Purchase.objects.filter(user_id=1)
    for purchase in allPurchases:
        productions = Purchase_Production.objects.filter(purchase_id=purchase.id)
        data[purchase.id] = {}
        data[purchase.id]['payment_status'] = purchase.payment_status
        data[purchase.id]['token_payment'] = purchase.token_payment
        data[purchase.id]['date_created'] = purchase.date_created
        data[purchase.id]['date_payment'] = purchase.date_payment
        data[purchase.id]['products'] = {}
        for production in productions:
            product = catalog.getSingleCatalog(production.production_id)
            data[purchase.id]['products'][production.id] = {}
            data[purchase.id]['products'][production.id]['production'] = product
            if product.type == 'series':
                data[purchase.id]['products'][production.id]['season'] = production.season_num
            else:
                data[purchase.id]['products'][production.id]['season'] = None

    tparams = {
        'title': MAIN_TITLE + title,
        'data': data,
        'numCart': numCartProducts
    }
    return render(request, 'pages/user-panel.html', tparams)

def pay(request, checkout_token):
    return redirect(payment.PAYMENT_SERVICE_URL + "/pay?checkout_token=" + checkout_token)

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
