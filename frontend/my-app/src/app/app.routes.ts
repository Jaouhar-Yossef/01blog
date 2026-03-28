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
import { AdminComponent } from './admin/admin';
import { AdminDashboard } from './admin-dashboard/admin-dashboard';
import { UserBanned } from './auth/userBanned.guard';
import { Notifications } from './notifications/notifications';
import { EditProfile } from './edit-profile/edit-profile';


export const routes: Routes = [

  {
    path: 'home',
    component: HomeComponent,
    canActivate: [authGuard],
    children: [
      { path: '', component: BlogListComponent },
      { path: 'blogsSaved', component: BlogListComponent, data: { view: 'saved' } },
      { path: 'blog/:id', component: Blog , canActivate: [UserBanned]},
      { path: 'profile/:name', component: Profile , canActivate: [UserBanned] },
      { path: 'Users', component: Users , canActivate: [UserBanned]},
      { path: 'Search', component: Search  , canActivate: [UserBanned]},
      { path: 'Notifications', component: Notifications , canActivate: [UserBanned] },
      { path: 'blog/:id/edit', component: CreatBlog , canActivate: [UserBanned] ,  data: { view: 'editBlog' }  },
      { path: 'EditProfile', component:  EditProfile, canActivate: [UserBanned] },
      { path: 'CreatBlog', component: CreatBlog , canActivate: [UserBanned] ,  data: { view: 'creatBlog' } }
    ]
  },

  {
    path: 'admin',
    component: AdminComponent,
    canActivate: [adminGuard],
    children: [
      { path: '', component: AdminDashboard },
      { path: 'profile/:name', component: Profile },
      { path: 'blog/:id', component: Blog },
      { path: 'EditProfile', component:  EditProfile }

    ]

  },


  { path: '', component: PageWelcome, canActivate: [loggedInGuard] },
  { path: 'account', component: AuthComponent, canActivate: [loggedInGuard] },
  { path: '**', component: AuthComponent, canActivate: [loggedInGuard] },
];
