//var express = require('express');
//var app = express();
var opn = require('opn');
var fs = require("fs");
var WebSocket = require('ws');
const http = require('http');
var WebSocketServer = WebSocket.Server;

const HTTP_PORT = 1935;

var usersID = new Set();

var clients = {};

const handleRequest = function(request, response) {
	console.log('request received: ' + request.url);

	if (request.method === "POST" && request.url === "/streamMovie/") {
	    let body = '';
	    request.on('data', chunk => { body += chunk.toString(); });
	    request.on('end', () => {
	    	body = JSON.parse(body)
	    	var uuid = body["uuid"];
	    	var movie_id = body["movie_id"];

	        console.log(body);

			var movie_src = __dirname + "/movies/" + movie_id + ".mp4";

			fs.stat(movie_src, function(error, stats) {
				if(error){
					if(error.code==='ENOENT'){
						console.log("A");
						response.setHeader("Content-Type", "application/json");
						response.setHeader("Access-Control-Allow-Origin", "*");
						response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
						response.setHeader("Status", 404); // file not found
						response.end(JSON.stringify({"response": "Movie not found"}));
						return response;
					}
				}
		        if(!usersID.has(uuid)){
	        		usersID.add(uuid);
	        		console.log("User session added to the server");
	        		response.setHeader("Content-Type", "application/json");
	        		response.setHeader("Access-Control-Allow-Origin", "*");
					response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
	        		response.writeHead(200);
	        		response.end(JSON.stringify({"response": "Movie found"}));
	        		loadServerPeerMovie(uuid, movie_id);
	        		return response;
	        		//return response;
	        	}else{
	        		console.log("User already has an open session with the server!");
	        		response.writeHead(404);
	        		response.end();
	        		return response;
	        	}
			});

	    });
	}
	
	/*
	if(request.url === '/') {
		response.writeHead(200, {'Content-Type': 'text/html'});
		response.end(fs.readFileSync('client/index.html'));
	} else if(request.url === '/webrtc.js') {
		response.writeHead(200, {'Content-Type': 'application/javascript'});
		response.end(fs.readFileSync('client/webrtc.js'));
	}
	*/
};

const httpsServer = http.createServer(handleRequest);
httpsServer.listen(HTTP_PORT, '0.0.0.0');

// Create a server for handling websocket calls
const wss = new WebSocketServer({server: httpsServer});

wss.on("connection", (ws) => {
	console.log("Websocket connection open!");
	ws.on('message', function(message) {
		var msg = JSON.parse(message);
		console.log(msg);
		//console.log('received: %s', message);
		if(msg['sdp']){
			if(usersID.has(msg['uuid'])){
				console.log(msg['uuid']);
				clients.push({ 'uuid' : msg['uuid'], 'ws' : ws});
			}
			if(msg['uuid'] === 0){ // server id
				console.log("Server peer added to client list");
				usersID.add(msg['uuid']);
				clients[msg['uuid']] = ws;
			}
		}else{
			console.log("No uuid in sdp message. Aborting...");
			return;
		}
	});
});

console.log('Server running. https://localhost:' + HTTP_PORT);

loadServerPeer();

function loadServerPeer(){
	console.log("Opening server peer web page");
	opn("./server.html");
}

function loadServerPeerMovie(uuid, movie_id){
	console.log("Loading movie " + movie_id + " to serve to peer " + uuid);
	if(clients[0]) clients[0].send(JSON.stringify({'load' : movie_id}));
}

// exit callback
function onExit(){
	// do app specific cleaning before exiting
	process.on('exit', function () {
		console.log(' Stopping server ...');
		process.emit('cleanup');
	});

	// catch ctrl+c event and exit normally
	process.on('SIGINT', function () {
		console.log('\nCtrl-C...');
		console.log(' Stopping server ...');
		closeServerPeer();
		process.exit(2);
	});

	//catch uncaught exceptions, trace, then exit normally
	process.on('uncaughtException', function(e) {
		console.log('Uncaught Exception...');
		console.log(e.stack);
		process.exit(99);
	});

}

function closeServerPeer(){
	if(usersID.has(0)){
		console.log("Closing server peer page...");
		clients[0].send(JSON.stringify({'sdp' : 'end'}));	
	}
}

onExit();

/*
function renderServerPeerWebpage(movie_id){

	// delete old html page if existent
	if(fs.existsSync( __dirname +"/"+movie_id+".html")){
		fs.unlink( __dirname +"/"+movie_id+".html", function(err){
			if(err) throw err;
			console.log("Old page removed");
		});
	}
	
	fs.appendFile(movie_id+".js",
		"",

	function (err){
		if(err) throw err;
		console.log("Page created successfuly!");
	});


	opn("./"+movie_id+".html");
}
*/