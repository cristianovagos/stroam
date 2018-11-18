// let notificationClient = new NotTheServiceClient("127.0.0.1", 1884);

// Tópicos do canal são "/notTheService{/channelID}"

// callback
// function onConnect() {
//     notificationClient.subscribe("/requestResponse"); //if have interest in receive the requests responses
//     console.log("connected to notification service");
// }

// callback erros
function onResponseRequestArrived(requestId, errorCode, message) {
    console.log(requestId + " -> " + errorCode + ": " + message);
}

// callback push arrived - todas as respostas chegam aqui
function onPushArrived(topic, payload) {
    console.log("topic: ", topic);
    console.log("payload: ", payload);

    $.notify({
        // options
        icon: 'fas fa-bell',
        message: 'Push arrived!\nTopic: ' + topic + '\nPayload: ' + payload
    },{
        // settings
        type: 'danger',
        placement: {
            from: 'bottom',
            align: 'right'
        },
        animate: {
            enter: 'animated fadeInDown',
            exit: 'animated fadeOutUp'
        },
        timer: 1000
    });
}

function sendEmail_buttonClick() {
    console.log("email sended with request id: " +
    notificationClient.sendEmail(
        undefined, //or you can put you request id here (STRING por exemplo), se o requestID for undefined é devolvido um requestID
        document.getElementById('emailAddress').value,
        document.getElementById('emailSubject').value,
        document.getElementById('emailBody').value));
}

function sendPhone_buttonClick() {
    console.log("phone message sended with request id: " +
    notificationClient.sendPhone(
        undefined, //or you can put you request id here
        document.getElementById('phoneNumber').value,
        document.getElementById('phoneBody').value));
}

function sendPush_buttonClick() {
    // var pushTopic = document.getElementById('pushTopic').value;
    notificationClient.subscribe("/stroam-movie20"); //subscribe the topic to receive notifications

    notificationClient.sendPush("/stroam-movie20", "olaolaolaolaolaola");
    //client.unsubscribe(pushTopic); to unsubscribe an topic
    //notificationClient = new NotTheServiceClient("127.0.0.1", 1884);
}


// NOTIFICATION HANDLERS


// the WebSocket that will be used to subscribe/unsubscribe
// in frontend, using Django channels
var subscribeSocket = new WebSocket(
    'ws://' + window.location.host + '/ws/subscribe/'
);

// onopen callback
subscribeSocket.onopen = function (e) {
    console.log("subscribe socket opened");
};

// onerror callback
subscribeSocket.onerror = function (e) {
    console.error("subscribe socket error", e);
};

// onmessage callback
subscribeSocket.onmessage = function (e) {
    var data = JSON.parse(e.data);
    console.log("data received on socket: ", data);
};

// onclose callback
subscribeSocket.onclose = function (e) {
    console.error("Subscribe socket closed unexpectedly", e);
};

// subscribe user to a channel
function subscribeToChannel(e, user_id, channel_name) {
    if(e.innerHTML.trim().toLowerCase() === 'subscribe' ||
        e.innerHTML.trim().toLowerCase() === '&nbsp;subscribe') {
        notificationClient.subscribe('/' + channel_name);
        e.classList.remove('btn-danger');
        e.classList.add('btn-secondary');
        e.innerHTML = "\xa0Unsubscribe";
        console.log('Subscribing ' + user_id + ' to channel ' + channel_name);

        $.notify({
            // options
            icon: 'fas fa-bell',
            message: 'Subscrito com sucesso!'
        },{
            // settings
            type: 'info',
            placement: {
                from: 'top',
                align: 'right'
            },
            animate: {
                enter: 'animated fadeInDown',
                exit: 'animated fadeOutUp'
            },
            timer: 1000
        });
    } else {
        console.log('Unsubscribing ' + user_id + ' to channel ' + channel_name);
        notificationClient.unsubscribe('/' + channel_name);
        e.classList.remove('btn-secondary');
        e.classList.add('btn-danger');
        e.innerHTML = "\xa0Subscribe";

        $.notify({
            // options
            icon: 'fas fa-bell-slash',
            message: 'Acabou de retirar a sua subscrição...'
        },{
            // settings
            type: 'info',
            placement: {
                from: 'top',
                align: 'right'
            },
            animate: {
                enter: 'animated fadeInDown',
                exit: 'animated fadeOutUp'
            },
            timer: 1000
        });
    }

    subscribeSocket.send(JSON.stringify({
        'channel': channel_name,
        'user_id': user_id
    }));
}