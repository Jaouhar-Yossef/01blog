import { bootstrapApplication } from '@angular/platform-browser';
import { App } from './app/app';
import { HttpClientModule } from '@angular/common/http';
import { importProvidersFrom } from '@angular/core';

bootstrapApplication(App, {
  providers: [importProvidersFrom(HttpClientModule)],
}).catch((err) => console.error(err));
