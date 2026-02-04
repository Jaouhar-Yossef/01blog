import { Component, inject, Inject, PLATFORM_ID } from '@angular/core';
import { CommonModule } from '@angular/common';

import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { ReactiveFormsModule } from '@angular/forms';
import { ContentHomeService } from '../content-home/content-home.service';
import { ErrorService } from '../error/error.service';

import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { FormsModule } from '@angular/forms';

import { ActivatedRoute, Router } from '@angular/router';
import { BehaviorSubject, catchError, map, Observable, of } from 'rxjs';
import { Comment } from '../comment/comment';
import { CardBlogService } from '../card-blog/card-blog.service';





@Component({
  selector: 'app-blog',
  standalone: true,
  imports: [
    CommonModule,
    MatButtonModule,
    MatIconModule,
    ReactiveFormsModule,
    MatInputModule,
    MatFormFieldModule,
    FormsModule,
    Comment
  ],
  templateUrl: './blog.html',
  styleUrl: './blog.css',
})

export class Blog {


  blogSubject = new BehaviorSubject<any>(null);
  blog$ = this.blogSubject.asObservable();

  id_blog: string = "";
  AllTheDiscription = false;
  loading = false;
  likedblog = false;

  baseUrl = 'http://localhost:8080';

  private blogService = inject(CardBlogService);


  constructor(private errorService: ErrorService, @Inject(PLATFORM_ID) private platformId: Object,
    private route: ActivatedRoute, private router: Router, private contentHomeService: ContentHomeService) { }



  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) return;

    this.id_blog = id;

    this.contentHomeService.getBlogById(id).subscribe({
      next: blog => this.blogSubject.next(blog),
      error: () => this.errorService.showMessage('Cannot load blog', 'error')
    });
  }


  toggleDescription() {
    this.AllTheDiscription = !this.AllTheDiscription;
  }



  toggleLike() {
    if (this.loading) return;
    const blog = this.blogSubject.value;
    if (!blog) return;

    this.loading = true;

    const previousState = blog.liked;

    this.blogSubject.next({
      ...blog,
      liked: !previousState,
      numbLiked: previousState
        ? blog.numbLiked - 1
        : blog.numbLiked + 1
    });

    const request$ = previousState
      ? this.blogService.unliked_Blogs(this.id_blog)
      : this.blogService.liked_Blogs(this.id_blog);

    request$.subscribe({
      next: () => {
        this.loading = false;
        this.errorService.showMessage(
          previousState ? 'Unliked successfully!' : 'Liked successfully!',
          'success'
        );
      },

      error: () => {
        this.blogSubject.next({
          ...blog,
          liked: previousState,
          numbLiked: blog.numbLiked
        });

        this.loading = false;
        this.errorService.showMessage('Something went wrong 😢', 'error');
      }
    });
  }

  toggleSave() {
    if (this.loading) return;

    const blog = this.blogSubject.value;
    if (!blog) return;

    this.loading = true;

    const previousState = blog.saved;

    this.blogSubject.next({
      ...blog,
      saved: !previousState
    });

    const request$ = previousState
      ? this.blogService.unsave_Blogs(this.id_blog)
      : this.blogService.save_Blogs(this.id_blog);

    request$.subscribe({
      next: () => {
        this.loading = false;
        this.errorService.showMessage(
          previousState ? 'Removed from saved' : 'Saved successfully!',
          'success'
        );
      },

      error: () => {
        // 🔁 rollback
        this.blogSubject.next({
          ...blog,
          saved: previousState
        });

        this.loading = false;
        this.errorService.showMessage('Something went wrong 😢', 'error');
      }
    });
  }




  goToProfile() {
  }
}
