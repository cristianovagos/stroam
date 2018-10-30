
var express = require('express');
var app = express();
var fs = require("fs");
var WebSocket = require('ws');

app.get('/streamMovie/:id', function (req, res) {

	// CORS headers
	//res.header("Access-Control-Allow-Origin", "*");
	//res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");

	console.log("streamMovie");

	// source of movie requested by user
	var movie = __dirname + "/movies/" + req.params.id + ".mp4";

	fs.stat(movie, function(error, stats) {
		if(error){
			if(error.code==='ENOENT') res.sendStatus(404); // file not found
			return next(error); // return error
		}

		var range = req.headers.range; // range of bytes the player is fetching

		if(!range){
			var e = new Error('Invalid range!');
			e.status = 416; // server unable to fulfill request
			return next(error); // return error
		}

		var parts = range.replace(/bytes=/, "").split("-"); // get starting and ending bytes
		var file_size = stats.size; // get size of file in bytes
		var start = parseInt(parts[0],10); // first value to integer

		var finish;
		if(parts[1])
			finish = parseInt(parts[1], 10);
		else
			finish = file_size - 1;

		var ret_bytes = (finish - start) + 1; // bytes to return to client

		// header response specification
		var head = {
			'Content-Range': 'bytes ' + start + '-' + finish + '/' + file_size,
			'Accept-Ranges': 'bytes',
			'Content-Length': ret_bytes,
			'Content-Type': 'video/mp4'
		}

		res.writeHead(206, head); // 206 code => partial information, this case meaning ongoing stream

		var file_range = { start: start, end: finish }; // bytes range to read from file

		var stream = fs.createReadStream(movie, file_range); // create stream reader

		stream.on('open', function(){
			stream.pipe(res); // pipe response
		});

	});

});

app.post('closeStream/:id', function (req, res){

	// CORS headers
	//res.header("Access-Control-Allow-Origin", "*");
	//res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");

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
	res.writeHead(200, {'Content-Type': 'application/javascript'});
	res.end(fs.readFileSync('./client.js'));
	return res;
	//throw new Error("Invalid request")
});

var server = app.listen(1935, function () {

	var host = server.address().address
	var port = server.address().port

	console.log("Server listening at http://%s:%s", host, port);

});