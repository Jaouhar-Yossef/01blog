import { Component, effect, Inject, OnInit, PLATFORM_ID, signal } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ErrorComponent } from './error/error';
import { AuthService } from './auth/auth.service';
import { isPlatformBrowser } from '@angular/common';
import { AuthStore } from './auth/auth-store.service';

@Component({
  selector: 'app-root',
  standalone: true, 
  imports: [RouterModule ,  FormsModule , ErrorComponent],
   template: `
      <app-error></app-error>
      <router-outlet></router-outlet>
   `
})

export class App {
    constructor(public authStore: AuthStore) {}
}