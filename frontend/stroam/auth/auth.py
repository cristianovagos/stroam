from django.conf import settings

AUTH_SERVICE_URL = "http://engserv-3-aulas.ws.atnog.av.it.pt/auth" if settings.USE_DOCKER else "http://localhost:4200"
AUTH_SERVICE_API_URL = "http://authserver:3000/api/" if settings.USE_DOCKER else "http://localhost:3000/"