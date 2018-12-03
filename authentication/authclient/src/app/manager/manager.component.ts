import { Component, OnInit, Inject } from '@angular/core';
import { DOCUMENT } from '@angular/common';
import { Router, ActivatedRoute } from '@angular/router';
import { HttpClient, HttpRequest, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User } from '../models/user.model';
import { StorageData } from '../models/storage.model';
import { switchMap } from 'rxjs/operators';

@Component({
    selector: 'manager',
    templateUrl: './manager.component.html'
})

export class ManagerComponent implements OnInit {

    model: any = {};
    gAuth: boolean;

    constructor(
        private activatedRoute: ActivatedRoute,
        public router: Router,
        private http: HttpClient,
        private storage: StorageData
    ) {
        this.gAuth = false
     }

    ngOnInit(): void {

        if (this.storage.data) {

            this.model.username = this.storage.data.name;
            this.model.email = this.storage.data.email;
            this.model.gauth2faActive = this.storage.data.gauth2faActive;
        
        } else {
            let client_id;
            this.activatedRoute.queryParams.subscribe(params => {
                client_id = params['id'];
            });
            //this.getUserFromServer(client_id);
        }
    }

    getUserFromServer(client_id: number): void {

        let url = 'http://localhost:3000/api/v1/user/';

        this.http.post<User>(url, {
            id: client_id,
        }).subscribe( 
            resp => {
                console.log(resp);
            },
            err => {
                console.log(err);
            }
        );
    }

    active2fa(): void {

        let url = "http://localhost:3000/api/v1/gauth2fa/active/"+ this.model.username

        this.http.get<any>(url).subscribe( 
            resp => {
                console.log(resp);
                this.storage.data = JSON.stringify({
                    'name': this.model.username, 
                    'key': resp.key,
                });
                this.router.navigate(["/gauth"]);
            },
            err => {
                console.log(err);
            }
        );
    }
}