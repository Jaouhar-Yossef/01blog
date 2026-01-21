import { inject, PLATFORM_ID } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { isPlatformBrowser } from '@angular/common';
import { AuthService } from './auth.service';
import { map, catchError } from 'rxjs/operators';
import { of } from 'rxjs';

export const authGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const platformId = inject(PLATFORM_ID);

  if (!isPlatformBrowser(platformId)) {
    return true;
  }

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
};
