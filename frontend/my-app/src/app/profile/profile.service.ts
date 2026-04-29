import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '../content-home/content-home.service';

export interface UserProfile {
    username: string;
    imageUrl: string;
    isFollower: boolean;
    isFollowing: boolean;
    isYourProfile: boolean;
    CountFollowers: number;
    CountFollowing: number;
    BlogsCont: number;
}


@Injectable({
    providedIn: 'root'
})
export class ProfileService {

    private baseUrl = 'http://localhost:8080/profile';
    private baseUrl_admin = 'http://localhost:8080/admin';
    constructor(private http: HttpClient,) { }

    getProfileByUsername(username: string): Observable<ApiResponse<UserProfile>> {
        return this.http.get<ApiResponse<UserProfile>>(`${this.baseUrl}/${username}`);
    }

     getProfileByUsernameToTheAdmin(username: string): Observable<ApiResponse<UserProfile>> {
        return this.http.get<ApiResponse<UserProfile>>(`${this.baseUrl_admin}/profile/${username}`);
    }


    follow(username: string): Observable<ApiResponse<any>> {
        return this.http.post<ApiResponse<any>>(`${this.baseUrl}/${username}/follow`, {});
    }

    unfollow(username: string): Observable<ApiResponse<any>> {
        return this.http.post<ApiResponse<any>>(`${this.baseUrl}/${username}/unfollow`, {});
    }
}
