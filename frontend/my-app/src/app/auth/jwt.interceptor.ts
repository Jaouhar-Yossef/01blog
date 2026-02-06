import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from './auth.service';
import { catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';
import { ErrorService } from '../error/error.service';

export const JwtInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const errorService = inject(ErrorService);
  const router = inject(Router);

  const token = auth.getToken();
  let authReq = req;

  if (token) {
    authReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`,
      },
    });
  }

  return next(authReq).pipe(
    catchError(err => {
      if (err.status === 401) {
        const message =
          err?.error?.message ||
          err?.message ||
          'Session expired. Please login again.';

        errorService.showMessage(message, 'error');
        auth.logout();
        router.navigate(['/']);
      }

      return throwError(() => err);
    })
  );
};
