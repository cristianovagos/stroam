import { Component, OnInit, Inject } from '@angular/core';
import { DOCUMENT } from '@angular/common';
import { Router, ActivatedRoute } from '@angular/router';
import { HttpClient, HttpRequest, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User } from '../models/user.model';
import { StorageData } from '../models/storage.model';

@Component({
    selector: 'manager',
    templateUrl: './manager.component.html'
})

export class ManagerComponent implements OnInit {

    client_id: string;
    model: any = {};

    constructor(
        private activatedRoute: ActivatedRoute,
        private http: HttpClient,
    ) { }

    ngOnInit(): void {

        this.activatedRoute.queryParams.subscribe(params => {
            this.client_id = params['id'];
        });

        let url = 'http://localhost:3000/api/v1/user/';

        this.http.post<User>(url, {
            id: this.client_id,
        }).subscribe( 
            resp => {
                console.log(resp);
            },
            err => {
                console.log(err);
            }
        );
    }
}