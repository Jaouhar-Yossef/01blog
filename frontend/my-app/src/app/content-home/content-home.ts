import { Component, inject, PLATFORM_ID, NgZone } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { CardBlog } from '../card-blog/card-blog';
import { ContentHomeService } from './content-home.service';
import { Observable } from 'rxjs';

interface Blog {
  id: number;
  title: string;
  content: string;
  createdBy: string;
}

@Component({
  selector: 'app-content-home',
  standalone: true,
  imports: [CommonModule, CardBlog],
  templateUrl: './content-home.html',
  styleUrls: ['./content-home.css'],
})

export class ContentHome {
  
  blogs$: Observable<Blog[]>;

  private platformId = inject(PLATFORM_ID);
  private postService = inject(ContentHomeService);
  
  constructor() {
    this.blogs$ = this.postService.getBlogs();
  }

  trackById(index: number, blog: any): number {
    return blog.id; 
  }
}
