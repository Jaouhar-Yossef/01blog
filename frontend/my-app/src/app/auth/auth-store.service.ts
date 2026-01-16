import { Injectable, effect, signal } from '@angular/core';
import { AuthService } from './auth.service';


@Injectable({ providedIn: 'root' })
export class AuthStore {
  user = signal<any>(null);
  loggedIn = signal<boolean>(false);

  constructor(private authService: AuthService) {
    // Try to read from localStorage first
    const token = authService.getToken();
    if (token) {
      this.checkUserFromServer();
    }
  }

  checkUserFromServer() {
    this.authService.checkAuthServer().subscribe({
      next: (user) => {
        if (user) {
          this.user.set(user);
          this.loggedIn.set(true);
        } else {
          this.user.set(null);
          this.loggedIn.set(false);
        }
      },
      error: (err) => {
        console.log('Auth check failed:', err);
        this.user.set(null);
        this.loggedIn.set(false);
      }
    });
  }

  setUser(user: any) {
    this.authService.user = user;
    this.authService.loggedIn.set(true);
    this.user.set(user);
    this.loggedIn.set(true);
  }

  clearUser() {
    this.authService.logout();
    this.user.set(null);
    this.loggedIn.set(false);
  }
}
