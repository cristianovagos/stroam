// Tópicos do canal são "/notTheService{/channelID}"

// callback erros
function onResponseRequestArrived(requestId, errorCode, message) {
    console.log(requestId + " -> " + errorCode + ": " + message);
}

// callback push arrived - todas as respostas chegam aqui
function onPushArrived(topic, payload) {
    console.log("onPushArrived");
    console.log("topic: ", topic);
    console.log("payload: ", JSON.parse(payload));

    var obj = JSON.parse(payload);

    $.notify({
        // options
        icon: 'fas fa-bell',
        title: obj['title'],
        message: obj['message'],
        url: window.location + obj['url_path'],
        target: '_blank'
    },{
        // settings
        type: 'info',
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
        "test@stroam.com",
        "You received an email from STROAM!",
        "Hi! This is a awesome message!"));
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
    notificationClient.subscribe("/stroam-general"); //subscribe the topic to receive notifications

    notificationClient.sendPush("/stroam-general",
        JSON.stringify(
            {
                "title": "Notificação teste",
                "message": "Olá! Esta notificação é um teste!\nParece que está tudo OK ;)",
            }
        )
    );
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
                from: 'bottom',
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

    subscribeSocket.send(JSON.stringify({
        'channel': channel_name,
        'user_id': user_id
    }));
}