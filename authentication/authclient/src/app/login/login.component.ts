import { Component, OnInit, Inject } from '@angular/core';
import { DOCUMENT } from '@angular/common';
import { Router, ActivatedRoute } from '@angular/router';
import { HttpClient, HttpRequest, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User } from '../models/user.model';
import { StorageData } from '../models/storage.model';

@Component({
    selector: 'login',
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.css']
})

export class LoginComponent implements OnInit {

    model: any = {};
    failLogin: boolean;
    urlRedirect: string;
    token: string;

    constructor(
        @Inject(DOCUMENT) private document: any,
        public router: Router,
        private activatedRoute: ActivatedRoute,
        private http: HttpClient,
        private storage: StorageData
    ) { 
        this.failLogin = false;
    }

    ngOnInit() {
        //sessionStorage.setItem('token', '');
        this.activatedRoute.queryParams.subscribe(params => {
            this.urlRedirect = params['url'];
            this.token = params['token'];
            // console.log(urlRedirect);
            // console.log(atob(urlRedirect));
        });
    }

    goToLogin() {
        this.router.navigate(["/login"]);
    }

    goToRegister() {
        this.router.navigate(["/register"]);
    }

    login() {

        let url = 'http://localhost:3000/api/v1/login';

        console.log(this.model.username, this.model.password)

        this.http.post<User>(url, {
            username: this.model.username,
            password: this.model.password
        }).subscribe( 
            resp => {
                console.log(resp);
                if (resp.id != null) {
                    console.log("nova pagina");
                    this.storage.data = JSON.stringify({
                        'user': resp, 
                        'url': this.urlRedirect,
                        'token': this.token
                    });
                    this.router.navigate(['/welcome']);

                } else {
                    console.log("fail");
                    this.failLogin = true;
                }
            },
            err => {
                console.log(err.error.status, err.error.error);
            }
        );
    }

    getAllUsers() {

        let url = 'http://localhost:3000/api/all';

        this.http.get(url).subscribe(
            res => {
                console.log(res);
            },
            err => {
                console.log(err);
            }
        );
    }
    
    facebookLogin() {
        console.log("redirect ...");
        this.document.location.href = "http://localhost:3000/login/facebook";
    }
    
    githubLogin() {
        console.log("redirect ...");
        this.document.location.href = "https://github.com/login/oauth/authorize?client_id=1a6064af05f1ade02e7b&redirect_uri=http://localhost:3000/api/v1/github;//"; //http://localhost:3000/login/github";
        /* this.http.get("https://github.com/login/oauth/authorize", { params: {}})     &scope=user%20email"
        .subscribe(
            rsp => {
                console.log(rsp);
            },
            err => {
                console.log(err);
            }
         ); */
    }
}
