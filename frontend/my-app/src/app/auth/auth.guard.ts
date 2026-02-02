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

  if (!isPlatformBrowser(platformId)) return true;

   if (auth.loggedIn()) {
    return true;
  }

  return auth.validateToken().pipe(
    map(isValid => {
      if (!isValid) {
        router.navigate(['/']);
        return false;
      }
      return true;
    })
  );
};
