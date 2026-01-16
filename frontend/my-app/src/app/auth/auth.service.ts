import { Injectable, inject, Inject, PLATFORM_ID, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { catchError, map, Observable, of } from 'rxjs';
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


@Injectable({ providedIn: 'root' })
export class AuthService {
  public loggedIn = signal(false);

    public user: any = null;


  private http = inject(HttpClient);
  private platformId = inject(PLATFORM_ID);
  private apiUrl = 'http://localhost:8080/auth';



checkTokenOnServer(): Observable<boolean> {
  const token = this.getToken();
  if (!token) return of(false); 

  return this.http.get<{ valid: boolean }>(`http://localhost:8080/auth/me`, {
    headers: { Authorization: `Bearer ${token}` }
  }).pipe(
    map(response => response.valid),
    catchError(() => of(false))
  );
}




  checkAuthServer(): Observable<any> {
  const token = this.getToken();
  if (!token) return new Observable(observer => {
    observer.next(null);
    observer.complete();
  });

  return this.http.get(`${this.apiUrl}/me`, {
    headers: { Authorization: `Bearer ${token}` }
  });
}



  register(dataRegister: RegisterData): Observable<any> {
    return this.http.post(`${this.apiUrl}/register`, dataRegister);
  }

  login(dataLogin: LoginData): Observable<any> {
    return this.http.post(`${this.apiUrl}/login`, dataLogin);
  }

 

    saveAuthData(token: string, user: any) {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.setItem('token', token);
      localStorage.setItem('user', JSON.stringify(user));
    }
    this.user = user;
    this.loggedIn.set(true);
  }


  getToken(): string | null {
    if (isPlatformBrowser(this.platformId)) {
      return localStorage.getItem('token');
    }
    return null;
  }

  
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



  logout() {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
    }
    this.loggedIn.set(false);
  }

  isLoggedIn() {
    return this.loggedIn;
  }
}
