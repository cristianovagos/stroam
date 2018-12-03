import { Component, OnInit, Inject } from '@angular/core';
import { DOCUMENT } from '@angular/common';
import { Router, ActivatedRoute } from '@angular/router';
import { HttpClient, HttpRequest, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User } from '../models/user.model';
import { StorageData } from '../models/storage.model';

@Component({
    selector: 'gauth',
    templateUrl: './gauth.component.html'
})

export class GauthComponent implements OnInit {

    name: string;
    key: string;
    gauthcode: any = {};
    send: boolean;
    success: boolean;

    constructor(
        private storage: StorageData,
        private http: HttpClient,
    ) {
        this.send = false;
        this.success = false;
    }

    ngOnInit(): void {

        console.log(this.storage.data);
        this.name = JSON.parse(this.storage.data).name;
        this.key = JSON.parse(this.storage.data).key;
    }

    verifycode() {
        console.log( this.gauthcode.code );

        let url = "http://localhost:3000/api/v1/gauth2fa/confrim/"+this.name +"/"+this.gauthcode.code;

        this.http.get<any>(url).subscribe(
            resp => {
                console.log(resp);
                this.send = true;
                if(resp.code == "success") {
                    this.success = true;
                
                } else if(resp.code == "fail"){
                    this.success = false;
                }
            },
            err => {
                console.log(err);
            }
        );
    }


}