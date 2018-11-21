from django.urls import path
from . import views

urlpatterns = [
    path('v1/subscriptions/<slug:channel_name>', views.getUserSubscriptionsByChannel),
]