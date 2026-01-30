import { Injectable, Inject, PLATFORM_ID, signal } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';

import { Router } from '@angular/router';

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
    @Inject(PLATFORM_ID) platformId: Object,
    private router: Router,
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

  saveAuthData(user: any) {
    if (!this.isBrowser) return;
    localStorage.setItem('token', user.tokeString); 
    localStorage.setItem('user', JSON.stringify(user));
    this.loggedIn.set(true);
    this.user = user;
  }
 
  getToken(): string | null {
    if (!this.isBrowser) return null;  
    return localStorage.getItem('token'); 
  }


  logout() {
    console.log("ggggggggg")
    if (this.isBrowser) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
    }
    this.loggedIn.set(false);
    this.user = null;
    this.router.navigate(['/home']); here
  }

  login(data: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/login`, data).pipe(
      tap(res => this.saveAuthData(res.anyData))
    );
  }

  register(data: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/register`, data);
  }

  
  validateToken(): Observable<boolean> {

  const token = this.getToken();  
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
