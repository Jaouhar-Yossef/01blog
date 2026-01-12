import { Injectable, inject, Inject, PLATFORM_ID, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { isPlatformBrowser } from '@angular/common';

export interface RegisterData {
  username: string;
  email: string;
  password: string;
}

export interface LoginData {
  emailOrUsername: string;
  password: string;
}


// ----------------- AUTH SERVICE -----------------
@Injectable({ providedIn: 'root' })
export class AuthService {
  public loggedIn = signal(false);

    public user: any = null;


  private http = inject(HttpClient);
  private platformId = inject(PLATFORM_ID);
  private apiUrl = 'http://localhost:8080/auth';

  // ----------------- REGISTER -----------------
  register(dataRegister: RegisterData): Observable<any> {
    return this.http.post(`${this.apiUrl}/register`, dataRegister);
  }

  // ----------------- LOGIN -----------------
  login(dataLogin: LoginData): Observable<any> {
    return this.http.post(`${this.apiUrl}/login`, dataLogin);
  }

  // ----------------- SAVE AUTH DATA -----------------
 

    saveAuthData(token: string, user: any) {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.setItem('token', token);
      localStorage.setItem('user', JSON.stringify(user));
    }
    this.user = user;
    this.loggedIn.set(true);
  }


  // ----------------- GET TOKEN -----------------
  getToken(): string | null {
    if (isPlatformBrowser(this.platformId)) {
      return localStorage.getItem('token');
    }
    return null;
  }

  // ----------------- CHECK AUTH -----------------
  
  checkAuth() {
    if (!isPlatformBrowser(this.platformId)) return;

    const token = localStorage.getItem('token');
    const userData = localStorage.getItem('user');

    if (!token || !userData) {
      this.loggedIn.set(false);
      return;
    }

    this.user = JSON.parse(userData);
    this.loggedIn.set(true);
  }



  // ----------------- LOGOUT -----------------
  logout() {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
    }
    this.loggedIn.set(false);
  }

  // ----------------- IS LOGGED IN -----------------
  isLoggedIn() {
    return this.loggedIn;
  }
}
