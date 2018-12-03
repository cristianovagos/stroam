import { Component, OnInit, Inject } from '@angular/core';
import { DOCUMENT } from '@angular/common';
import { Router, ActivatedRoute } from '@angular/router';
import { HttpClient, HttpRequest, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User } from '../models/user.model';
import { StorageData } from '../models/storage.model';

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
    session_id:any;

    constructor(
        @Inject(DOCUMENT) private document: any,
        private activatedRoute: ActivatedRoute,
        private http: HttpClient,
        private storage: StorageData,
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
    }

    ngAfterViewInit() {
        setInterval( ()=> {
            this.sec -=  1;
            
            if (this.sec == 0) {

                this.http.post(this.url, {
                    "id": this.client_id,
                    "name": this.name,
                    "token": this.token,
                    "session_id": this.session_id 
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
}