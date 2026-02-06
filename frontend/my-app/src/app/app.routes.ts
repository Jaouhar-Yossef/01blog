import { Routes } from '@angular/router';

import { HomeComponent } from './home/home';
import { AuthComponent } from './auth/auth';
import { PageWelcome } from './page-welcome/page-welcome';
import { Profile } from './profile/profile';
import { authGuard } from './auth/auth.guard';
import { loggedInGuard } from './auth/loggedInGuard';

import { Blog } from './blog/blog';
import { BlogListComponent } from './blog-list-component/blog-list-component';


export const routes: Routes = [
    
    {
      path: 'home',
      component: HomeComponent,
      canActivate: [authGuard],
      children: [
     
            { path: '', component: BlogListComponent , canActivate: [authGuard] },
            { path: 'blog/:id', component: Blog , canActivate: [authGuard]},
            { path: 'blogsSaved', component: BlogListComponent , canActivate: [authGuard] ,  data: { view: 'saved' } },
            { path: 'profile/:name', component: Profile , canActivate: [authGuard] }
      ]
    },




    { path: '', component: PageWelcome, canActivate: [loggedInGuard] },

    { path: 'account', component: AuthComponent, canActivate: [loggedInGuard] },
    { path: '**', component: AuthComponent , canActivate: [loggedInGuard] },
];
    