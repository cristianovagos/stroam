import json
import base64
import requests
from random import shuffle
from django.urls import resolve
from django.shortcuts import render, redirect, reverse
from django.http import HttpResponse
from django.views.decorators.csrf import csrf_exempt
from django.contrib.sessions.backends.db import SessionStore

from .catalog import production, genre
from .payment import payment
from .notifications import notifications
from .models import *
from .utils import *

SPACING = ' '
WEBSITE_TITLE = 'STROAM'
WEBSITE_SEPARATOR = '|'
MAIN_TITLE = WEBSITE_TITLE + SPACING + WEBSITE_SEPARATOR + SPACING

LOGGER = logging.getLogger(__name__)

@csrf_exempt
def home(request):
    title = 'Stream strong, anytime and anywhere.'
    movies = production.getAllProduction()
    if movies:
        shuffle(movies)

    if request.method == 'POST':
        body_decoded = request.body.decode('utf-8')
        body = json.loads(body_decoded)
        auth_code = body['code']
        session_id = body['session_id']
        if auth_code:
            req = requests.post('http://localhost:3000/api/v1/oauth/login/access_token', json={
                "client_id": "es-stroam-frontend",
                "client_secret": "super_secret001",
                "code": auth_code
            })
            json_res = json.loads(req.text)

            sess = SessionStore(session_key=session_id)
            sess['isAuthenticated'] = True
            sess['user_id'] = int(json_res['id'])
            sess['user_name'] = str(json_res['name'])
            sess['user_token'] = str(json_res['token'])
            sess.save()

    if request.session.get('user_id', False):
        subscribed_channels = [ch['channel_name'] for ch in
                              Notification_Channels.objects.filter(notification_subscription__user_id=request.session['user_id']).values(
                                  'channel_name')]
    else:
        subscribed_channels = None

    tparams = {
        'title': MAIN_TITLE + title,
        'movies': movies,
        'numCart': request.session.get('cartNumber', 0),
        'subscribed_channels': subscribed_channels,
        'isAuthenticated': request.session.get('isAuthenticated', False),
        'username': request.session.get('user_name', None),
        'thisUrl': resolve(request.path_info).url_name,
    }

    for key, value in request.session.items():
        print('{} => {}'.format(key, value))

    return render(request, 'pages/index.html', tparams)

def homeMovies(request):
    title = 'Our movies'
    try:
        movies = [x for x in production.getAllProduction() if x.type == 'movie']
        shuffle(movies)
    except Exception:
        movies = None

    if request.session.get('user_id', False):
        subscribed_channels = [ch['channel_name'] for ch in
                              Notification_Channels.objects.filter(notification_subscription__user_id=request.session['user_id']).values(
                                  'channel_name')]
    else:
        subscribed_channels = None

    tparams = {
        'title': MAIN_TITLE + title,
        'movies': movies,
        'numCart': request.session.get('cartNumber', 0),
        'subscribed_channels': subscribed_channels,
        'user': request.session.get('user', None),
        'thisUrl': resolve(request.path_info).url_name,
    }
    return render(request, 'pages/index.html', tparams)

def homeSeries(request):
    title = 'Our TV Series'
    try:
        movies = [x for x in production.getAllProduction() if x.type == 'series']
        shuffle(movies)
    except Exception:
        movies = None
    if request.session.get('user_id', False):
        subscribed_channels = [ch['channel_name'] for ch in
                              Notification_Channels.objects.filter(notification_subscription__user_id=request.session['user_id']).values(
                                  'channel_name')]
    else:
        subscribed_channels = None

    tparams = {
        'title': MAIN_TITLE + title,
        'movies': movies,
        'numCart': request.session.get('cartNumber', 0),
        'subscribed_channels': subscribed_channels,
        'user': request.session.get('user', None),
        'thisUrl': resolve(request.path_info).url_name,
    }
    return render(request, 'pages/index.html', tparams)

def genreList(request):
    title = 'Our Genres'
    genres = genre.getAllGenres()
    if genres:
        shuffle(genres)
    if request.session.get('user_id', False):
        subscribed_channels = [ch['channel_name'] for ch in
                              Notification_Channels.objects.filter(notification_subscription__user_id=request.session['user_id']).values(
                                  'channel_name')]
    else:
        subscribed_channels = None

    tparams = {
        'title': MAIN_TITLE + title,
        'genres': genres,
        'numCart': request.session.get('cartNumber', 0),
        'subscribed_channels': subscribed_channels,
        'user': request.session.get('user', None),
        'thisUrl': resolve(request.path_info).url_name,
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

    if request.session.get('user_id', False):
        subscribed_channels = [ch['channel_name'] for ch in
                              Notification_Channels.objects.filter(notification_subscription__user_id=request.session['user_id']).values(
                                  'channel_name')]
    else:
        subscribed_channels = None

    tparams = {
        'title': MAIN_TITLE + title,
        'movies': movies,
        'genreName': genreName,
        'numCart': request.session.get('cartNumber', 0),
        'subscribed': notifications.is_user_subscribed(user_id=1, channel_name=('stroam-' + genreName)),
        'subscribed_channels': subscribed_channels,
        'user': request.session.get('user', None),
        'thisUrl': resolve(request.path_info).url_name,
    }
    return render(request, 'pages/genre.html', tparams)

def singleMovie(request, id):
    assert isinstance(id, int)

    movie = production.getSingleProduction(id)
    movieTitle = movie.title
    seasonsPurchased = []

    if request.session.get('user_id', False):
        p = Purchase_Production.objects.filter(purchase_id__user_id=request.session['user_id'], purchase_id__purchase_production__production_id=id,
                                               purchase_id__payment_status=Purchase.PAYMENT_COMPLETED)
        moviePurchased = p.count() > 0

        subscribed_channels = [ch['channel_name'] for ch in
                               Notification_Channels.objects.filter(notification_subscription__user_id=request.session['user_id']).values(
                                   'channel_name')]

        subscribed = notifications.is_user_subscribed(user_id=request.session['user_id'], channel_name=('stroam-movie' + str(id)))

        if movie is not None:
            if movie.type == 'series' and moviePurchased:
                seasonsPurchased = list(p.all().values_list('season_num', flat=True).distinct())
    else:
        moviePurchased = False
        subscribed_channels = None
        subscribed = False

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
            'subscribed': subscribed,
            'subscribed_channels': subscribed_channels,
            'user': request.session.get('user', None),
            'thisUrl': resolve(request.path_info).url_name,
        }
        return render(request, 'pages/single-movie.html', tparams)

    tparams = {
        'title': MAIN_TITLE + movieTitle,
        'movie': movie,
        'addedToCart': False,
        'numCart': request.session.get('cartNumber', 0),
        'purchased': moviePurchased,
        'seasonsPurchased': seasonsPurchased,
        'subscribed': subscribed,
        'subscribed_channels': subscribed_channels,
        'user': request.session.get('user', None),
        'thisUrl': resolve(request.path_info).url_name,
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

    if request.session.get('user_id', False):
        subscribed_channels = [ch['channel_name'] for ch in
                              Notification_Channels.objects.filter(notification_subscription__user_id=request.session['user_id']).values(
                                  'channel_name')]
    else:
        subscribed_channels = None

    tparams = {
        'title': MAIN_TITLE + title,
        'products': products,
        'totalPrice': price,
        'numCart': request.session.get('cartNumber', 0),
        'subscribed_channels': subscribed_channels,
        'isAuthenticated': request.session.get('isAuthenticated', False),
        'user': request.session.get('user', None),
        'thisUrl': resolve(request.path_info).url_name,
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

    if request.session.get('user_id', False):
        subscribed_channels = [ch['channel_name'] for ch in
                              Notification_Channels.objects.filter(notification_subscription__user_id=request.session['user_id']).values(
                                  'channel_name')]
    else:
        subscribed_channels = None

    tparams = {
        'title': MAIN_TITLE + title,
        'checkoutToken': checkoutToken,
        'buyerID': buyerID,
        'billingData': billingData,
        'products': products,
        'totalPrice': totalPrice,
        'cartShowing': False,
        'subscribed_channels': subscribed_channels,
        'user': request.session.get('user', None),
        'thisUrl': resolve(request.path_info).url_name,
    }
    return render(request, 'pages/checkout.html', tparams)

def paymentCompleted(request):
    title = 'Payment Completed'
    deleteProductListFromSession(request)

    if request.session.get('user_id', False):
        subscribed_channels = [ch['channel_name'] for ch in
                              Notification_Channels.objects.filter(notification_subscription__user_id=request.session['user_id']).values(
                                  'channel_name')]
    else:
        subscribed_channels = None

    tparams = {
        'title': MAIN_TITLE + title,
        'subscribed_channels': subscribed_channels,
        'user': request.session.get('user', None),
        'thisUrl': resolve(request.path_info).url_name,
    }
    return render(request, 'pages/payment-completed.html', tparams)

def paymentError(request):
    title = 'Payment Canceled'
    deleteProductListFromSession(request)

    if request.session.get('user_id', False):
        subscribed_channels = [ch['channel_name'] for ch in
                              Notification_Channels.objects.filter(notification_subscription__user_id=request.session['user_id']).values(
                                  'channel_name')]
    else:
        subscribed_channels = None

    print("checkoutError")
    print(request.body)

    tparams = {
        'title': MAIN_TITLE + title,
        'subscribed_channels': subscribed_channels,
        'user': request.session.get('user', None),
        'thisUrl': resolve(request.path_info).url_name,
    }
    return render(request, 'pages/payment-error.html', tparams)

def userPanel(request):
    title = 'User Panel'

    if not request.session.get('isAuthenticated', False):
    #     TODO - create user not authenticated page
        return redirect('homepage')

    if request.method == 'POST':
        if request.POST.get('orderID', False):
            p = Purchase.objects.get(id=request.POST['orderID'])
            p.onOrderCancelled()
            payment.deleteCheckout(p.token_payment)
            return HttpResponse('')
        else:
            if request.session.get('user_id', False):
                notifications.unsubscribe(user_id=request.session['user_id'], channel_name=request.POST['channel_name'])
            return redirect('user-panel')

    purchaseData = {}
    allPurchases = Purchase.objects.filter(user_id=request.session['user_id'])
    for purchase in allPurchases:
        productions = Purchase_Production.objects.filter(purchase_id=purchase.id)
        purchaseData[purchase.id] = {}
        purchaseData[purchase.id]['payment_status'] = purchase.payment_status
        purchaseData[purchase.id]['token_isValid'] = purchase.token_isValid
        purchaseData[purchase.id]['token_payment'] = purchase.token_payment
        purchaseData[purchase.id]['date_created'] = purchase.date_created
        purchaseData[purchase.id]['date_payment'] = purchase.date_payment
        purchaseData[purchase.id]['products'] = {}
        for prod in productions:
            product = production.getSingleProduction(prod.production_id)
            purchaseData[purchase.id]['products'][prod.id] = {}
            purchaseData[purchase.id]['products'][prod.id]['production'] = product
            if product.type == 'series':
                purchaseData[purchase.id]['products'][prod.id]['season'] = prod.season_num
            else:
                purchaseData[purchase.id]['products'][prod.id]['season'] = None

    subscribed_channels = []
    subscriptionsData = {}
    allSubscriptions = Notification_Subscription.objects.filter(user_id=request.session['user_id'])
    for subscription in allSubscriptions:
        channels = Notification_Channels.objects.filter(notification_subscription=subscription.pk)
        for ch in channels:
            subscriptionsData[ch.id] = {}
            subscriptionsData[ch.id]['channel_name'] = ch.channel_name
            subscribed_channels.append(ch.channel_name)

            if ch.channel_name.strip('stroam-movie').isnumeric():
                pr = production.getSingleProduction(int(ch.channel_name.strip('stroam-movie')))
                subscriptionsData[ch.id]['production'] = pr
                subscriptionsData[ch.id]['type'] = str(pr.type).capitalize()
            else:
                subscriptionsData[ch.id]['type'] = 'Genre'

            subscriptionsData[ch.id]['strip'] = ch.channel_name.strip('stroam-movie')

    tparams = {
        'title': MAIN_TITLE + title,
        'purchaseData': purchaseData,
        'subscriptionsData': subscriptionsData,
        'subscribed_channels': subscribed_channels,
        'numCart': request.session.get('cartNumber', 0),
        'user': request.session.get('user', None),
        'thisUrl': resolve(request.path_info).url_name,
    }
    return render(request, 'pages/user-panel.html', tparams)

def myMovies(request):
    title = 'My Library'

    if not request.session.get('isAuthenticated', False):
    #     TODO - create user not authenticated page
        return redirect('homepage')

    movies = []
    purchasedProds = Purchase_Production.objects.filter(purchase_id__user_id=request.session['user_id'], purchase_id__payment_status=Purchase.PAYMENT_COMPLETED)
    for prod in purchasedProds:
        movies.append(production.getSingleProduction(prod.production_id))
    if movies:
        shuffle(list(set(movies)))

    if request.session.get('user_id', False):
        subscribed_channels = [ch['channel_name'] for ch in
                              Notification_Channels.objects.filter(notification_subscription__user_id=request.session['user_id']).values(
                                  'channel_name')]
    else:
        subscribed_channels = None

    tparams = {
        'title': MAIN_TITLE + title,
        'movies': movies,
        'subscribed_channels': subscribed_channels,
        'numCart': request.session.get('cartNumber', 0),
        'user': request.session.get('user', None),
        'thisUrl': resolve(request.path_info).url_name,
    }
    return render(request, 'pages/my-movies.html', tparams)

def pay(request, checkout_token):
    return redirect(payment.PAYMENT_SERVICE_URL + "/pay?checkout_token=" + checkout_token)

def makeauth(request, url):
    redirUrl = request.build_absolute_uri(reverse(url)).encode("utf-8")
    urlEncoded = base64.b64encode(redirUrl)
    return redirect('http://localhost:4200?url=' + str(urlEncoded).replace('b\'', '').replace('\'', '') +
                    '&id=es-stroam-frontend&sess_id=' + request.session.session_key)

def logout(request):
    if request.session.get('user_id', False):
        del request.session['user_id']
    if request.session.get('user_name', False):
        del request.session['user_name']
    if request.session.get('user_token', False):
        del request.session['user_token']
    if request.session.get('isAuthenticated', False):
        del request.session['isAuthenticated']
    return redirect('homepage')

def pushtest(request):
    title = 'Send a push notification'
    tparams = {
        'title': MAIN_TITLE + title,
        'user': request.session.get('user', None),
        'thisUrl': resolve(request.path_info).url_name,
    }
    return render(request, 'pages/push.html', tparams)

# FOR DEBUGGING PURPOSES! THIS WILL DELETE ALL STUFF (SESSIONS + FRONTEND DATABASE DATA)
def deleteAll(request):
    title = 'All data deleted'
    request.session.flush()
    Purchase.objects.all().delete()
    Purchase_Production.objects.all().delete()
    Notification_Subscription.objects.all().delete()
    Notification_Channels.objects.all().delete()

    tparams = {
        'title': MAIN_TITLE + title,
        'user': request.session.get('user', None),
        'thisUrl': resolve(request.path_info).url_name,
    }
    return render(request, 'pages/delete-all.html', tparams)
