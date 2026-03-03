import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export type BlogMode = 'HOME' | 'PROFILE' | 'SAVED';
export type UserMode = 'ALLUSERS' | 'FOLLOWERS' | 'FOLLOWING';

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


interface ReportBlogOrUser {
  type: string;
  reason: string;
  reportedBlog?: string;
  reportedUser?: string;
}




export interface User {
  username: string;
  imageUrl: string;
  follower: boolean;
  following: boolean;
  yourProfile: boolean;
  blogsCont: number;
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
  if (diffWeeks < 4) return `${diffWeeks} w`;

  if (diffMonths == 1) {
    return `${diffMonths} month`;
  }
  return `${diffMonths} months`;
}



@Injectable({ providedIn: 'root' })
export class ContentHomeService {

  private apiUrl = 'http://localhost:8080/blogs';
  private apiUrlReport = 'http://localhost:8080/Report';
  private apiUrlNotification = 'http://localhost:8080/Notifications';
    private apiUrlUsers = 'http://localhost:8080/users';
  constructor(
    private http: HttpClient,

  ) {
  }

  deleteOnenotification(Id: number): Observable<ApiResponse<any>> {
    return this.http.delete<ApiResponse<any>>(`${this.apiUrlNotification}/deleteOneNotification/${Id}`);
  }

  getNotification(page: number, size: number): Observable<ApiResponse<any>> {
    let params = `page=${page}&size=${size}`;
    return this.http.get<ApiResponse<any>>(`${this.apiUrlNotification}/getNotifications?${params}`)
  }

  deleteOneBlog(blogId: string): Observable<ApiResponse<any>> {
    return this.http.delete<ApiResponse<any>>(`${this.apiUrl}/deleteblog/${blogId}`);
  }

  ReportUserOrBlog(type: string, reason: string, username_or_blogId: string): Observable<ApiResponse<any>> {
    let data: ReportBlogOrUser;
    if (type == 'USER') {
      data = {
        type: type,
        reason: reason,
        reportedUser: username_or_blogId,
      }
    } else {
      data = {
        type: type,
        reason: reason,
        reportedBlog: username_or_blogId,
      }
    }
    return this.http.post<ApiResponse<any[]>>(`${this.apiUrlReport}/creat`, data);
  }



  getBlogs(
    page: number,
    size: number,
    mode: BlogMode,
    username?: string
  ): Observable<ApiResponse<any[]>> {

    let params = `page=${page}&size=${size}&mode=${mode}`;

    if (mode === 'PROFILE' && username) {
      params += `&username=${username}`;
    }
    return this.http.get<ApiResponse<any[]>>(`${this.apiUrl}/blogs?${params}`);
  }


  getBlogById(blogId: string): Observable<ApiResponse<any>> {
    return this.http.get<ApiResponse<any>>(`${this.apiUrl}/blog/${blogId}`);
  }


  creatBlogs(formData: FormData): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/creat-blog`, formData);
  }

  updateBlogs(formData: FormData): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/update-blog`, formData);
  }



  creatComment(formData: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/creat-comment`, formData);
  }

  getComment(id_blog: string, page: number, size: number): Observable<ApiResponse<Comment[]>> {
    return this.http.get<ApiResponse<Comment[]>>(
      `${this.apiUrl}/comments/${id_blog}?page=${page}&size=${size}`
    );
  }

  getUsers(page: number, size: number, mode: string, username: string): Observable<ApiResponse<User[]>> {
    let params = `page=${page}&size=${size}&mode=${mode}&username=${username}`;
    return this.http.get<ApiResponse<User[]>>(`${this.apiUrlUsers}/gettingUsers?${params}`)
  }

}