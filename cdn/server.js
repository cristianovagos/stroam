
var express = require('express');
var app = express();
var fs = require("fs");

app.get('/streamMovie/:id', function (req, res) {

	if(fs.existsSync( __dirname + "/movies/" + req.params.id + ".mp4")){
		console.log("Movie found");
		res.statusCode = 200;
		return res.end();
	}else
		console.log("Movie not found");
		res.statusCode = 404;
      	return res.json({ errors: ['Movie not found!'] });

})

app.all('*', function(req, res) {
    throw new Error("Invalid request")
})

var server = app.listen(1935, function () {

	var host = server.address().address
	var port = server.address().port

	console.log("Server listening at http://%s:%s", host, port);
})
