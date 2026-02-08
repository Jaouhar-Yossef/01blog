import { Component, EventEmitter, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../auth/auth.service';
import { ErrorService } from '../error/error.service';
import { Router } from '@angular/router';

import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';

import { MatButtonModule } from '@angular/material/button';
import { RouterModule } from '@angular/router';


@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, MatFormFieldModule, MatButtonModule, RouterModule],
  templateUrl: './login.html',
  styleUrls: ['./login.css']
})
export class LoginComponent {
  showPassword = false;

  constructor(
    private authService: AuthService,
    private errorService: ErrorService,
    private router: Router
  ) { }

  @Output() switchForm = new EventEmitter<void>();

  onClickCircle() {
    this.showPassword = !this.showPassword;
  }

  goToRegister() {
    this.switchForm.emit();
  }

  password: string = ''
  email: string = ''

  check() {
    if (this.email.trim() == '' || this.password.trim() == '') {
      this.errorService.showMessage('All fields must be filled!', 'error');
      return
    }
    if (this.email.trim().length < 3) {
      this.errorService.showMessage('At least 3 characters!', 'error');
      return
    }

    if (this.password.trim().length < 6) {
      this.errorService.showMessage('Password must be at least 6 characters', 'error');
      return
    }

    this.fetchLogin();
  }

  fetchLogin() {
    const data = {
      emailOrUsername: this.email.trim(),
      password: this.password.trim(),
    };

    this.authService.login(data).subscribe(res => {
      if (res.success === false) {
        this.errorService.showMessage(res.message, 'error');
        return;
      }
      this.router.navigate(['/home']);
      this.errorService.showMessage('Login successful!', 'success');
    });
  }


}
