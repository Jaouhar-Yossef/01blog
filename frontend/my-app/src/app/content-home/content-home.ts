import {
  Component,
  OnInit,
  AfterViewInit,
  ViewChild,
  ElementRef,
  inject,
  PLATFORM_ID,
} from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { BehaviorSubject } from 'rxjs';
import { ContentHomeService } from './content-home.service';
import { CardBlog } from '../card-blog/card-blog';
import { Blog } from '../blog/blog';
import { ErrorService } from '../error/error.service';

@Component({
  selector: 'app-content-home',
  standalone: true,
  imports: [CommonModule, CardBlog , Blog],
  templateUrl: './content-home.html',
  styleUrls: ['./content-home.css'],
})

export class ContentHome implements OnInit, AfterViewInit {
  constructor(
    private errorService: ErrorService,
  ) {}

  private blogsSubject = new BehaviorSubject<any[]>([]);
  blogs$ = this.blogsSubject.asObservable();

  page = 0;
  size = 10;
  loading = false;
  hasMore = true;

  @ViewChild('observer') observer!: ElementRef;

  private postService = inject(ContentHomeService);
  
  private platformId = inject(PLATFORM_ID); 

  ngOnInit() {
    this.loadNextPage();
  }

  ngAfterViewInit() {
    if (!isPlatformBrowser(this.platformId)) return;
    if (!this.observer) return;
    const io = new IntersectionObserver(entries => {
      if (
        entries[0].isIntersecting &&
        !this.loading &&
        this.hasMore   

      ) {
        this.loadNextPage();
      }
    });
    
    io.observe(this.observer.nativeElement);
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
