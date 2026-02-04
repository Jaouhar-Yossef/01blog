import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
// import { AuthService } from '../auth/auth.service';


interface Comment {
  id: number;
  comment: string;
  urlString: string;
  creatorUsername: string;
}

interface ApiResponse {
  success: boolean;
  message: string;
  anyData: Comment[];
}

@Injectable({ providedIn: 'root' })
export class ContentHomeService {
  private apiUrl = 'http://localhost:8080/blogs';
  private isBrowser: boolean;

  constructor(
    private http: HttpClient,
    
    @Inject(PLATFORM_ID) platformId: Object
  ) {
    this.isBrowser = isPlatformBrowser(platformId);
  }


  getBlogs(page: number, size: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/blogs?page=${page}&size=${size}`);
  }


  getBlogById(blogId : string): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/blog/${blogId}`);
  }


  creatBlogs(formData: FormData): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/creat-blog`, formData);
  }
    


  creatComment(formData: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/creat-comment`, formData);
  }

  getComment(id_blog: string, page: number, size: number) {
    return this.http.get<ApiResponse>(`${this.apiUrl}/comments/${id_blog}?page=${page}&size=${size}`);
  }
}