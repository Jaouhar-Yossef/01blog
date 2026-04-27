import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { ApiResponse } from "../content-home/content-home.service";
import { log } from "console";




@Injectable({ providedIn: 'root' })
export class AdminService {
    private apiUrl = 'http://localhost:8080/admin';

    constructor(
        private http: HttpClient
    ) { }

    getAnalytics(): Observable<any> {
        return this.http.get<any>(`${this.apiUrl}/getAnalytics`);
    }

    getReports(page: number, size: number): Observable<ApiResponse<any[]>> {
        let params = `page=${page}&size=${size}`;
        return this.http.get<ApiResponse<any[]>>(`${this.apiUrl}/getRports?${params}`);
    }


    getUsers(page: number, size: number): Observable<ApiResponse<any[]>> {
        let params = `page=${page}&size=${size}`;
        return this.http.get<ApiResponse<any[]>>(`${this.apiUrl}/getUsers?${params}`);
    }

    getBlogs(page: number, size: number): Observable<ApiResponse<any[]>> {
        let params = `page=${page}&size=${size}`;
        return this.http.get<ApiResponse<any[]>>(`${this.apiUrl}/getBlogs?${params}`);
    }


    updateReportStatus(report_id: number, status: string): Observable<ApiResponse<any>> {        
        return this.http.put<ApiResponse<any>>(`${this.apiUrl}/updateReportStatus`, { report_id, status: status.toUpperCase() });
    }



    updateBlogStatus(blog_id: number, status: string): Observable<ApiResponse<any>> {
        return this.http.put<ApiResponse<any>>(`${this.apiUrl}/updateBlogStatus`, { blog_id, status });
    }

    updateUserStatus(username: string, status: string): Observable<ApiResponse<any>> {
        return this.http.put<ApiResponse<any>>(`${this.apiUrl}/updateUserStatus`, { username, status });
    }

    deleteReport(report_id: number): Observable<ApiResponse<any>> {
        return this.http.delete<ApiResponse<any>>(`${this.apiUrl}/deleteReport`, {
            body: { report_id }
        });
    }

    deleteOneBlog(blogId: string): Observable<ApiResponse<any>> {
        return this.http.delete<ApiResponse<any>>(`${this.apiUrl}/deleteblog/${blogId}`);
    }

    deleteOneUser(username: string): Observable<ApiResponse<any>> {
        return this.http.delete<ApiResponse<any>>(`${this.apiUrl}/deleteUser/${username}`);
    }
}