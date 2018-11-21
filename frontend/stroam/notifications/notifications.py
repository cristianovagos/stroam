from ..models import *

def channel_exists(channel_name):
    return Notification_Channels.objects.filter(channel_name=channel_name).count() > 0

def is_user_subscribed(user_id=1, channel_name='stroam-general'):
    n = Notification_Subscription.objects.filter(user_id=user_id, channel_id__channel_name=channel_name)
    return n.count() > 0

def is_user_ever_subscribed(user_id=1):
    return Notification_Subscription.objects.filter(user_id=user_id).count() > 0

def subscribe(user_id=1, channel_name='stroam-general'):
    if not channel_exists(channel_name):
        ch = Notification_Channels(channel_name=channel_name)
        ch.save()
        print('Subscribe - channel ' + channel_name + ' created')
    else:
        ch = Notification_Channels.objects.filter(channel_name=channel_name).get().id

    if not is_user_subscribed(user_id, channel_name):
        if is_user_ever_subscribed(user_id):
            sub = Notification_Subscription.objects.filter(user_id=user_id).first()
        else:
            sub = Notification_Subscription(user_id=user_id)
            sub.save()
        sub.channel_id.add(ch)
        print('Subscribe - userID ' + str(user_id) + ' is now subscribed to channel ' + channel_name + ' !')

def unsubscribe(user_id=1, channel_name='stroam-general'):
    if not is_user_subscribed(user_id, channel_name):
        print('Unsubscribe - userID ' + str(user_id) + ' is not subscribed to channel ' + channel_name + ', exiting...')
        return
    n = Notification_Subscription.objects.filter(user_id=user_id).first()
    n.channel_id.remove(Notification_Channels.objects.filter(channel_name=channel_name).first())
    print('Unsubscribe - userID ' + str(user_id) + ' is now unsubscribed to channel ' + channel_name + ' !')
