import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface RegisterData {
  username: string;
  email: string;
  password: string;
}


export interface loginData {
  emailOrUsername : string;
  password: string
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = 'http://localhost:8080/auth';

  constructor(private http: HttpClient) {}

  register(dataRegister: RegisterData): Observable<any> {
    return this.http.post(`${this.apiUrl}/register`, dataRegister);
  }

  login(dataLogin: loginData): Observable<any> {
    return this.http.post(`${this.apiUrl}/login`, dataLogin);
  }
}
