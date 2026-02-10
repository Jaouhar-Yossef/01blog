import { Component, inject, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';

import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { ReactiveFormsModule } from '@angular/forms';
import { ContentHomeService, TimeAgo } from '../content-home/content-home.service';
import { ErrorService } from '../error/error.service';

import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { FormsModule } from '@angular/forms';

import { ActivatedRoute, Router } from '@angular/router';
import { BehaviorSubject, catchError, map, Observable, of } from 'rxjs';
import { Comment } from '../comment/comment';
import { CardBlogService } from '../card-blog/card-blog.service';


export interface TheMediaBlog {
  url: string;
  filename: string;
  type: string;
}


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


  theMedia: TheMediaBlog[] = [];

  currentIndex = 0;

  id_blog: string = "";
  creat_at: string = "";
  AllTheDiscription = false;
  loading = false;
  likedblog = false;

  videoNotPlay = true;

  baseUrl = 'http://localhost:8080';

  private blogService = inject(CardBlogService);


  constructor(private errorService: ErrorService,
    private route: ActivatedRoute, private router: Router,
    private contentHomeService: ContentHomeService) { }



  ngOnInit() {
    if (this.loading) return;
    this.loading = true
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) return;

    this.id_blog = id;

    this.contentHomeService.getBlogById(id).subscribe({
      next: blog => {
        this.blogSubject.next(blog),
          this.theMedia = this.blogSubject.value.media
        this.creat_at = TimeAgo(this.blogSubject.value.creat_at);
      },
      error: () => {
        this.errorService.showMessage('Cannot load blog ):', 'error')
      }
    });
    this.loading = false
  }

  toggleVideo(event: Event) {
    const video = event.target as HTMLVideoElement;

    if (video.paused) {
      video.play();
      this.videoNotPlay = false;
    } else {
      this.videoNotPlay = true;
      video.pause();
    }
  }

  addCurrentIndex() {
    this.videoNotPlay = true;
    if (this.theMedia.length - 1 == this.currentIndex) {
      this.currentIndex = 0;
      return;
    }
    this.currentIndex++;
  }

  minusCurrentIndex() {
    this.videoNotPlay = true;
    if (this.currentIndex == 0) {
      return;
    }
    this.currentIndex--;
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
          previousState ? 'Unliked successfully! (:' : 'Liked successfully! (:',
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
        this.errorService.showMessage('Something went wrong ):', 'error');
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
          previousState ? 'Removed from saved (:' : 'Saved successfully! (:',
          'success'
        );
      },

      error: () => {
        this.blogSubject.next({
          ...blog,
          saved: previousState
        });

        this.loading = false;
        this.errorService.showMessage('Something went wrong ):', 'error');
      }
    });
  }

  goToProfile(creatBy: string) {
    this.router.navigate(['/home/profile', creatBy]);
  }
}
