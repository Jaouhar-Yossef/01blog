import { Component, OnInit, signal } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ErrorComponent } from './error/error';
import { AuthService } from './auth/auth.service';

@Component({
  selector: 'app-root',
  standalone: true, 
  imports: [RouterModule ,  FormsModule , ErrorComponent],
   template: `
      <app-error></app-error>
      <router-outlet></router-outlet>
   `
})

export class App  implements OnInit  {
  errorMessage: string = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.authService.checkAuth(); 
  }
  
}