 _   _       _  _____ _          ____                  _          
| \ | | ___ | ||_   _| |__   ___/ ___|  ___ _ ____   _(_) ___ ___
|  \| |/ _ \| __|| | | '_ \ / _ \___ \ / _ | '__\ \ / | |/ __/ _ \
| |\  | (_) | |_ | | | | | |  __/___) |  __| |   \ V /| | (_|  __/
|_| \_|\___/ \__||_| |_| |_|\___|____/ \___|_|    \_/ |_|\___\___|
                  MQTT based notification service

## About
NotTheService is a notification service based in MQTT protocol.
This service aims to process and forward the various notifications to all its users with great simplicity accross the diferent services and by email.

## API Specification
The service will be available on port 1884 in the form **ws://serverAddress:1884"** where MQTT messages should be forwarded.

Due to the uncoupling of producers and subscribers created by the publish/subscribe protocol in order to know and distinguish producers from subscribers, some requirements must be fulfilled to guarantee the sending and receiving of notifications.

-&gt; Any service message should use a channel with a "/stroam" prefix.
-&gt; Producers and subscribers must be duly registered in this service so that no notifications are lost.

**Channels**
>/stroam/&lt;producer&gt;/subscriber/register -&gt; channel to user registration.
>/stroam/&lt;producer&gt;/subscriber/unregister -&gt; channel to user deregistration.
>/stroam/&lt;producer&gt;/subscriber/subscribe -&gt; channel to indicate that the user subscribed to a channel.
>/stroam/&lt;producer&gt;/subscriber/unregister -&gt; channel to indicate that the user unsubscribed to a channel.
>/stroam/&lt;producer&gt;/serviceResponse -&gt; channel used for the producer to receive the answers to their requests
>/stroam/&lt;producer&gt;/... -&gt; any other channels are available for the producer to use as he intend.

<h3>Producer service messages</h3>
No delivery of notifications to unregistered producers and messages that do not meet the following criteria is guaranteed.

>**Producer registration**
(not available yet)

>**Producer deregistration**
(not available yet)


<h3>Subscriber service messages</h3>
No delivery of notifications to unregistered subscribers and messages that do not meet the following criteria is guaranteed.

>**Subscriber registration**
* Message stucture:
>{"senderId":"..." "subscriberId":"..." "subscriberEmail":"..." "subscriberPhone":"..." }
* Where:
>senderId is the id of the sender (publisher)
>subscriberId is the id of the subscriber
>subscriberEmail is the email address to which the subscriber want to receive the notifications
>subscriberPhone is the phone number to which the subscriber want to receive the notifications

Note: If the subscriber is already registered a message of this type can be used to update your data.

>**Subscriber deregistration**
* Message stucture:
{"senderId":"..." "subscriberId":"..."}
* Where:
>senderId is the id of the sender (publisher)
>subscriberId is the id of the subscriber

>**Subscriber subscription**
* Message stucture:
>{"senderId":"..." "subscriberId":"..." "channel":"..."}
* Where:
>senderId is the id of the sender (publisher)
>subscriberId is the id of the subscriber
>channel s the channel that the subscriber want to subscribe to

>**Subscriber ubsubscription**
* Message stucture:
>{"senderId":"..." "subscriberId":"..." "channel":"..."}
* Where:
>senderId is the id of the sender (publisher)
>subscriberId is the id of the subscriber
>channel is the channel that the subscriber want to unsubscribe to

## What is done
- All server communication logic
- Some of the message processors

## What is left
- Some message processing logic
- Database usage logic
- Producer and subscriber registration logic
- Bonus features like send notifications via SMS using capable hardware

## Deployment
(more details briefly)