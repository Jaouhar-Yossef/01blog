import { Routes } from '@angular/router';

import { HomeComponent } from './home/home';
import { AuthComponent } from './auth/auth';
import { PageWelcome } from './page-welcome/page-welcome';
import { authGuard } from './auth/auth.guard';
import { loggedInGuard } from './auth/loggedInGuard';

export const routes: Routes = [
    // { path: 'home', component:  HomeComponent , canActivate: [authGuard] },
    
    { path: 'home', component:  HomeComponent },

    { path: '', component: PageWelcome, canActivate: [loggedInGuard] },

    { path: 'account', component: AuthComponent, canActivate: [loggedInGuard] },
];
    