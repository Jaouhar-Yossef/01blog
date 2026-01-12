import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Output } from '@angular/core';
import { FormsModule } from '@angular/forms'; 
import { ErrorService } from '../error/error.service';
import { AuthService } from '../auth/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule ],
  templateUrl: './register.html',
  styleUrls: ['./register.css']
})
export class RegisterComponent {
  showPassword = false;

  @Output() switchForm = new EventEmitter<void>();


  constructor(
    private errorService: ErrorService,
    private authService: AuthService,
    private router: Router
  ) {}


  onClickCircle() {
    this.showPassword = !this.showPassword;
  }

  goToLogin() {
    this.switchForm.emit(); 
  }

    error: string = '';

    username: string = '';
  

    passWord: string = '';
    ConfirmPassWord: string = '';
    email: string = '';

    emailRegex: RegExp = /^[^\s@]{3,}@[^\s@]{2,}\.[^\s@]{2,}$/;

    isEmailValid: boolean = false;
    isusername: boolean = false;
    ispassWordValid: boolean = false
    isConfirmpassWordValid: boolean = false
    
    isfilled: boolean = false


    checkPassword() {
      if (this.passWord.length >= 6) {
        this.ispassWordValid = true;
      } else {
        this.ispassWordValid = false;
      }
      if (this.passWord != this.ConfirmPassWord) {
        this.isConfirmpassWordValid = false 
      } else {
        this.isConfirmpassWordValid = true 
      }
      this.allisfilled()
    }

    checkEmail() {
      const trimmedEmail = this.email.trim();
      if (trimmedEmail === '') {
        this.isEmailValid = false;
      } else if (!this.emailRegex.test(trimmedEmail)) {
        this.isEmailValid = false;
      } else {
        this.isEmailValid = true;
      }
      this.allisfilled()
    }


     checkUserName() {
      if (  this.username.trim().length >= 3) {
        this.isusername = true;
      } else {
        this.isusername = false;
      }
      this.allisfilled()
    }


    allisfilled() {
        if (this.isEmailValid && this.isusername && this.ispassWordValid && this.isConfirmpassWordValid) { // this.isFirstnameValid && this.isLastnameValid &&
          this.isfilled = true
        } else {
          this.isfilled = false
        }
    }
    


     check() {
      this.allisfilled()
       this.error = ''; 
      if (!this.isusername) {
        this.error += 'username must be at least 3 characters!'
      }
      if (!this.checkError()) {
        return
      } 
      if (!this.isEmailValid) {
          this.error += 'Email must be like example@gmail.com'
      }
       if (!this.checkError()) {
        return
      } 
      if (!this.ispassWordValid) {
          this.error += 'Password must be at least 6 characters'
      }

      if (!this.checkError()) {
        return
      } 

      if (!this.isConfirmpassWordValid) {
        this.error += 'Password and Confirm Password do not match';
      }

      if (!this.checkError()) {
        return
      }

      this.fetchRegister()
      
    }
    checkError (): boolean {
      if (this.error !== '') {
          this.errorService.showMessage(this.error, 'error');
          return false
      } else {
        return true
      }
    }


    
  isLoading = false;
  
  fetchRegister() {
    const data = {
      username: this.username.trim(),
      email: this.email.trim(),
      password: this.passWord.trim(),
    };

    this.isLoading = true;

    this.authService.register(data).subscribe({
      next: (res) => {
        this.isLoading = false;
        this.errorService.showMessage('Registration successful!', 'success');
        
        this.authService.saveAuthData(res.token, res.anyData);

        this.router.navigate(['/']);
      },
      error: (err) => {
        this.isLoading = false;
        this.errorService.showMessage(err.error.message, 'error');
      }
    });
  }

}
