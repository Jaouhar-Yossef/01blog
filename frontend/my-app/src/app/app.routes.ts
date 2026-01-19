import { Routes } from '@angular/router';

import { HomeComponent } from './home/home';
import { AuthComponent } from './auth/auth';
import { PageWelcome } from './page-welcome/page-welcome';
import { authGuard } from './auth/auth.guard';

export const routes: Routes = [
    { path: 'home', component:  HomeComponent , canActivate: [authGuard] },
    { path: '', component: PageWelcome   },
    { path: 'account', component: AuthComponent },
];
    