import { Injectable, Inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class CardBlogService {

    private apiUrl = 'http://localhost:8080/blogs';

    constructor(
      private http: HttpClient,
    ) {
    }

    save_Blogs(id_blog: string) {
      return this.http.post(
        `${this.apiUrl}/save_blog`,{id_blog});
    }

    unsave_Blogs(id_blog: string) {
      return this.http.post(
        `${this.apiUrl}/unsave_blog`,{id_blog});
    }


    liked_Blogs(id_blog: string) {
      console.log("==> " , id_blog )  
      return this.http.post(
        `${this.apiUrl}/like_blog`,{id_blog});
    }

    unliked_Blogs(id_blog: string) {
      return this.http.post(
        `${this.apiUrl}/unlike_blog`,{id_blog});
    }

}