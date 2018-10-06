from random import shuffle
from django.shortcuts import render, redirect
from django.http import HttpRequest

from .catalog import catalog

SPACING = ' '
WEBSITE_TITLE = 'STROAM'
WEBSITE_SEPARATOR = '|'

MAIN_TITLE = WEBSITE_TITLE + SPACING + WEBSITE_SEPARATOR + SPACING

def home(request):
    title = 'Stream strong, anytime and anywhere.'
    numCartProducts = len(request.session.get('productList'))
    movies = catalog.getAllCatalog()
    shuffle(movies)
    tparams = {
        'title': MAIN_TITLE + title,
        'movies': movies,
        'numCart': numCartProducts
    }
    return render(request, 'pages/index.html', tparams)

def homeMovies(request):
    title = 'Our movies'
    numCartProducts = len(request.session.get('productList'))
    movies = [x for x in catalog.getAllCatalog() if x.type == 'movie']
    shuffle(movies)
    tparams = {
        'title': MAIN_TITLE + title,
        'movies': movies,
        'numCart': numCartProducts
    }
    return render(request, 'pages/index.html', tparams)

def homeSeries(request):
    title = 'Our TV Series'
    numCartProducts = len(request.session.get('productList'))
    movies = [x for x in catalog.getAllCatalog() if x.type == 'series']
    shuffle(movies)
    tparams = {
        'title': MAIN_TITLE + title,
        'movies': movies,
        'numCart': numCartProducts
    }
    return render(request, 'pages/index.html', tparams)

def singleMovie(request, id):
    assert isinstance(id, int)
    numCartProducts = len(request.session.get('productList'))

    movie = catalog.getSingleCatalog(id)
    if movie is not None:
        movieTitle = movie.title

    if request.method == 'POST':
        if 'productList' in request.session and request.POST['productID'] not in request.session['productList']:
            auxList = request.session.get('productList')
            auxList.append(int(request.POST['productID']))
            request.session['productList'] = auxList
        else:
            request.session['productList'] = [int(request.POST['productID'])]

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
    numCartProducts = len(request.session.get('productList'))

    if request.method == 'POST':
        if 'productList' in request.session:
            auxList = request.session.get('productList')
            auxList.remove(int(request.POST['productID']))
            request.session['productList'] = auxList
            return redirect('homepage')

    products = []
    price = 0
    if 'productList' in request.session:
        prodList = request.session.get('productList')
        for product in prodList:
            p = catalog.getSingleCatalog(int(product))
            price += p.price
            products.append(p)

    tparams = {
        'title': MAIN_TITLE + title,
        'products': products,
        'totalPrice': price,
        'numCart': numCartProducts
    }
    return render(request, 'pages/shopping-cart.html', tparams)

def checkout(request):
    title = 'Checkout'
    numCartProducts = len(request.session.get('productList'))

    products = []
    price = 0
    if 'productList' in request.session:
        for product in request.session['productList']:
            p = catalog.getSingleCatalog(int(product))
            price += p.price
            products.append(p)

    tparams = {
        'title': MAIN_TITLE + title,
        'products': products,
        'totalPrice': price,
        'numCart': numCartProducts
    }
    return render(request, 'pages/checkout.html', tparams)