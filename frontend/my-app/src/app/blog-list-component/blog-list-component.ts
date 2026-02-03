import { AfterViewInit, Component, ElementRef, inject, OnInit, PLATFORM_ID, ViewChild } from '@angular/core';

import { ErrorService } from '../error/error.service';
import { BehaviorSubject } from 'rxjs';
import { BlogUiService } from '../blog/blog-ui.service';
import { ContentHomeService } from '../content-home/content-home.service';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { CardBlog } from '../card-blog/card-blog';
import { Router } from '@angular/router';

@Component({
  selector: 'app-blog-list-component',
  imports: [CommonModule,CardBlog],
  templateUrl: './blog-list-component.html',
  styleUrl: './blog-list-component.css',
})
export class BlogListComponent implements OnInit, AfterViewInit{
  
  constructor(
    private errorService: ErrorService,
    private router: Router
  ) {}

  private blogsSubject = new BehaviorSubject<any[]>([]);
  blogs$ = this.blogsSubject.asObservable();

  page = 0;
  size = 10;
  loading = false;
  hasMore = true;
  JustOneBlog = false;

  public ui = inject(BlogUiService)
  private io!: IntersectionObserver;
  @ViewChild('observer') observer!: ElementRef;
  private postService = inject(ContentHomeService);
  private platformId = inject(PLATFORM_ID);

  ngOnInit() {
    if (this.router.url === '/home') {
      
    }

    if (this.router.url === '/home/blogsSaved') {
      
    }
    this.loadNextPage();
  }


  ngAfterViewInit() {
    if (!isPlatformBrowser(this.platformId)) return;

    this.io = new IntersectionObserver(entries => {
      if (
        entries[0].isIntersecting &&
        !this.loading &&
        this.hasMore
      ) {
        this.loadNextPage();
      }
    });
    this.io.observe(this.observer.nativeElement);
  }

  loadNextPage() {
    if (this.loading || !this.hasMore) return;
    this.loading = true;
    this.postService.getBlogs(this.page, this.size).subscribe({
      next: (data) => {
        this.blogsSubject.next([
          ...this.blogsSubject.value,
          ...data
        ]);

        if (data.length < this.size) {
          this.hasMore = false;
        } else {
          this.page++;
        }

        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  trackById(_: number, blog: any) {
    return blog.id;
  }
}
