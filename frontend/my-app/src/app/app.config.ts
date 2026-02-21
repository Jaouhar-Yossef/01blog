import {
  ApplicationConfig,
  inject,
  provideAppInitializer,
  provideBrowserGlobalErrorListeners,
  provideZonelessChangeDetection
} from '@angular/core';

import { provideRouter } from '@angular/router';
import {
  provideHttpClient,
  withFetch,
  withInterceptors
} from '@angular/common/http';

import { routes } from './app.routes';
import { JwtInterceptor } from './auth/jwt.interceptor';
import { authInitializer } from './auth/auth.initializer';

export const appConfig: ApplicationConfig = {
  providers: [
    provideHttpClient(
      withFetch(),
      withInterceptors([JwtInterceptor])
    ),
    provideBrowserGlobalErrorListeners(),
    provideZonelessChangeDetection(),
    provideRouter(routes),
    provideAppInitializer(authInitializer)
  ]
};
