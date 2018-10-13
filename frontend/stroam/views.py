from random import shuffle
from django.shortcuts import render, redirect
from django.http import HttpRequest, HttpResponse
from channels.layers import get_channel_layer
from asgiref.sync import async_to_sync

from .catalog import catalog

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

    movie = catalog.getSingleCatalog(id)
    if movie is not None:
        movieTitle = movie.title

    if request.method == 'POST':
        request.session.flush()
        auxDict = {}
        if 'productList' in request.session:
            auxDict = request.session.get('productList')
        if request.POST['seasonID'] and request.POST['seasonNum']:
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
            'numCart': numCartProducts
        }
        return render(request, 'base/single-movie.html', tparams)

    tparams = {
        'title': MAIN_TITLE + movieTitle,
        'movie': movie,
        'addedToCart': False,
        'numCart': numCartProducts
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

def checkout(request):
    title = 'Checkout'
    numCartProducts = 0
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
    return render(request, 'pages/checkout.html', tparams)

