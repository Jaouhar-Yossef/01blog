import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';


export const authGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  const authService = inject(AuthService)

  const u = authService.getUser();

  console.log("==> dsf => ", u)

  if (!auth.loggedIn()) {
    auth.logout();
    router.navigate(['/']);
    return false;
  }

  return true;
};
