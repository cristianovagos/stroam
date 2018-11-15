import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {

    
    constructor(
        public router: Router,
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
    }
}
