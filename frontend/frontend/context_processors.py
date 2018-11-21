from django.conf import settings

def global_settings(request):
    return {
        'NOTIFICATION_SERVER_HOST': settings.NOTIFICATION_SERVER_HOST,

    }