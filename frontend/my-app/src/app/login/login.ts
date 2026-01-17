import { Component, EventEmitter, Output } from '@angular/core';
import { ErrorService } from '../error/error.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../auth/auth.service';
import { Router } from '@angular/router';


@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule ],
  templateUrl: './login.html',
  styleUrls: ['./login.css']
})
export class LoginComponent {
  showPassword = false;
  
  constructor(
    private errorService: ErrorService,
    private authService: AuthService,
     private router: Router
  ) {}

  @Output() switchForm = new EventEmitter<void>();

  onClickCircle() {
    this.showPassword = !this.showPassword;
  }

  goToRegister() {
    this.switchForm.emit();
  }

  password: string = ''
  email: string = ''
  emailRegex: RegExp = /^[^\s@]{3,}@[^\s@]{2,}\.[^\s@]{2,}$/;

  check() {
    if (this.email.trim() == '' || this.password.trim() == ''  ) {
      this.errorService.showMessage('All fields must be filled!', 'error');
      return
    }
    if (!this.emailRegex.test(this.email.trim())) {
      this.errorService.showMessage('Email must be like example@gmail.com', 'error');
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

    this.authService.login(data).subscribe({
      next: (res) => {
        this.errorService.showMessage('login successful!', 'success');        
        // this.authService.saveAuthData(res.token, res.anyData);
        // this.authService.loggedIn.set(true);

        console.log('data after login:--->   ', res);
        this.router.navigate(['/']);

      },
      error: (err) => {
        
        this.errorService.showMessage(err.error.message, 'error');
      }

    })
  }

}
