import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from '../auth/auth.service';


@Injectable({ providedIn: 'root' })
export class ContentHomeService {
  private apiUrl = 'http://localhost:8080/blogs';
  private isBrowser: boolean;

  constructor(
    private http: HttpClient,
    private authService: AuthService,
    @Inject(PLATFORM_ID) platformId: Object
  ) {
    this.isBrowser = isPlatformBrowser(platformId);
  }


  getBlogs(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/allblogs`);
  }

  creatBlogs(formData: FormData): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/creat-blog`, formData);
  }
    
}