
function playerSetup(){

	// get movie id from url, not the best approach but will do for now
	var movie_id = window.location.href.split("/");

	console.log("Movie id:" + movie_id[movie_id.length-1]);

	//checkMovieAvailable(movie_id[movie_id.length-1])

	checkMovieAvailable(1); // check if movie id 1 is available

}

function showPlayer(){

	var playerDiv = document.getElementById("playerDiv");
    if (playerDiv.style.display === "none") {
        playerDiv.style.display = "block";
    } else {
        playerDiv.style.display = "none";
    }

}

// additional method used to check if the movie exists in the CDN
function checkMovieAvailable(movie_id){

    var xmlHttp = new XMLHttpRequest();
    xmlHttp.onreadystatechange = function() { 	
    	
        if (xmlHttp.readyState == 4){ // if successfuly sent
        	console.log("state change");
        	if(xmlHttp.status == 200){
            	showPlayer();
        	}
            else{
            	alert("Movie not found!");
            }
        }
    }

    xmlHttp.open("GET", "http://localhost:1935/streamMovie/"+movie_id, false); // true for asynchronous 
   	xmlHttp.send(null);
}
