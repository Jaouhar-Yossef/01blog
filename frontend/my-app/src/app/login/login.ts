import { Component, EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'app-login',
  standalone: true,
  templateUrl: './login.html',
  styleUrls: ['./login.css']
})
export class LoginComponent {
  showPassword = false;

  @Output() switchForm = new EventEmitter<void>();

  onClickCircle() {
    this.showPassword = !this.showPassword;
  }

  goToRegister() {
    this.switchForm.emit();
  }
}
