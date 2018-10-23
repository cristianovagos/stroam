import logging
from django.http import Http404

LOGGER = logging.getLogger(__name__)

def deleteProductListFromSession(request):
    try:
        del request.session['productList']
        del request.session['cartNumber']
    except KeyError:
        pass

def checkURLOrigin(request, url):
    try:
        referer = request.META.get('HTTP_REFERER', None)
        referer.index(url)
    except Exception:
        LOGGER.error('referer: ' + str(referer) + ', url: ' + str(url))
        raise Http404("Bad Request")
