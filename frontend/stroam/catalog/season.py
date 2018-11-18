import json
from django.conf import settings
from urllib.request import urlopen

BASE_CATALOG_URL = "http://catalog:4000/api" if settings.USE_DOCKER else "http://localhost:4000/api"

class Season:
    def __init__(self, id, seasonNum, episodes):
        self.id = id
        self.seasonNum = seasonNum
        self.episodes = episodes

def getSeriesSeason(id, seasonNum):
    assert isinstance(seasonNum, int)
    assert isinstance(id, int)

    try:
        url = urlopen(BASE_CATALOG_URL + "/v1/catalog/" + str(id) + "/season/" + str(seasonNum))
    except Exception:
        return None

    data = json.loads(url.read().decode())

    id = data['id']
    episodes = []
    for episode in data['episodes']:
        ep = {}
        ep['num'] = episode['episode']
        ep['title'] = episode['title']
        ep['releaseDate'] = episode['releaseDate']
        episodes.append(ep)

    return Season(id, seasonNum, episodes)
