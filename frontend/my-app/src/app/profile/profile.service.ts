import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '../content-home/content-home.service';

export type UserMode = 'AllUsers' | 'followers' | 'following';


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

export interface User {
    username: string;
    imageUrl: string;
    follower: boolean;    
    following: boolean;  
    yourProfile: boolean;
    blogsCont: number;
}



@Injectable({
    providedIn: 'root'
})
export class ProfileService {

    private baseUrl = 'http://localhost:8080/profile';
    constructor(private http: HttpClient,) { }

    getProfileByUsername(username: string): Observable<ApiResponse<UserProfile>> {
        return this.http.get<ApiResponse<UserProfile>>(`${this.baseUrl}/${username}`);
    }

    getUsers(page: number, size: number, mode: string, username: string): Observable<ApiResponse<User[]>> {

        let params = `page=${page}&size=${size}&mode=${mode}&username=${username}`;

        return this.http.get<ApiResponse<User[]>>(`${this.baseUrl}/users?${params}`)
    }

    follow(username: string): Observable<ApiResponse<any>> {
        return this.http.post<ApiResponse<any>>(`${this.baseUrl}/${username}/follow`, {});
    }

    unfollow(username: string): Observable<ApiResponse<any>> {
        return this.http.post<ApiResponse<any>>(`${this.baseUrl}/${username}/unfollow`, {});
    }
}
