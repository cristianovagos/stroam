from django.shortcuts import render
from django.http import HttpRequest

SPACING = ' '
WEBSITE_TITLE = 'STROAM'
WEBSITE_SEPARATOR = '|'

MAIN_TITLE = WEBSITE_TITLE + SPACING + WEBSITE_SEPARATOR + SPACING

def home(request):
    assert isinstance(request, HttpRequest)
    title = 'Stream strong, anytime and anywhere.'

    # array dummy com filmes
    movies = [{
        'title': 'Man of Steel',
        'image': 'http://henrycavill.org/images/Films/2013-Man-of-Steel/posters/3-Walmart-Superman-a.jpg',
    }, {
        'title': 'Man of Steel',
        'image': 'http://henrycavill.org/images/Films/2013-Man-of-Steel/posters/3-Walmart-Superman-a.jpg',
    }]

    tparams = {
        'title': MAIN_TITLE + title,
        'movies': movies
    }
    return render(request, 'pages/index.html', tparams)

def singleMovie(request, id):
    assert isinstance(request, HttpRequest)
    movieTitle = 'Movie #' + str(id)

    tparams = {
        'title': MAIN_TITLE + movieTitle,
        'image': 'http://henrycavill.org/images/Films/2013-Man-of-Steel/posters/3-Walmart-Superman-a.jpg',
    }
    return render(request, 'base/single-movie.html', tparams)

def shoppingCart(request):
    assert isinstance(request, HttpRequest)
    title = 'Your shopping cart'

    tparams = {
        'title': MAIN_TITLE + title
    }
    return render(request, 'pages/shopping-cart.html', tparams)

def checkout(request):
    assert isinstance(request, HttpRequest)
    title = 'Checkout'

    tparams = {
        'title': MAIN_TITLE + title
    }
    return render(request, 'pages/checkout.html', tparams)