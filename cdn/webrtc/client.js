
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

/*
const offerOptions = {
    video: true,
    audio: true,
};
*/

uuid = createUUID(); // unique identifier to distinguish users at the server
console.log("User UUID: " + uuid);

/*
if(navigator.mediaDevices.getUserMedia) {
    navigator.mediaDevices.getUserMedia(constraints).then(getUserMediaSuccess).catch(errorHandler);
}else {
    alert('Your browser does not support getUserMedia API');
}
*/

function showPlayer(){
    var playerDiv = document.getElementById("playerDiv");
    if (playerDiv.style.display === "none") {
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
                initWebRTCPeerSession();
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

    peerConnection.onicecandidate = gotIceCandidate;
    peerConnection.onTrack = gotRemoteTrack;
    peerConnection.onaddstream = gotRemoteStream; /**** Deprecated */

    serverConnection.onopen = async function(){ 
    
        const offer = await peerConnection.createOffer(offerOptions);

        await onCreateOfferSuccess(offer);
    
    };

    serverConnection.onmessage = processServerMessage;

    /*
    var dataChannel = peerConnection.createDataChannel("client_channel");

    dataChannel.onmessage = function(event){
        console.log("new message");
        console.log(event);
    }
    */


     /*
    player.addEventListener('resize', () => {
      console.log(`Player size changed to ${player.videoWidth}x${player.videoHeight}`);
      // We'll use the first onsize callback as an indication that video has started
      // playing out.
      if (start_time) {
        const elapsedTime = window.performance.now() - start_time;
        console.log('Setup time: ' + elapsedTime.toFixed(3) + 'ms');
        startTime = null;
      }
    }); 
    */
}

async function onCreateOfferSuccess(desc) {
    //console.log(`Offer from pc1\n${desc.sdp}`);
    console.log('pc1 setLocalDescription start');
    try {
        await peerConnection.setLocalDescription(desc);
        onSetLocalSuccess(peerConnection);
    } catch (e) {
        console.log("Error: " + e);
    }
}

function gotIceCandidate(event) {
    console.log("got ice candidate");
    if(event.candidate != null) {
        serverConnection.send(JSON.stringify({'ice': event.candidate, 'uuid': uuid}));
    }
}

function onSetLocalSuccess(pc) {
    console.log("setLocalDescription complete");
    serverConnection.send(JSON.stringify({'sdp' : peerConnection.localDescription, 'uuid': uuid}));
}

function gotRemoteStream(event) {
  console.log('got remote stream!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!');
  console.log(event);
  player.srcObject = event.stream;
  player.play();
}

function gotRemoteTrack(event) {
  console.log('got remote track!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!');
  player.srcObject = event.streams[0];
}

/*
function createdDescription(description) {
    console.log('got description');
    peerConnection.setLocalDescription(description).then(function() {
        serverConnection.send(JSON.stringify({'sdp': peerConnection.localDescription, 'uuid': uuid}));
    }).catch(errorHandler);
}
*/

function processServerMessage(message){
 
    // if(!peerConnection) start(false);

    var signal = JSON.parse(message.data);

    // Ignore messages from ourself
    if(signal.uuid == uuid) return;

    if(signal.sdp) {
        peerConnection.setRemoteDescription(new RTCSessionDescription(signal.sdp)).then(function() {

            start_time = window.performance.now();
        // Only create answers in response to offers
        /*
            if(signal.sdp.type == 'offer') {
                peerConnection.createAnswer().then(createdDescription).catch(errorHandler);
            }
        */
        }).catch(errorHandler);
    }else if(signal.ice) {
        console.log("New message: new ICE candidate");
        console.log(signal.ice);
        peerConnection.addIceCandidate(new RTCIceCandidate(signal.ice)).catch(errorHandler);
    }

}

function errorHandler(error) {
  console.log(error);
}

/*
function getUserMediaSuccess(stream) {
    localStream = stream;
    player.srcObject = stream;
    console.log(player.src);
    console.log(player.srcObject);
}
*/

/*
function gotRemoteStream(event) {
    console.log('got remote stream');
    player.srcObject = event.streams;
}
*/

// Taken from http://stackoverflow.com/a/105074/515584
// Strictly speaking, it's not a real UUID, but it gets the job done here
function createUUID() {
    function s4() {
        return Math.floor((1 + Math.random()) * 0x10000).toString(16).substring(1);
    }
    return s4() + s4() + '-' + s4() + '-' + s4() + '-' + s4() + '-' + s4() + s4() + s4();
}
