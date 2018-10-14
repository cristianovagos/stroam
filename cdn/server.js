
var express = require('express');
var app = express();
var fs = require("fs");
var opn = require('opn');

app.get('/streamMovie/:id', function (req, res) {

	// CORS headers
	res.header("Access-Control-Allow-Origin", "*");
	res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");

	if(fs.existsSync( __dirname + "/movies/" + req.params.id + ".mp4")){
		console.log("Movie found");
		res.statusCode = 200;
		renderWebpage(req.params.id);
		return res.json({ notice : ['Movie found!'] });
	}else
		console.log("Movie not found");
		res.statusCode = 404;
      	return res.json({ errors: ['Movie not found!'] });

});

app.post('closeStream/:id', function (req, res){

	// CORS headers
	res.header("Access-Control-Allow-Origin", "*");
	res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");

	// TODO
	// << logic to stop stream and following operations goes here >>

	/*
	fs.unlink("new_server"+movie_id+".html", function(err){
		if(err) throw err;
		console.log("Page removed");
	});
	*/

});

app.all('*', function(req, res) {
    throw new Error("Invalid request")
});

var server = app.listen(1935, function () {

	var host = server.address().address
	var port = server.address().port

	console.log("Server listening at http://%s:%s", host, port);

});


function renderWebpage(movie_id){

	// delete old html page if existent
	
	fs.unlink("./new_server"+movie_id+".html", function(err){
		if(err) throw err;
		console.log("Old page removed");
	});
	
	fs.appendFile("new_server"+movie_id+".html",
		"<!doctype html> \
		<html lang=\"en\"> \
		<head> \
		<meta charset=\"utf-8\"> \
		<title>Server side</title> \
		<link rel=\"stylesheet\" href=\"css/styles.css?v=1.0\"> \
		<style> \
		    video { \
		        width: 800px; \
		        height:600px; \
		    } \
		</style> \
		</head> \
		<body onload=\"pageReady()\"> \
		<div align=\"center\" border-style=\"hidden\" border-width=\"5 px \"> \
			Movie " + movie_id + " title \
		</div> \
		<div id=\"player_div\" align=\"center\"> \
        	<video controls id=\"sourceVideoPlayer\" src=\"./movies/"+movie_id+".mp4\" type=\"video/mp4\">Video not available.</video> \
    	</div> \
		  <script src=\"server_stream.js\"></script> \
		</body> \
		</html>" ,

	function (err){
		if(err) throw err;
		console.log("Page created successfuly!");
	});


	opn("./new_server"+movie_id+".html");
}
