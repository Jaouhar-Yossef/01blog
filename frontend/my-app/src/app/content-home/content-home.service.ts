import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';


@Injectable({  providedIn: 'root'})
export class ContentHomeService {

    private apiUrl = 'http://localhost:8080/blogs';

    constructor(private http: HttpClient) { }
    
    getBlogs(): Observable<any> {
        return this.http.get<any>(`${this.apiUrl}/all-blogs`);
    }

}