import {
  AfterViewInit,
  Component,
  ElementRef,
  inject,
  Input,
  OnInit,
  PLATFORM_ID,
  ViewChild
} from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { BehaviorSubject } from 'rxjs';

import { CardBlog } from '../card-blog/card-blog';
import { BlogUiService } from '../blog/blog-ui.service';
import { ContentHomeService } from '../content-home/content-home.service';
import { BlogMode } from '../blog-list-component/blog-list-mode';

import { MatIconModule } from '@angular/material/icon';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-blog-list-component',
  standalone: true,
  imports: [CommonModule, CardBlog, MatIconModule],
  templateUrl: './blog-list-component.html',
  styleUrl: './blog-list-component.css',
})
export class BlogListComponent implements OnInit, AfterViewInit {

  @Input() mode: BlogMode = 'home';
  @Input() username?: string;

  private blogsSubject = new BehaviorSubject<any[]>([]);
  blogs$ = this.blogsSubject.asObservable();

  page = 0;
  size = 10;
  loading = false;
  hasMore = true;

  public ui = inject(BlogUiService);
  private postService = inject(ContentHomeService);
  private platformId = inject(PLATFORM_ID);
  
  constructor(private route: ActivatedRoute) {}

  private io!: IntersectionObserver;
  @ViewChild('observer') observer!: ElementRef;

  ngOnInit() {
    if (!this.mode || this.mode == 'home') {
      const view = this.route.snapshot.data['view'];
      this.mode = view === 'saved' ? 'saved' : 'home';
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
    this.postService
      .getBlogs(this.page, this.size, this.mode, this.username)
      .subscribe({
        next: (res) => {


          this.blogsSubject.next([
            ...this.blogsSubject.value,
            ...res.anyData
          ]);

          if (res.anyData.length < this.size) {
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


  get modeClass(): string {
    return this.mode + '-style';
  }

  get modeClassforblogs(): string {
    return this.mode + '-blogsforuser-style';
  }
}
