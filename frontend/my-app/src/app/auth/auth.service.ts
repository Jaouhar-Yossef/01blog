import { Injectable, Inject, PLATFORM_ID, signal } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = 'http://localhost:8080/auth';
  private isBrowser: boolean;

  loggedIn = signal<boolean>(false);
  user: any = null;

  constructor(
    private http: HttpClient,
    @Inject(PLATFORM_ID) platformId: Object
  ) {
    this.isBrowser = isPlatformBrowser(platformId);
  }

  saveAuthData(token: string, user: any) {
    if (!this.isBrowser) return;

    localStorage.setItem('token', token);
    localStorage.setItem('user', JSON.stringify(user));
    this.loggedIn.set(true);
    this.user = user;
  }

  getToken(): string | null {
    if (!this.isBrowser) return null;
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
      tap(res => {
        this.saveAuthData(res.token, res.user);
      })
    );
  }

  register(data: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/register`, data);
  }

  checkAuthServer(): Observable<any> {
    const token = this.getToken();
    if (!token) return of(null);

    return this.http.get<any>(`${this.apiUrl}/me`, {
      headers: { Authorization: `Bearer ${token}` }
    }).pipe(
      catchError(() => of(null))
    );
  }
}
