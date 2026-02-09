import { inject } from '@angular/core';

import { CanActivateFn , Router } from '@angular/router';
import { AuthService } from './auth.service';

export const adminGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

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
