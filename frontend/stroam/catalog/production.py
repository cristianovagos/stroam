import json
import requests
from urllib.request import urlopen
from .genre import *
from .season import *

# ON LOCAL DEVELOPMENT CHANGE THIS
#BASE_CATALOG_URL = "http://localhost:4000/api"
BASE_CATALOG_URL = "http://catalog:4000/api"

class Production:
    def __init__(self, id, title, releaseDate, year, genres, director, type, poster, description, runtime, price, seasons=None, seasonList=None):
        self.id = id
        self.title = title
        self.releaseDate = releaseDate
        self.year = year
        self.genres = genres
        self.director = director
        self.type = type
        self.poster = poster
        self.description = description
        self.runtime = runtime
        self.price = price
        self.seasons = seasons
        self.seasonList = seasonList

def getAllProduction():
    prodsList = []

    try:
        url = urlopen(BASE_CATALOG_URL + "/v1/catalog/")
    except Exception:
        return None

    data = json.loads(url.read().decode())

    for line in data:
        id = line['id']
        title = line['name']
        releaseDate = line['releaseDate']
        year = line['year']
        genres = []
        for genre in line['genres']:
            genres.append(Genre(genre['id'], genre['name']))
        director = line['director']
        type = line['type']
        poster = line['poster']
        description = line['description']
        runtime = line['runtime']
        price = line['price']

        p = Production(id, title, releaseDate, year, genres, director, type, poster, description, runtime, price)
        prodsList.append(p)

    return prodsList

def getSingleProduction(id):
    assert isinstance(id, int)

    try:
        url = urlopen(BASE_CATALOG_URL + "/v1/catalog/" + str(id))
    except Exception:
        return None

    data = json.loads(url.read().decode())

    id = data['id']
    title = data['name']
    releaseDate = data['releaseDate']
    year = data['year']
    genres = []
    for genre in data['genres']:
        genres.append(Genre(genre['id'], genre['name']))
    director = data['director']
    type = data['type']
    poster = data['poster']
    description = data['description']
    runtime = data['runtime']
    price = data['price']
    seasons = data['seasons']

    seasonList = []
    if seasons > 0:
        for i in range(1, seasons+1):
            season = getSeriesSeason(id, i)
            seasonList.append(season)

    return Production(id, title, releaseDate, year, genres, director, type, poster, description, runtime, price, seasons, seasonList)

def getProductionsByGenreID(id):
    assert isinstance(id, int)

    try:
        url = urlopen(BASE_CATALOG_URL + "/v1/catalog/genre/" + str(id) + "/productions")
    except Exception:
        return None

    prodsList = []
    data = json.loads(url.read().decode())

    for line in data:
        id = line['id']
        title = line['name']
        releaseDate = line['releaseDate']
        year = line['year']
        genres = []
        for genre in line['genres']:
            genres.append(Genre(genre['id'], genre['name']))
        director = line['director']
        type = line['type']
        poster = line['poster']
        description = line['description']
        runtime = line['runtime']
        price = line['price']

        p = Production(id, title, releaseDate, year, genres, director, type, poster, description, runtime, price)
        prodsList.append(p)

    return prodsList
