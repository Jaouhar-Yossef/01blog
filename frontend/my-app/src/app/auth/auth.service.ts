import { Injectable, Inject, PLATFORM_ID, signal } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { log } from 'node:console';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = 'http://localhost:8080/auth';
  private isBrowser: boolean;

  loggedIn = signal<boolean>(false);
  
  getloggedIn() {
    return this.loggedIn();
  }

  user: any = null;

  constructor(
    private http: HttpClient,
    @Inject(PLATFORM_ID) platformId: Object
  ) {
    this.isBrowser = isPlatformBrowser(platformId);

    
    if (this.isBrowser) {
      const token = localStorage.getItem('token');
      const user = localStorage.getItem('user');
      if (token && user) {
        this.loggedIn.set(true);
        this.user = JSON.parse(user);
      }
    }
  }

  saveAuthData(token: string, user: any) {
    if (!this.isBrowser) return;
    localStorage.setItem('token', token);
    localStorage.setItem('user', JSON.stringify(user));
    this.loggedIn.set(true);
    this.user = user;
  }

  getToken(): string | null {
    if (this.isBrowser) return null;
    return localStorage.getItem('token');
  }

  logout() {
    if (this.isBrowser) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
    }
    this.loggedIn.set(false);
    this.user = null;
  }

  login(data: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/login`, data).pipe(
      tap(res => this.saveAuthData(res.token, res.user))
    );
  }

  register(data: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/register`, data);
  }

  
  validateToken(): Observable<boolean> {
  // لو الـ user موجود في localStorage مسبقاً

  const token = this.getToken();

  log("Validating token called"  , this.isBrowser);



console.log("Validating token:", token);
  
  
  if (!token) return of(false);

  return this.http.post<any>(`${this.apiUrl}/validate-token`, {}, {
    headers: { Authorization: `Bearer ${token}` }
  }).pipe(
    map(user => {
      this.loggedIn.set(true);
      this.user = user;
      return true;
    }),
    catchError(() => {
      this.logout();
      return of(false);
    })
  );
}


}
