import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'app-register',
    templateUrl: './register.html',
      standalone: true, 
      imports: [FormsModule, HttpClientModule],
      styleUrls: ['./register.css'],
})
export class RegisterComponent {

  showPassword = false;

  onClickCircle() {
    this.showPassword = !this.showPassword;
  }


  private apiUrl = 'http://localhost:8080/register';

  username: string = '';
  email!: string;

  user: { name: string; email: string } = { name: '', email: '' };

  constructor(private http: HttpClient) {}

  register() {
    this.user.name = this.username;
    this.user.email = this.email;
    this.http.post(this.apiUrl, this.user)
      .subscribe(
        response => {
          console.log('su:', response);
        },
        error => {
          console.error('error:', error);
        }
      );
  }
}
