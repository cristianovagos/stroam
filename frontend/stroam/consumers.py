from asgiref.sync import async_to_sync
from channels.generic.websocket import WebsocketConsumer
import json

class CartConsumer(WebsocketConsumer):
    def connect(self):
        async_to_sync(self.channel_layer.group_add)(
            'cart',
            self.channel_name
        )
        self.accept()

    def disconnect(self, code):
        async_to_sync(self.channel_layer.group_discard)(
            'cart',
            self.channel_name
        )

    def receive(self, text_data):
        text_data_json = json.loads(text_data)
        print(text_data_json)

        async_to_sync(self.channel_layer.group_send)(
            'cart',
            {
                'message': 'hello'
            }
        )

