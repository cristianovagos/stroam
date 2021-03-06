import { Component, OnInit, AfterViewInit, Inject } from '@angular/core';
import { DOCUMENT } from '@angular/common';
import { Router, ActivatedRoute } from '@angular/router';
import { StorageData } from '../models/storage.model';
import { User } from '../models/user.model';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';

@Component({
    selector: 'welcome',
    templateUrl: './welcome.component.html'
})

export class WelcomeComponent implements OnInit, AfterViewInit {

    user: User;
    url: string;
    sec: any;
    token: string;
    session_id: string;

    public constructor(
        @Inject(DOCUMENT) private document: any,
        private storage: StorageData,
        private http: HttpClient
    ) { }

    ngOnInit(): void {
        this.user = JSON.parse(this.storage.data).user;
        this.url = atob(JSON.parse(this.storage.data).url);
        this.session_id = JSON.parse(this.storage.data).session_id;
        this.sec = 3;
    }

    ngAfterViewInit() {
        setInterval( ()=> {
            this.sec -=  1;
            console.log(this.sec);
            if (this.sec == 0) {
                // this.http.get(this.url, {params: new HttpParams().set('id', this.user.id.toString())}
                // )
                // .subscribe( 
                //     resp => {
                //         console.log(resp);
                //     },
                //     err => {
                //         console.log(err);
                //     }
                // );
                // this.document.location.href = this.url + '?id=' + this.user.id.toString();

                this.http.post(this.url, {'user': this.user, 'session_id': this.session_id }, {responseType: 'text'})
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