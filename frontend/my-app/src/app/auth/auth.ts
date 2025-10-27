import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RegisterComponent } from '../register/register';
import { LoginComponent } from '../login/login';

@Component({
  selector: 'app-auth',
  standalone: true,
  imports: [CommonModule, RegisterComponent, LoginComponent],
  templateUrl: './auth.html',
  styleUrls: ['./auth.css'],
})
export class AuthComponent {
  showLogin = false;

  toggleForm() {
    this.showLogin = !this.showLogin;
  }
}
