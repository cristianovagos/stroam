import { Component, OnInit, AfterViewInit, Inject } from '@angular/core';
import { DOCUMENT } from '@angular/common';
import { Router, ActivatedRoute } from '@angular/router';
import { StorageData } from '../models/storage.model';
import { User } from '../models/user.model';

@Component({
    selector: 'welcome',
    templateUrl: './welcome.component.html'
})

export class WelcomeComponent implements OnInit, AfterViewInit {

    user: User;
    sec: any;

    public constructor(
        @Inject(DOCUMENT) private document: any,
        private storage: StorageData
    ) { }

    ngOnInit(): void {
        this.user = this.storage.data;
        this.sec = 3;
    }

    ngAfterViewInit() {
        setInterval( ()=> {
            this.sec -=  1;
            console.log(this.sec)
            if (this.sec == 0) {
                this.document.location.href = 'http://www.google.com';
            }
        }, 1000);
    }

    ngOnDestroy() {
        this.sec = 0;
    }
}