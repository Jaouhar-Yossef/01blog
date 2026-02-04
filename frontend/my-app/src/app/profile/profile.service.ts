import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UserProfile } from '../profile/profile';

@Injectable({
    providedIn: 'root'
})
export class ProfileService {

    private baseUrl = 'http://localhost:8080/profile';

    constructor(
        private http: HttpClient,
    ) {
    }

    getProfileByUsername(username: string): Observable<UserProfile> {
        return this.http.get<any>(`${this.baseUrl}/${username}`);
    }

    follow(username: string): Observable<void> {
        return this.http.post<void>(`${this.baseUrl}/${username}/follow`, {});
    }

    unfollow(username: string): Observable<void> {
        return this.http.post<void>(`${this.baseUrl}/${username}/unfollow`, {});
    }
}
