import { Component, OnInit, Inject } from '@angular/core';
import { Router } from '@angular/router';
import { DOCUMENT } from '@angular/common';
import { HttpClient } from '@angular/common/http';

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
        console.log("register");
        console.log(this.model)

        let url = 'http://localhost:3000/api/user';

        this.http.post(url, {
            name: this.model.username,
            password: this.model.email,
            email: this.model.password,
        }).subscribe( 
            resp => {
                console.log(resp);
                //this.router.navigate(['/success']);
                this.document.location.href = "http://google.com";
            },
            err => {
                console.log(err.error.status, err.error.error);
                //this.router.navigate(['/error']);
            }
        );
    }
}
