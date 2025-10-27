import { Component, OnInit, signal } from '@angular/core';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ErrorComponent } from './error/error';

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
  errorMessage: string = '';
}