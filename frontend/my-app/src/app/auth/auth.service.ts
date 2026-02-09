import { Injectable, Inject, PLATFORM_ID, signal } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { Router } from '@angular/router';
import { ApiResponse } from '../content-home/content-home.service';

interface TheUser {
  username: string;
  imageUrl: string;
  role: string;
}


@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = 'http://localhost:8080/auth';
  private isBrowser: boolean;

  loggedIn = signal<boolean>(false);


  getloggedIn() {
    return this.loggedIn();
  }


  user = signal<TheUser | null>(null);

  constructor(
    private http: HttpClient,
    @Inject(PLATFORM_ID) platformId: Object,
    private router: Router,
  ) {
    this.isBrowser = isPlatformBrowser(platformId);


    if (this.isBrowser) {
      const token = localStorage.getItem('token');
      if (token) {
        this.loggedIn.set(true);
      }
    }
  }


  getUser(): TheUser | null {
    return this.user();
  }


  saveAuthData(user: any) {
    if (!this.isBrowser) return;

    localStorage.setItem('token', user.tokeString);

    const theUser: TheUser = {
      username: user.username,
      role: user.role,
      imageUrl: user.imageUrl,
    };

    this.loggedIn.set(true);

    this.user.set(theUser);
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
    this.user.set(null);
    this.router.navigate(['/']);
  }

  login(data: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/login`, data).pipe(
      tap(res => this.saveAuthData(res.anyData)),
      catchError(err => {
        return of({
          success: false,
          message: err.error?.message || 'Login failed'
        });
      })
    );
  }


  register(data: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/register`, data);
  }



  deleteAccount(): Observable<any> {
    return this.http.delete<any>(`${this.apiUrl}/deleteAccount`, {});
  }

  validateToken(): Observable<boolean> {
    const token = this.getToken();
    if (!token) return of(false);

    return this.http.post<ApiResponse<TheUser>>(
      `${this.apiUrl}/validate-token`,
      {}
    ).pipe(
      map((res) => {
        this.user.set(res.anyData);
        this.loggedIn.set(true);
        return true;
      }),
      catchError((err) => {
        console.error('Auth error:  ', err);
        this.user.set(null);
        this.loggedIn.set(false);
        return of(false);
      })
    );
  }

}
