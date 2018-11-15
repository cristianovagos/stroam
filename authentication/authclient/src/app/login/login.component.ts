import { Component } from '@angular/core';
import { Router } from '@angular/router';

import { LoginService } from '../services/login/login.service';

@Component({
  selector: 'login',
  templateUrl: './login.component.html'
})
export class LoginComponent {

    username: string;
    password: string;
    rememberMe: boolean;
    authenticationError: boolean;

    constructor(
        private loginService: LoginService,
        private router: Router
    ){}

    login() {
        this.loginService
            .login({
                username: this.username,
                password: this.password,
                rememberMe: this.rememberMe
            })
            .then(() => {
                this.authenticationError = false;
                this.router.navigate(['welcome']);

                /* this.eventManager.broadcast({
                    name: 'authenticationSuccess',
                    content: 'Sending Authentication Success'
                }); */

                // previousState was set in the authExpiredInterceptor before being redirected to login modal.
                // since login is succesful, go to stored previousState and clear previousState
                /* const redirect = this.stateStorageService.getUrl();
                if (redirect) {
                    this.stateStorageService.storeUrl(null);
                    this.router.navigate([redirect]);
                } */
            })
            .catch(() => {
                this.authenticationError = true;
            });
    }

    register() {
        this.router.navigate(['/register']);
    }

    requestResetPassword() {
        this.router.navigate(['/resetpassword']);
        /* this.router.navigate(['/reset', 'request']); */
    }
}
