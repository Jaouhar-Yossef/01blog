import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';
import { map } from 'rxjs';

export const loggedInGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  const token = auth.getToken();
  if (!token) return true;


  const u = auth.getUser();
  if (u == null) {
    return auth.validateToken().pipe(
      map(isValid => {
        if (isValid) {
          router.navigate(['/home']);
          return false;
        }
        return true;
      })
    );
  }
  return false;
};
