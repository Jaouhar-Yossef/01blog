import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from '../auth/auth.service';
import { AuthStore } from '../auth/auth-store.service';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';

@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {
  constructor(
    private authService: AuthService,
    private authStore: AuthStore,
    private router: Router
  ) {}

  canActivate(): Observable<boolean> {
    const token = this.authService.getToken();

    if (!token) {
      this.router.navigate(['/account']);
      return of(false);
    }

    return this.authService.checkAuthServer().pipe(
      map(user => {
        if (user) {
          this.authStore.setUser(user);
          return true;
        } else {
          this.authStore.clearUser();
          this.router.navigate(['/account']);
          return false;
        }
      }),
      catchError(() => {
        this.authStore.clearUser();
        this.router.navigate(['/account']);
        return of(false);
      })
    );
  }
}
