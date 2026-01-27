import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class CardBlogService {

    private apiUrl = 'http://localhost:8080/blogs';
    private isBrowser: boolean;

    constructor(
      private http: HttpClient,
      @Inject(PLATFORM_ID) platformId: Object
    ) {
      this.isBrowser = isPlatformBrowser(platformId);
    }

    save_Blogs(id_blog: number) {
      return this.http.post(
        `${this.apiUrl}/save_blog`,
        id_blog);
    }

    unsave_Blogs(id_blog: number) {
      return this.http.post(
        `${this.apiUrl}/unsave_blog`,
        id_blog);
    }

}