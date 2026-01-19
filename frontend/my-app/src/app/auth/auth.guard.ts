import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';
import { map, catchError } from 'rxjs/operators';
import { of } from 'rxjs';
import { log } from 'node:console';

export const authGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  

  // تحقق من السيرفر
  return auth.validateToken().pipe(

    
    map(isValid => {
      console.log("hhhhhh")
      console.log(isValid );
      if (!isValid) {
        console.log("hello");
        
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
