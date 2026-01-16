import { Routes } from '@angular/router';

import { HomeComponent } from './home/home';
import { AuthComponent } from './auth/auth';
import { AuthGuard } from './guards/auth.guard';

export const routes: Routes = [
    { path: '', component:  HomeComponent,
        canActivate:[AuthGuard]
    },
     { path: 'account', component: AuthComponent },
];
