import { inject, PLATFORM_ID } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { isPlatformBrowser } from '@angular/common';
import { AuthService } from './auth.service';
import { of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';

export const authGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const platformId = inject(PLATFORM_ID);

  // const loading = false; 

  if (!isPlatformBrowser(platformId)) return true;

  if (!auth.loggedIn()) {
    auth.logout();
    return false;
  }

  const u = auth.getUser();
  if (u == null) {
    // console.log("hhhhhhhhhhhhh")
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
