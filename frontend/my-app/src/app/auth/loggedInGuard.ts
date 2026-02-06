import { inject, PLATFORM_ID } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { isPlatformBrowser } from '@angular/common';
import { AuthService } from './auth.service';
import { map } from 'rxjs';

export const loggedInGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const platformId = inject(PLATFORM_ID);

  if (!isPlatformBrowser(platformId)) return true;

  const token = auth.getToken();
  if (!token) return true;

  return auth.validateToken().pipe(
    map(isValid => {
      if (isValid) {
        router.navigate(['/home']);
        return false;
      }
      return true;
    })
  );
};
