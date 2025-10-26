import { HttpClientModule } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-login',
      standalone: true,
  imports: [FormsModule, HttpClientModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class LoginComponent {
  showPassword = false;
  onClickCircle() {
    this.showPassword = !this.showPassword;
  }
}
