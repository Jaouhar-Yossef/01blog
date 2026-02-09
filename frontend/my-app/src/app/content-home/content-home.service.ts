import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BlogMode } from '../blog-list-component/blog-list-mode';


interface Comment {
  id: number;
  comment: string;
  urlString: string;
  creatorUsername: string;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  anyData: T;
}



export function TimeAgo(dateString: string): string {
  if (!dateString) return '';
  const createdDate: Date = new Date(dateString);
  const now: Date = new Date();
  const diffMs: number = now.getTime() - createdDate.getTime();

  const diffMinutes: number = Math.floor(diffMs / (1000 * 60));
  const diffHours: number = Math.floor(diffMinutes / 60);
  const diffDays: number = Math.floor(diffHours / 24);
  const diffWeeks: number = Math.floor(diffDays / 7);
  const diffMonths: number = Math.floor(diffDays / 30);
  if (diffMinutes < 4) return 'just now';
  if (diffMinutes < 60) return `${diffMinutes} min`;
  if (diffHours < 24) return `${diffHours} h`;
  if (diffDays < 7) {
    if (diffDays == 1) {
      return `${diffDays} day`;
    }
      return `${diffDays} days`;

  } 
  if (diffWeeks < 4) return `${diffWeeks} weeks`;

  if (diffMonths == 1) {
    return `${diffMonths} month`;
  }
  return `${diffMonths} months`;
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



  getBlogs(
    page: number,
    size: number,
    mode: BlogMode,
    username?: string
  ): Observable<ApiResponse<any[]>> {

    let params = `page=${page}&size=${size}&mode=${mode}`;

    if (mode === 'profile' && username) {
      params += `&username=${username}`;
    }

    return this.http.get<ApiResponse<any[]>>(`${this.apiUrl}/blogs?${params}`);
  }


  getBlogById(blogId: string): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/blog/${blogId}`);
  }


  creatBlogs(formData: FormData): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/creat-blog`, formData);
  }



  creatComment(formData: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/creat-comment`, formData);
  }

  getComment(id_blog: string, page: number, size: number): Observable<ApiResponse<Comment[]>> {
    return this.http.get<ApiResponse<Comment[]>>(
      `${this.apiUrl}/comments/${id_blog}?page=${page}&size=${size}`
    );
  }

}