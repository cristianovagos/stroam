import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { WelcomeComponent } from './welcome/welcome.component';
import { ManagerComponent } from './manager/manager.component';
import { GithubComponent } from './github/github.component';
import { GauthComponent } from './gauth/gauth.component';

const routes: Routes = [
  { path: '', component: LoginComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'manager', component: ManagerComponent},
  { path: 'welcome', component: WelcomeComponent },
  { path: 'github', component: GithubComponent },
  { path: 'gauth', component: GauthComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
