import { Component, OnInit, Inject } from '@angular/core';
import { Router } from '@angular/router';
import { DOCUMENT } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { StorageData } from '../models/storage.model';

@Component({
  selector: 'register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {

    model: any = {};
    
    constructor(
        @Inject(DOCUMENT) private document: any,
        public router: Router,
        private http: HttpClient,
        private storage: StorageData
    ) { 
    }

    ngOnInit(): void {
    }

    goToLogin() {
        this.router.navigate(["/login"]);
    }

    goToRegister() {
        this.router.navigate(["/register"]);
    }

    register() {
        let url = 'http://localhost:3000/api/user';

        this.http.post<any>(url, {
            name: this.model.username,
            password: this.model.password,
            email: this.model.email,
        }).subscribe( 
            resp => {
                console.log(resp);
                //this.router.navigate(['/success']);
                this.storage.data = resp;
                this.router.navigate(["/manager"]);
            },
            err => {
                console.log(err.error.status, err.error.error);
                //this.router.navigate(['/error']);
            }
        );
    }
}
