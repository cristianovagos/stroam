
var serverConnection;
var peerConnection;
var localStream;
var uuid;
var player;

var peerConnectionConfig = {
  'iceServers': [
    {'urls': 'stun:stun.stunprotocol.org:3478'},
    {'urls': 'stun:stun.l.google.com:19302'},
  ]
};

const offerOptions = {
  offerToReceiveAudio: 1,
  offerToReceiveVideo: 1
};

uuid = createUUID(); // unique identifier to distinguish users at the server
console.log("User UUID: " + uuid);

function showPlayer(){
    var playerDiv = document.getElementById("playerDiv");
    if (playerDiv.style.display === "none") {
        console.log("block");
        playerDiv.style.display = "block";
    } else {
        playerDiv.style.display = "none";
    }
}

function playerSetup(){

	// get movie id from url, not the best approach but will do for now
	var movie_id = window.location.href.split("/");

	console.log("Movie id:" + movie_id[movie_id.length-1]);

	//streamMovie(1);
    
    checkMovieAvailable(1);

    //initWebRTCPeerSession();

}

function checkMovieAvailable(movie_id){

    var xmlHttp = new XMLHttpRequest();
    xmlHttp.onreadystatechange = function() {   
        
        if (xmlHttp.readyState == 4){ // if successfuly sent
            console.log("state change");
            if(xmlHttp.status == 200){
                console.log("Response: " + xmlHttp.response);
                showPlayer();
                // initWebRTCPeerSession(); ------------------------------------------------------------------------------
            }
            else{
                alert("Movie not found!");
            }
        }
    }

    xmlHttp.open("POST", "http://localhost:1935/streamMovie/", true);
    xmlHttp.send(JSON.stringify({"uuid" : uuid, "movie_id" : movie_id}));

}

function initWebRTCPeerSession(){

    console.log("Initating WebRTC Peer Connection...");

    player = document.getElementById("player");

    serverConnection = new WebSocket("ws://localhost:1935");

    peerConnection = new RTCPeerConnection(peerConnectionConfig);

    serverConnection.onopen = async function(){ 
    
        const offer = await peerConnection.createOffer(offerOptions);
        await onCreateOfferSuccess(offer);


        // peerConnection.createOffer().then(createdDescription);//.catch(errorHandler);

        /*
        peerConnection.createOffer().then(function(offer) {
            return myPeerConnection.setLocalDescription(offer);
        }).then(function() { 
            serverConnection.send(JSON.stringify({'sdp': peerConnection.localDescription, 'uuid': uuid}));
        }).catch(errorHandler);
        */
    
    };

    //serverConnection.onmessage = processServerMessage;
    //serverConnection.onclose = function() { console.log("Socket closed"); };
    //serverConnection.onerror = function() { console.log("Socket error"); };

    //serverConnection.onopen = function() { console.log('Socket connection with server established') };
    //serverConnection.onmessage = processServerMessage;

    /*peerConnection.onicecandidate = gotIceCandidate;
    peerConnection.ontrack = gotRemoteStream;
    peerConnection.addStream(localStream);

    if(isCaller) {
        peerConnection.createOffer().then(createdDescription).catch(errorHandler);
    }
    */

    /*
    var constraints = {
        video: true,
        audio: true,
    };

    if(navigator.mediaDevices.getUserMedia) {
        navigator.mediaDevices.getUserMedia(constraints).then(getUserMediaSuccess).catch(errorHandler);
    } else {
        alert('Your browser does not support getUserMedia API');
    }
    */ 
}

async function onCreateOfferSuccess(desc) {
    console.log(`Offer from pc1\n${desc.sdp}`);
    console.log('pc1 setLocalDescription start');
    try {
        await peerConnection.setLocalDescription(desc);
        onSetLocalSuccess(peerConnection);
    } catch (e) {
        console.log("Error: " + e);
    }
}

function onSetLocalSuccess(pc) {
    console.log("setLocalDescription complete");
    serverConnection.send(JSON.stringify({'sdp' : peerConnection.localDescription, 'uuid': uuid}));
}

function createdDescription(description) {
    console.log('got description');
    peerConnection.setLocalDescription(description).then(function() {
        serverConnection.send(JSON.stringify({'sdp': peerConnection.localDescription, 'uuid': uuid}));
    }).catch(errorHandler);
}

function processServerMessage(message){
 
    // if(!peerConnection) start(false);

    var signal = JSON.parse(message.data);

    // Ignore messages from ourself
    if(signal.uuid == uuid) return;

    if(signal.sdp) {
        peerConnection.setRemoteDescription(new RTCSessionDescription(signal.sdp)).then(function() {
        // Only create answers in response to offers
            if(signal.sdp.type == 'offer') {
                peerConnection.createAnswer().then(createdDescription).catch(errorHandler);
            }
        }).catch(errorHandler);
    }else if(signal.ice) {
        peerConnection.addIceCandidate(new RTCIceCandidate(signal.ice)).catch(errorHandler);
    }

}

/*

function start(isCaller) {

    peerConnection = new RTCPeerConnection(peerConnectionConfig);
    peerConnection.onicecandidate = gotIceCandidate;
    peerConnection.ontrack = gotRemoteStream;
    peerConnection.addStream(localStream);

    if(isCaller) {
        peerConnection.createOffer().then(createdDescription).catch(errorHandler);
    }
}

*/

function errorHandler(error) {
  console.log(error);
}

function getUserMediaSuccess(stream) {
    localStream = stream;
    player.srcObject = stream;
}

function gotRemoteStream(event) {
    console.log('got remote stream');
    player.srcObject = event.streams[0];
}

// Taken from http://stackoverflow.com/a/105074/515584
// Strictly speaking, it's not a real UUID, but it gets the job done here
function createUUID() {
    function s4() {
        return Math.floor((1 + Math.random()) * 0x10000).toString(16).substring(1);
    }
    return s4() + s4() + '-' + s4() + '-' + s4() + '-' + s4() + '-' + s4() + s4() + s4();
}
