var localVideo;
var localStream;
var remoteVideo;
var peerConnection;
var uuid;
var serverConnection;

var peerConnectionConfig = {
  'iceServers': [
    {'urls': 'stun:stun.stunprotocol.org:3478'},
    {'urls': 'stun:stun.l.google.com:19302'},
  ]
};

function pageReady() {

	console.log("pageReady");

	player = document.getElementById('videoPlayer');

	serverConnection = new WebSocket("wss://localhost:1935"); // socket connection to stream ???

	serverConnection.onmessage = messageProcessor;


/* 
	TODO
	<< streaming logic at the client side >>
 
  uuid = createUUID();
  localVideo = document.getElementById('localVideo');
  remoteVideo = document.getElementById('remoteVideo');

  serverConnection = new WebSocket('wss://' + window.location.hostname + ':8443');
  serverConnection.onmessage = gotMessageFromServer;

  var constraints = {
    video: true,
    audio: true,
  };
*/
}
