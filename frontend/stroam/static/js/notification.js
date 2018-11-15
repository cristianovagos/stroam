let client = new NotTheServiceClient("localhost", 1884);

// Tópicos do canal são "/notTheService/{channelID}"


// callback
function onConnect() {
    client.subscribe("/requestResponse"); //if have interest in receive the requests responses
}

// callback erros
function onResponseRequestArrived(requestId, errorCode, message) {
    console.log(requestId + " -> " + errorCode + ": " + message);
}

// callback push arrived - todas as respostas chegam aqui
function onPushArrived(topic, payload) {
    console.log("topic: ", topic);
    console.log("payload: ", payload);

    var ta_pushNotification = document.getElementById('pushNotification');
    ta_pushNotification.value += topic + " -> " + payload + "\n";
}

function sendEmail_buttonClick() {
    console.log("email sended with request id: " +
    client.sendEmail(
        undefined, //or you can put you request id here (STRING por exemplo), se o requestID for undefined é devolvido um requestID
        document.getElementById('emailAddress').value,
        document.getElementById('emailSubject').value,
        document.getElementById('emailBody').value));
}

function sendPhone_buttonClick() {
    console.log("phone message sended with request id: " +
    client.sendPhone(
        undefined, //or you can put you request id here
        document.getElementById('phoneNumber').value,
        document.getElementById('phoneBody').value));
}

function sendPush_buttonClick() {
    var pushTopic = document.getElementById('pushTopic').value;
    client.subscribe(pushTopic); //subscribe the topic to receive notifications

    client.sendPush(pushTopic, document.getElementById('pushBody').value);
    //client.unsubscribe(pushTopic); to unsubscribe an topic
}