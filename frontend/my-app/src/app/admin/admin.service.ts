import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { ApiResponse } from "../content-home/content-home.service";




@Injectable({ providedIn: 'root' })
export class AdminService {
    private apiUrl = 'http://localhost:8080/admin';

    constructor(
        private http: HttpClient
    ) { }

    getAnalytics(): Observable<any> {
        return this.http.get<any>(`${this.apiUrl}/getAnalytics`);
    }

    getReports(page:  number ,size: number): Observable<ApiResponse<any[]>> {
        let params = `page=${page}&size=${size}`;
        return this.http.get<ApiResponse<any[]>>(`${this.apiUrl}/getRports?${params}`);
    }

}