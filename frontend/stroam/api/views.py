from django.http import JsonResponse
from ..models import *

def getUserSubscriptionsByChannel(request, channel_name):
    assert isinstance(channel_name, str)

    result = {}
    n = Notification_Subscription.objects.filter(channel_id__channel_name=channel_name)
    result['users'] = [user.user_id for user in n.all()]
    return JsonResponse(result)
