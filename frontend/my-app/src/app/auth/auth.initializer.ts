import { AuthService } from './auth.service';
import { firstValueFrom } from 'rxjs';

export function authInitializer(authService: AuthService) {
    return () => firstValueFrom(authService.validateToken());
}
