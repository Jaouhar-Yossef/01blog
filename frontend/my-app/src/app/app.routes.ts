import { Routes } from '@angular/router';

import { HomeComponent } from './home/home';
import { RegisterComponent } from './register/register';

export const routes: Routes = [
    { path: '', component:  HomeComponent},
     { path: 'register', component: RegisterComponent }
];
