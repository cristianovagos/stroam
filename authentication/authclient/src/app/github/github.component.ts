import { Component, OnInit, Inject } from '@angular/core';
import { DOCUMENT } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { StorageData } from '../models/storage.model';
import { LOCAL_STORAGE, WebStorageService } from 'angular-webstorage-service';

@Component({
    selector: 'github',
    templateUrl: './github.component.html'
})

export class GithubComponent implements OnInit {

    client_id: string;
    name: string;
    token: string;
    sec: any;
    url: any;
    sess_id:any;

    public data: any=[];

    constructor(
        @Inject(DOCUMENT) private document: any,
        private activatedRoute: ActivatedRoute,
        private http: HttpClient,
        private storage: StorageData,
        @Inject(LOCAL_STORAGE) private session: WebStorageService
    ) {}

    ngOnInit(): void {
        this.activatedRoute.queryParams.subscribe(params => {
            this.client_id = params['id'];
            this.name = params['name'];
            this.token = params['token'];
            console.log(this.client_id);
            console.log(this.name);
            console.log(this.token);
        });
        this.sec = 3;
        console.log(this.storage.data);
        //this.url = JSON.parse(this.storage.data).url;
        //this.session_id = JSON.parse(this.storage.data).sess_id;
        this.url = this.getFromLocal("url");
        this.sess_id = this.getFromLocal("sess_id");
    }

    ngAfterViewInit() {

        console.log(this.url);
        console.log(this.client_id);
        console.log(this.name);
        console.log(this.token);
        console.log(this.sess_id);
        
        setInterval( ()=> {
            this.sec -=  1;
            
            if (this.sec == 0) {

                this.http.post<any>(this.url, {
                    "id": this.client_id,
                    "name": this.name,
                    "token": this.token,
                    "session_id": this.sess_id 
                })
                .subscribe( 
                    resp => {
                        this.document.location.href = this.url;
                    },
                    err => {
                        console.error(err);
                    }
                );
            }
        }, 1000);
    }

    ngOnDestroy() {
        this.sec = 0;
    }

    getFromLocal(key): string {
        console.log('recieved= key: ' + key);
        this.data[key]= this.session.get(key);
        console.log(this.data);
        return this.data[key];
    }
}