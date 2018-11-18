class NotTheServiceClient {
    
    constructor(serverAddress, port) {
        this.mqttClient = new Paho.MQTT.Client(serverAddress, port, NotTheServiceClient.randomUniqueIdentifier())
        this.mqttClient.connect({ onSuccess: onConnect });

        this.send = function(topic, data) {
            if(this.isUndefinedNullOrEmpty(topic) || this.isUndefinedNullOrEmpty(data))
                return;
    
            var message = new Paho.MQTT.Message(data)
            message.destinationName = "/notTheService" + topic;
            this.mqttClient.send(message);
        }

        this.isUndefinedNullOrEmpty = function(value) {
            return typeof value == "undefined" || value == null || value == "";
        }

        this.mqttClient.onMessageArrived = function(message) {
            if(typeof onResponseRequestArrived != "undefined" && message.destinationName == "/notTheService/requestResponse" ) {
                var payload = message.payloadString;
                if(typeof payload == "undefined" || payload == null || payload == "")
                    return;
                
                var jsonData;
                try{ jsonData = JSON.parse(payload); } catch (ignored) { return; }
                if(typeof payload == "undefined" || payload == null || payload == "")
                    return;
                
                onResponseRequestArrived(jsonData["requestId"], jsonData["resultCode"], jsonData["reason"]);
            } else if(typeof onPushArrived != "undefined") {
                onPushArrived(message.destinationName, message.payloadString);
            }
        }
    }

    subscribe(topic) {
        if(this.isUndefinedNullOrEmpty(topic))
            throw "topic is undefined, null or empty";

        this.mqttClient.subscribe("/notTheService" + topic);
    }

    unsubscribe(topic) {
        if(this.isUndefinedNullOrEmpty(topic))
            throw "topic is undefined, null or empty";

        this.mqttClient.unsubscribe("/notTheService" + topic);
    }

    sendEmail(requestId, emailAddress, subject, body) {
        if(this.isUndefinedNullOrEmpty(emailAddress))
            throw "email address is undefined, null or empty";
        if(this.isUndefinedNullOrEmpty(body))
            throw "email body is undefined, null or empty";
        if(this.isUndefinedNullOrEmpty(requestId))
            requestId = NotTheServiceClient.randomUniqueIdentifier();
        
        var jsonMessageData = {};
        jsonMessageData["requestId"] = requestId;
        jsonMessageData["subject"] = btoa(subject);
        jsonMessageData["body"] = btoa(body);

        this.send("/email/" + emailAddress, JSON.stringify(jsonMessageData));
        return requestId;
    }

    sendPhone(requestId, phoneNumber, body) {
        if(this.isUndefinedNullOrEmpty(phoneNumber))
            throw "phone number is undefined, null or empty";
        if(this.isUndefinedNullOrEmpty(body))
            throw "phone body is undefined, null or empty";
        if(this.isUndefinedNullOrEmpty(requestId))
            requestId = NotTheServiceClient.randomUniqueIdentifier();

        var jsonMessageData = {};
        jsonMessageData["requestId"] = requestId;
        jsonMessageData["body"] = btoa(body);

        this.send("/phone/" + phoneNumber, JSON.stringify(jsonMessageData));
        return requestId;
    }

    sendPush(topic, body) {
        if(this.isUndefinedNullOrEmpty(topic))
            throw "push topic is undefined, null or empty";
        if(this.isUndefinedNullOrEmpty(body))
            throw "push body is undefined, null or empty";

        this.send(topic, body);
    }

    //solução sucateira
    static randomUniqueIdentifier() {
        function randomUIpart() {
            return Math.floor((1 + Math.random()) * 0x10000).toString(16).substring(1);
        }
        return randomUIpart() + randomUIpart() + 
        "-" + randomUIpart() + 
        "-" + randomUIpart() + 
        "-" + randomUIpart() +
        "-" + randomUIpart() + randomUIpart() + randomUIpart();
    }
}


