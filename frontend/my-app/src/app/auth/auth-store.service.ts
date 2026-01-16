import { Injectable, signal } from '@angular/core';
import { AuthService } from './auth.service';

@Injectable({ providedIn: 'root' })
export class AuthStore {
  user = signal<any>(null);
  loggedIn = signal<boolean>(false);

  constructor(private authService: AuthService) {
    this.initializeAuth();
  }

  private initializeAuth() {
    const token = this.authService.getToken();
    const userData =
      typeof window !== 'undefined'
        ? localStorage.getItem('user')
        : null;

    if (token && userData) {
      this.user.set(JSON.parse(userData));
      this.loggedIn.set(true);
      this.checkUserFromServer();
    } else {
      this.clearUser();
    }
  }

  checkUserFromServer() {
    this.authService.checkAuthServer().subscribe(user => {
      if (user) {
        this.user.set(user);
        this.loggedIn.set(true);
      } else {
        this.clearUser();
      }
    });
  }

  setUser(user: any) {
    this.user.set(user);
    this.loggedIn.set(true);
  }

  clearUser() {
    this.authService.logout();
    this.user.set(null);
    this.loggedIn.set(false);
  }
}
