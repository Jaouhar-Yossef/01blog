import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthStore } from '../auth/auth-store.service';
import { AuthService } from '../auth/auth.service';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';

@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {
  constructor(
    private authStore: AuthStore,
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(): Observable<boolean> {
    const token = this.authService.getToken();

    if (!token) {
      this.router.navigate(['/account']);
      return of(false);
    }

    return this.authService.checkTokenOnServer().pipe(
      map(valid => {
        if (valid) {

            const userData = localStorage.getItem('user');
          if (userData) this.authStore.setUser(JSON.parse(userData));
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
