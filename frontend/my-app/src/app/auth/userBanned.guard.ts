import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';


export const UserBanned: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (!auth.loggedIn()) {
    auth.logout();
    router.navigate(['/']);
    return false;
  }
  const theUser = auth.user();
  if (theUser) {
    if (theUser.status == 'BANNED') {
      router.navigate(['/home']);
      return false;
    }
  }

  return true;
};
