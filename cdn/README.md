
### About
Node/WebRTC based server allowing users to obtain the desired cdn contents (movies/series) via video/audio streamming


### API specification

| Method        | Call           | Header |Description  | Parameters |
| ------------- | :------------: | :----------: | :----------: | :--------- |
| GET    | /streamMovie/<id> | **content-type:** <br> | Movie request made by the client of movie identified by specified id | **id** movie id


## Execution 
$ node server.js