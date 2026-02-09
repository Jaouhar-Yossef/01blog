import { Routes } from '@angular/router';

import { HomeComponent } from './home/home';
import { AuthComponent } from './auth/auth';
import { PageWelcome } from './page-welcome/page-welcome';
import { Profile } from './profile/profile';
import { authGuard } from './auth/auth.guard';
import { loggedInGuard } from './auth/loggedIn.guard';

import { Blog } from './blog/blog';
import { BlogListComponent } from './blog-list-component/blog-list-component';
import { Users } from './users/users';
import { Search } from './search/search';
import { CreatBlog } from './creat-blog/creat-blog';
import { adminGuard } from './auth/admin.guard';
import { AdminComponent } from './admin-component/admin-component';


export const routes: Routes = [

  {
    path: 'home',
    component: HomeComponent,
    canActivate: [authGuard],
    canActivateChild: [authGuard],
    children: [

      { path: 'admin', component: AdminComponent, canActivate: [adminGuard], },

      { path: '', component: BlogListComponent },
      { path: 'blog/:id', component: Blog },
      { path: 'blogsSaved', component: BlogListComponent, data: { view: 'saved' } },
      { path: 'profile/:name', component: Profile },
      { path: 'Users', component: Users },
      { path: 'Search', component: Search },
      { path: 'CreatBlog', component: CreatBlog }

    ]
  },

  { path: '', component: PageWelcome, canActivate: [loggedInGuard] },
  { path: 'account', component: AuthComponent, canActivate: [loggedInGuard] },
  { path: '**', component: AuthComponent, canActivate: [loggedInGuard] },
];
