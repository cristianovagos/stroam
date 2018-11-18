import json
import requests
from django.conf import settings
from urllib.request import urlopen

BASE_CATALOG_URL = "http://catalog:4000/api" if settings.USE_DOCKER else "http://localhost:4000/api"

class Genre:
    def __init__(self, id, name):
        self.id = id
        self.name = name

def getAllGenres():
    try:
        url = urlopen(BASE_CATALOG_URL + "/v1/catalog/genre/")
    except Exception:
        return None

    genres = []
    data = json.loads(url.read().decode())
    for line in data:
        genres.append(Genre(line['id'], line['name']))
    return genres

def getGenreInfo(id):
    assert isinstance(id, int)

    try:
        url = urlopen(BASE_CATALOG_URL + "/v1/catalog/genre/" + str(id))
    except Exception:
        return None

    data = json.loads(url.read().decode())
    return Genre(data['id'], data['name'])

def getGenreByName(genrename):
    r = requests.get(BASE_CATALOG_URL + "/v1/catalog/genre", params={"name": genrename})
    try:
        data = json.loads(r.text)
    except Exception:
        return None
    if r.status_code != 404:
        genre_id = data[0]['id']
        genre_name = data[0]['name']
        return Genre(genre_id, genre_name)
    return None

