

function streamSetup(){

	// get movie id from url, not the best approach but will do for now
	var movie_id = window.location.href.split("/");
	console.log("Movie id:" + movie_id[movie_id.length-1]);

	//checkMovieAvailable(movie_id[movie_id.length-1])

	//streamMovie(movie_id[movie_id.length-1]); // 
	streamMovie(1);

}

function showPlayer(){

    var playerDiv = document.getElementById("playerDiv");

    console.log(playerDiv);

    if (playerDiv.style.visibility  == 'hidden') {
        playerDiv.style.visibility = 'visible';
        console.log("A");
    } else {
        console.log("B");
        playerDiv.style.visibility = "hidden";
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
                console.log("SHOW PLAYER");
        	}
            else{
            	alert("Movie not found!");
            }
            showPlayer();
        }
    }

    xmlHttp.open("GET", "http://localhost:1935/streamMovie/"+movie_id, true); // true for asynchronous 
   	xmlHttp.send(null);
}

function streamMovie(movie_id){

	var player = document.getElementById("videoPlayer");

    showPlayer();

	// url for the movie
	// when "play" button is clicked the player sends a GET request method to the src URL
    // and procceed to ask for chunks of data to play
	player.src = "http://localhost:1935/streamMovie/" + movie_id;
    
    //player.play();
    //player.load();

}