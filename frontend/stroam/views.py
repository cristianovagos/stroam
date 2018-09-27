from django.shortcuts import render
from django.http import HttpRequest

def homeView(request):
    assert isinstance(request, HttpRequest)

    tparams = {
        'title': 'STROAM'
    }
    return render(request, 'index.html', tparams)
