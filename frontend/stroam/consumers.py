import json

from asgiref.sync import async_to_sync
from channels.generic.websocket import WebsocketConsumer
from .notifications import notifications

class SubscribeConsumer(WebsocketConsumer):
    def connect(self):
        self.accept()

    def disconnect(self, code):
        pass

    def receive(self, text_data):
        text_data_json = json.loads(text_data)
        channel = text_data_json['channel']
        user_id = text_data_json['user_id']

        print(text_data_json)

        if notifications.is_user_subscribed(user_id, channel):
            notifications.unsubscribe(user_id, channel)
        else:
            notifications.subscribe(user_id, channel)

        # self.send(text_data=json.dumps({
        #     'channel': message
        # }))

