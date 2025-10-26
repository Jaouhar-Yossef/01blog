import { Routes } from '@angular/router';

import { HomeComponent } from './home/home';
import { RegisterComponent } from './register/register';
import { LoginComponent } from './login/login';

export const routes: Routes = [
    { path: '', component:  HomeComponent},
     { path: 'register', component: RegisterComponent },
     { path: 'login', component: LoginComponent }
];
