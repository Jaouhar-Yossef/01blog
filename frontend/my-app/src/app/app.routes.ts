import { Routes } from '@angular/router';

import { HomeComponent } from './home/home';
import { AuthComponent } from './auth/auth';

export const routes: Routes = [
    { path: '', component:  HomeComponent},
     { path: 'account', component: AuthComponent },
];
