import { PLATFORM_ID, inject } from '@angular/core';

import { CanActivateFn , Router } from '@angular/router';
import { AuthService } from './auth.service';
import { isPlatformBrowser } from '@angular/common';

export const adminGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const platformId = inject(PLATFORM_ID);
  if (!isPlatformBrowser(platformId)) return true;
  const user = auth.user();
  if (!user) {
    router.navigate(['/']);
    return false;
  }

  if (user.role !== 'ADMIN') {
    router.navigate(['/home']); 
    return false;
  }

  return true;
};
