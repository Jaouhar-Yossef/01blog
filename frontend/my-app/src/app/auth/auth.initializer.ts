import { AuthService } from './auth.service';
import { firstValueFrom } from 'rxjs';
import { inject } from '@angular/core';

export function authInitializer(): Promise<boolean> {
  const authService = inject(AuthService);
  return firstValueFrom(authService.validateToken());
}