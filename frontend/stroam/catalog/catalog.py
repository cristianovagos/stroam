import json
from urllib.request import urlopen, HTTPError

BASE_CATALOG_URL = "http://localhost:8090/api"

class Production:
    def __init__(self, id, title, releaseDate, year, genres, director, type, poster, description, runtime, price):
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

def getAllCatalog():
    prodsList = []

    try:
        url = urlopen(BASE_CATALOG_URL + "/catalog/")
    except HTTPError:
        return None

    data = json.loads(url.read().decode())

    for line in data:
        id = line['id']
        title = line['name']
        releaseDate = line['releaseDate']
        year = line['year']
        genres = []
        for genre in line['genres']:
            genres.append(genre['name'])
        director = line['director']
        type = line['type']
        poster = line['poster']
        description = line['description']
        runtime = line['runtime']
        price = line['price']

        p = Production(id, title, releaseDate, year, genres, director, type, poster, description, runtime, price)
        prodsList.append(p)

    return prodsList

def getSingleCatalog(id):
    assert isinstance(id, int)

    try:
        url = urlopen(BASE_CATALOG_URL + "/catalog/" + str(id))
    except HTTPError:
        return None

    data = json.loads(url.read().decode())

    id = data['id']
    title = data['name']
    releaseDate = data['releaseDate']
    year = data['year']
    genres = []
    for genre in data['genres']:
        genres.append(genre['name'])
    director = data['director']
    type = data['type']
    poster = data['poster']
    description = data['description']
    runtime = data['runtime']
    price = data['price']

    return Production(id, title, releaseDate, year, genres, director, type, poster, description, runtime, price)
