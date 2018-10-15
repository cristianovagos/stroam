from django.http import Http404

def deleteProductListFromSession(request):
    try:
        del request.session['productList']
    except KeyError:
        pass

def checkURLOrigin(request, url):
    try:
        referer = request.META.get('HTTP_REFERER', None)
        referer.index(url)
    except Exception:
        raise Http404("Bad Request")
