import { Component, ElementRef, inject, Input, OnInit, SimpleChanges, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BehaviorSubject } from 'rxjs';
import { CardBlog } from '../card-blog/card-blog';
import { BlogUiService } from '../blog/blog-ui.service';
import { ContentHomeService, BlogMode } from '../content-home/content-home.service';
import { MatIconModule } from '@angular/material/icon';
import { ActivatedRoute } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { ReactiveFormsModule } from '@angular/forms';
import { ErrorService } from '../error/error.service';
import { ObserveIntersectionDirective } from '../content-home/observe-intersection.directive';


@Component({
  selector: 'app-blog-list-component',
  standalone: true,
  imports: [
    CommonModule, CardBlog, MatIconModule,
    MatFormFieldModule, MatInputModule,
    MatButtonModule, ReactiveFormsModule, ObserveIntersectionDirective
  ],

  templateUrl: './blog-list-component.html',
  styleUrl: './blog-list-component.css',
})


export class BlogListComponent implements OnInit {

  @Input() mode: BlogMode = 'HOME';
  @Input() username?: string;
  @Input() search_word?: string;


  private blogsSubject = new BehaviorSubject<any[]>([]);
  blogs$ = this.blogsSubject.asObservable();

  ngOnChanges(changes: SimpleChanges) {
    if (changes['search_word']) {
      setTimeout(()=> {} , 300)
      this.page = 0;
      this.hasMore = true;
      this.blogsSubject.next([]);

      if (!this.search_word || this.search_word.trim().length === 0) {
        this.blogsSubject.next([]);
        this.hasMore = false;
      } else {
        this.loadNextPage();
      }
    }
  }


  page = 0;
  size = 10;
  loading = false;
  hasMore = true;

  public ui = inject(BlogUiService);
  private postService = inject(ContentHomeService);

  private errorService = inject(ErrorService)

  constructor(private route: ActivatedRoute) { }

  private io!: IntersectionObserver;
  @ViewChild('observer') observer!: ElementRef;

  ngOnInit() {
    if (!this.mode || this.mode == 'HOME') {
      const view = this.route.snapshot.data['view'];
      this.mode = view === 'SAVED' ? 'SAVED' : 'HOME';
    }
      this.loadNextPage() 
  }

  loadNextPage() {
    if (this.loading || !this.hasMore) return;

    if (this.mode === 'SEARCH' && (!this.search_word || this.search_word.trim().length === 0)) {
      this.blogsSubject.next([]);
      return;
    }

    this.loading = true;
    this.postService
      .getBlogs(this.page, this.size, this.mode, this.username, this.search_word?.trim())
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
        error: err => {
          this.errorService.showMessage('Error getting Blogs ):', 'error');
          this.loading = false
        }
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
