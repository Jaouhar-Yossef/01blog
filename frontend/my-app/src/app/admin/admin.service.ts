import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";




@Injectable({ providedIn: 'root' })
export class AdminService {
    private apiUrl = 'http://localhost:8080/admin';

    constructor(
        private http: HttpClient
    ) { }

    getAnalytics(): Observable<any> {
        return this.http.get<any>(`${this.apiUrl}/getAnalytics`);
    }

}