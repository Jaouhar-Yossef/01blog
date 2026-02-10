import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';
import { catchError, map, of } from 'rxjs';

export const adminGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);


  const theUser = auth.user();
  if (theUser) {
    if (theUser.role !== 'ADMIN') {
      router.navigate(['/home']);
      return false;
    }
    return true;
  }

  return auth.validateToken().pipe(
    map(isValid => {
      if (!isValid) {
        auth.logout();
        router.navigate(['/']);
        return false;
      }
      const currentUser = auth.user();
      if (!currentUser || currentUser.role !== 'ADMIN') {
        router.navigate(['/home']);
        return false;
      }

      return true;
    }),
    catchError(err => {
      auth.logout();
      router.navigate(['/']);
      return of(false);
    })
  );
};
