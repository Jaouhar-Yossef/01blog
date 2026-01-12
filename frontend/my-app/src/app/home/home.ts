import { Component, Inject, PLATFORM_ID, effect } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Navbar } from '../navbar/navbar'; 
import { PageWelcome } from '../page-welcome/page-welcome';
import { AuthService } from '../auth/auth.service';

@Component({
  selector: 'app-home',
  standalone: true,  
  imports: [CommonModule, Navbar, PageWelcome],
  templateUrl: './home.html',
  styleUrls: ['./home.css']
})
export class HomeComponent {

  loggedIn = false;
  user: any = null;

  constructor(
    public authService: AuthService,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {
    // load user from localStorage if browser
    if (isPlatformBrowser(this.platformId)) {
      const userData = localStorage.getItem('user');
      if (userData) {
        this.user = JSON.parse(userData);
      }
    }

    // react to changes in loggedIn signal
    effect(() => {
      this.loggedIn = this.authService.isLoggedIn()(); // <- read the signal with ()
    });
  }
}
