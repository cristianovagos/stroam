import es1819.stroam.notification.client.NotTheServiceClient;
import es1819.stroam.notification.client.NotTheServiceClientCallback;

import java.util.UUID;

//TODO: you need to import the NotTheServiceClient.jar library to your project
public class Example implements NotTheServiceClientCallback {

    private NotTheServiceClient notTheServiceClient;

    private Example() {
        //create the client
        notTheServiceClient = new NotTheServiceClient("ws://<serverAddress>:1884");
        //set callback to use events
        notTheServiceClient.setCallback(this);

        try {
            //connect the client
            notTheServiceClient.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onConnect() {
        //Occurs when client successfully connects
        //You can use this event to subscribe the topics that you need
        try {
            //you need this topic if you need to get the responses to the email and phone requests
            notTheServiceClient.subscribe("/requestResponse");

            //subscribe the topic /topic
            notTheServiceClient.subscribe("/topic");

            //send push to topic topic
            //(you can send whatever you want when publish a notification, like xml, json or base64 string)
            notTheServiceClient.sendPush("/topic", "push message");
            //send email to mail address
            String emailRequestId = notTheServiceClient.sendEmail("maildestiny@service.com", "mail subject", "mail body, can be HTML");
            //you can set your own requestId, for example:
            notTheServiceClient.sendEmail("maildestiny@service.com", "mail subject", "mail body, can be HTML", UUID.randomUUID().toString());
            //send phone (dont use "+" like +351!)
            String phoneRequestId = notTheServiceClient.sendPhone("00351912345678", "phone body up to 160 characters");
            //you can set your own requestId, for example:
            notTheServiceClient.sendPhone("00351912345678", "phone body up to 160 characters", UUID.randomUUID().toString());
            //disconnect the client
            notTheServiceClient.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResponseRequestArrived(String requestId, int code, String reason) {
        //Occurs when a response to a email or phone request arrived
        //requestId is the given id, or the generated id in when the request have been made
        //code is the code of the request response, view the documentation to know more about it
        //reason is the reason why you get this response, for example,
        //request as been successfully processed or the email adddress of the email request are invalid
        //Unfortunately you can receive messages from other requests even if they are not yours
    }

    @Override
    public void onPushArrived(String topic, String push) {
        //Occurs when a push notification arrive on a subscribed topic
        //topic is the topic from where the push arrived
        //push is the body of the push
    }

    @Override
    public void onConnectionLost(Throwable throwable) {
        //Occurs with, for some reason (throwable) the connection to the broker is losted
    }

    @Override
    public void onDisconnect() {
        //Occurs after the client successfully disconnects
    }
}
