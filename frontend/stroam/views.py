import logging
import base64
from random import shuffle
from django.shortcuts import render, redirect, reverse
from django.http import HttpRequest, HttpResponse, Http404

from .catalog import production, season, genre
from .payment import payment
from .notifications import notifications
from .models import *
from .utils import *

SPACING = ' '
WEBSITE_TITLE = 'STROAM'
WEBSITE_SEPARATOR = '|'
MAIN_TITLE = WEBSITE_TITLE + SPACING + WEBSITE_SEPARATOR + SPACING

LOGGER = logging.getLogger(__name__)

def home(request):
    title = 'Stream strong, anytime and anywhere.'
    movies = production.getAllProduction()
    if movies:
        shuffle(movies)

    print(request.GET)
    # if request.method == 'POST':
    #     print(request.POST)

    tparams = {
        'title': MAIN_TITLE + title,
        'movies': movies,
        'numCart': request.session.get('cartNumber', 0)
    }
    return render(request, 'pages/index.html', tparams)

def homeMovies(request):
    title = 'Our movies'
    try:
        movies = [x for x in production.getAllProduction() if x.type == 'movie']
        shuffle(movies)
    except Exception:
        movies = None
    tparams = {
        'title': MAIN_TITLE + title,
        'movies': movies,
        'numCart': request.session.get('cartNumber', 0)
    }
    return render(request, 'pages/index.html', tparams)

def homeSeries(request):
    title = 'Our TV Series'
    try:
        movies = [x for x in production.getAllProduction() if x.type == 'series']
        shuffle(movies)
    except Exception:
        movies = None
    tparams = {
        'title': MAIN_TITLE + title,
        'movies': movies,
        'numCart': request.session.get('cartNumber', 0)
    }
    return render(request, 'pages/index.html', tparams)

def genreList(request):
    title = 'Our Genres'
    genres = genre.getAllGenres()
    if genres:
        shuffle(genres)
    tparams = {
        'title': MAIN_TITLE + title,
        'genres': genres,
        'numCart': request.session.get('cartNumber', 0)
    }
    return render(request, 'pages/genre-list.html', tparams)

def genreMovies(request, genre):
    title = genreName = genre
    movies = None
    requested_genre = production.getGenreByName(genre)
    if requested_genre is not None:
        title = requested_genre.name
        movies = production.getProductionsByGenreID(requested_genre.id)
        if movies:
            shuffle(movies)

    tparams = {
        'title': MAIN_TITLE + title,
        'movies': movies,
        'genreName': genreName,
        'numCart': request.session.get('cartNumber', 0),
        'subscribed': notifications.is_user_subscribed(user_id=1, channel_name=('stroam-' + genreName))
    }
    return render(request, 'pages/genre.html', tparams)

def singleMovie(request, id):
    assert isinstance(id, int)

    p = Purchase_Production.objects.filter(purchase_id__user_id=1, purchase_id__purchase_production__production_id=id,
                                           purchase_id__payment_status=Purchase.PAYMENT_COMPLETED)
    moviePurchased = p.count() > 0

    movie = production.getSingleProduction(id)
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
        if 'cartNumber' in request.session:
            request.session['cartNumber'] += 1
        else:
            request.session['cartNumber'] = 1

        tparams = {
            'title': MAIN_TITLE + movieTitle,
            'movie': movie,
            'addedToCart': True,
            'numCart': request.session.get('cartNumber', 0),
            'purchased': moviePurchased,
            'seasonsPurchased': seasonsPurchased,
            'subscribed': notifications.is_user_subscribed(user_id=1, channel_name=('stroam-movie' + id))
        }
        return render(request, 'pages/single-movie.html', tparams)

    tparams = {
        'title': MAIN_TITLE + movieTitle,
        'movie': movie,
        'addedToCart': False,
        'numCart': request.session.get('cartNumber', 0),
        'purchased': moviePurchased,
        'seasonsPurchased': seasonsPurchased,
        'subscribed': notifications.is_user_subscribed(user_id=1, channel_name=('stroam-movie' + id))
    }
    return render(request, 'pages/single-movie.html', tparams)

def shoppingCart(request):
    title = 'Your shopping cart'

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
            if 'cartNumber' in request.session:
                request.session['cartNumber'] -= 1
            return HttpResponse('')

    products = {}
    price = 0
    if 'productList' in request.session:
        prodList = request.session.get('productList')
        for product in prodList:
            p = production.getSingleProduction(int(product))
            if p:
                if p.type == 'movie':
                    price += p.price
                products[p.id] = {}
                auxObj = {}
                auxObj['product'] = p
                auxObj['seasons'] = []
                for obj in prodList[product]:
                    auxObj['seasons'].append(obj)
                    if obj['season'] is not None:
                        price += p.price
                products[p.id] = auxObj
            else:
                deleteProductListFromSession(request)

    tparams = {
        'title': MAIN_TITLE + title,
        'products': products,
        'totalPrice': price,
        'numCart': request.session.get('cartNumber', 0)
    }
    return render(request, 'pages/shopping-cart.html', tparams)

def checkoutCreate(request):
    products = []
    price = 0
    if 'productList' in request.session:
        prodList = request.session.get('productList')
        for product in prodList:
            p = production.getSingleProduction(int(product))
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
                        "image": p.poster,
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
            billingData = data.get('BILLING_ADDRESS', {})
        else:
            LOGGER.error(str(error))
            raise Http404('Bad Request: \n\n' + str(error))

    tparams = {
        'title': MAIN_TITLE + title,
        'checkoutToken': checkoutToken,
        'buyerID': buyerID,
        'billingData': billingData,
        'products': products,
        'totalPrice': totalPrice,
        'cartShowing': False
    }
    return render(request, 'pages/checkout.html', tparams)

def paymentCompleted(request):
    title = 'Payment Completed'
    deleteProductListFromSession(request)

    tparams = {
        'title': MAIN_TITLE + title,
    }
    return render(request, 'pages/payment-completed.html', tparams)

def paymentError(request):
    title = 'Payment Canceled'
    deleteProductListFromSession(request)

    print("checkoutError")
    print(request.body)

    tparams = {
        'title': MAIN_TITLE + title,
    }
    return render(request, 'pages/payment-error.html', tparams)

def userPanel(request):
    title = 'User Panel'

    if request.method == 'POST':
        p = Purchase.objects.get(id=request.POST['orderID'])
        p.onOrderCancelled()
        payment.deleteCheckout(p.token_payment)
        return HttpResponse('')

    data = {}
    allPurchases = Purchase.objects.filter(user_id=1)
    for purchase in allPurchases:
        productions = Purchase_Production.objects.filter(purchase_id=purchase.id)
        data[purchase.id] = {}
        data[purchase.id]['payment_status'] = purchase.payment_status
        data[purchase.id]['token_isValid'] = purchase.token_isValid
        data[purchase.id]['token_payment'] = purchase.token_payment
        data[purchase.id]['date_created'] = purchase.date_created
        data[purchase.id]['date_payment'] = purchase.date_payment
        data[purchase.id]['products'] = {}
        for prod in productions:
            product = production.getSingleProduction(prod.production_id)
            data[purchase.id]['products'][prod.id] = {}
            data[purchase.id]['products'][prod.id]['production'] = product
            if product.type == 'series':
                data[purchase.id]['products'][prod.id]['season'] = prod.season_num
            else:
                data[purchase.id]['products'][prod.id]['season'] = None

    tparams = {
        'title': MAIN_TITLE + title,
        'data': data,
        'numCart': request.session.get('cartNumber', 0)
    }
    return render(request, 'pages/user-panel.html', tparams)

def myMovies(request):
    title = 'My Library'
    movies = []
    purchasedProds = Purchase_Production.objects.filter(purchase_id__user_id=1, purchase_id__payment_status=Purchase.PAYMENT_COMPLETED)
    for prod in purchasedProds:
        movies.append(production.getSingleProduction(prod.production_id))
    if movies:
        shuffle(list(set(movies)))

    tparams = {
        'title': MAIN_TITLE + title,
        'movies': movies,
        'numCart': request.session.get('cartNumber', 0)
    }
    return render(request, 'pages/my-movies.html', tparams)

def pay(request, checkout_token):
    return redirect(payment.PAYMENT_SERVICE_URL + "/pay?checkout_token=" + checkout_token)

def makeauth(request):
    # notifications.subscribe(user_id=1)
    homeUrl = request.build_absolute_uri(reverse('homepage')).encode("utf-8")
    # return redirect('http://authclient:3200?url=' + base64.b64encode(homeUrl))
    homeEncoded = base64.b64encode(homeUrl)
    return redirect('http://localhost:4200?url=' + str(homeEncoded).replace('b\'', '').replace('\'', ''))

# def subscribeGenre(request, genre_name):
#     if production.getGenreByName(genre_name) is None:
#         return
#     notifications.subscribe(user_id=1, channel_name=('stroam-' + genre_name))
#
# def unsubscribeGenre(request, genre_name):
#     notifications.unsubscribe(user_id=1, channel_name=('stroam-' + genre_name))
#
# def subscribeMovie(request, movie_id):
#     if production.getSingleProduction(movie_id) is None:
#         return
#     notifications.subscribe(user_id=1, channel_name=('stroam-movie' + movie_id))
#
# def unsubscribeMovie(request, movie_id):
#     notifications.unsubscribe(user_id=1, channel_name=('stroam-movie' + movie_id))

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
