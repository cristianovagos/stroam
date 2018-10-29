
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

var serverConnection;
var peerConnection;
var localStream;
var uuid;

var video;
var recording_video;

function pageReady(){

	video = document.getElementById('sourceVideoPlayer');
	recording_video = document.getElementById('recordingVideoPlayer');

	serverConnection = new WebSocket("ws://localhost:1935");

	//serverConnection.send(JSON.stringify({'sdp': 'server peer', 'uuid': 0}));

	serverConnection.onopen = function(evt) {
    	console.log("Socket open");
    	console.log(evt);
    	serverConnection.send(JSON.stringify({'sdp': 'server peer online', 'uuid': 0}));
    };

    serverConnection.onmessage = processMessage;

}

function processMessage(msg){
	if(JSON.parse(msg.data)['sdp'] === "end"){
		//window.location.reload(true);
		//window.close(); // js code cannot close window because node.js opened it
		serverConnection.close();
		console.log("Closing...");
	}
	if(JSON.parse(msg.data)['load']){
		loadPlayerSrc(JSON.parse(msg.data)['load']);
	}

}

function loadPlayerSrc(movie_id){
	console.log("Loading video player src as " + movie_id + ".mp4");
	console.log(video);
	video.src = "./movies/"+movie_id+".mp4";

	// captureStream(); only to be called after WebRTC session is established between the peers

}

function captureStream(){

	var capture;

	video.addEventListener('canplay', () => {
		capture = video.mozCaptureStream();
		recording_video.srcObject = capture;
		console.log("Local recording started");
	});
}