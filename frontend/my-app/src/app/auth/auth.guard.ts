import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';
import { of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';

export const authGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);



  if (!auth.loggedIn()) {
    auth.logout();
    return false;
  }

  const u = auth.getUser();
  if (!u || u == null) {
    return auth.validateToken().pipe(
      map(isValid => {
        if (!isValid) {
          auth.logout();
          router.navigate(['/']);
          return false;
        }
        return true;
      }),
      catchError(() => {
        auth.logout();
        router.navigate(['/']);
        return of(false);
      })
    );
  }

  return true;
};
