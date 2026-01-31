import { Component, Inject, PLATFORM_ID } from '@angular/core';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import {isPlatformBrowser } from '@angular/common';
import { ErrorComponent } from './error/error';
import { AuthService } from './auth/auth.service';
import { ConfirmationComponent } from './service-confirmation/ConfirmationComponent'
 
@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterModule, FormsModule, ErrorComponent , ConfirmationComponent],
  templateUrl: `./app.html`,
  styleUrls: ['./app.css']
})
export class App {
  constructor( private auth: AuthService, @Inject(PLATFORM_ID) private platformId: Object ) {
    if (isPlatformBrowser(this.platformId)) {

      const token = localStorage.getItem('token');
      const user = localStorage.getItem('user');
      if (token && user) {
        this.auth.loggedIn.set(true);
        this.auth.user = JSON.parse(user);
      }
    }
  }
}
