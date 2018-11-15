import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { LocalStorageService, SessionStorageService } from 'ngx-webstorage';

import { SERVER_URL } from 'src/app/app.constants';

@Injectable({ providedIn: 'root' })
export class AuthServerProvider {

    constructor(
        private http: HttpClient, 
        /* private $localStorage: LocalStorageService, 
        private $sessionStorage: SessionStorageService */
    ) {}

    /* getToken() {
        return this.$localStorage.retrieve('authenticationToken') || this.$sessionStorage.retrieve('authenticationToken');
    } */

    login(credentials): Observable<any> {
        const body = {
            grant_type: "password",
            username: "user",//credentials.username,
            password: "password", //credentials.password,
            //rememberMe: credentials.rememberMe,
            //client_id: "trusted-app",
            //client_secret: "secret"
        };

        const httpOptions = {
            headers: new HttpHeaders(
                { 'Content-Type': 'application/x-www-form-urlencoded', observe : 'response' }
            )
          };

        return this.http.post( 'http://localhost:3000/' +'oauth/token', body, httpOptions)
                        .pipe(map(authenticateSuccess.bind(this)));

        function authenticateSuccess(resp) {
            const bearerToken = resp.headers.get('Authorization');
            if (bearerToken && bearerToken.slice(0, 7) === 'Bearer ') {
                const jwt = bearerToken.slice(7, bearerToken.length);
                this.storeAuthenticationToken(jwt, credentials.rememberMe);
                return jwt;
            }
        }
    }

    /* loginWithToken(jwt, rememberMe) {
        if (jwt) {
            this.storeAuthenticationToken(jwt, rememberMe);
            return Promise.resolve(jwt);
        } else {
            return Promise.reject('auth-jwt-service Promise reject'); // Put appropriate error message here
        }
    }

    storeAuthenticationToken(jwt, rememberMe) {
        if (rememberMe) {
            this.$localStorage.store('authenticationToken', jwt);
        } else {
            this.$sessionStorage.store('authenticationToken', jwt);
        }
    }

    logout(): Observable<any> {
        return new Observable(observer => {
            this.$localStorage.clear('authenticationToken');
            this.$sessionStorage.clear('authenticationToken');
            observer.complete();
        });
    } */
}