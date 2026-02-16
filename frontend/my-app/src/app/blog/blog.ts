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
import { BehaviorSubject } from 'rxjs';
import { Comment } from '../comment/comment';
import { CardBlogService } from '../card-blog/card-blog.service';


import { MatDialog } from '@angular/material/dialog';
import { Report } from './../report/report';
import { MatMenuModule } from '@angular/material/menu';
import { AuthService } from '../auth/auth.service';
import { ServiceConfirmation } from '../service-confirmation/service-confirmation.service';

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
    MatMenuModule,
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
  creat_by: string = "";
  AllTheDiscription = false;
  loading = false;
  likedblog = false;
  iSmyBlog = false;

  videoNotPlay = true;

  baseUrl = 'http://localhost:8080';

  private blogService = inject(CardBlogService);


  constructor(private errorService: ErrorService, private confirmService: ServiceConfirmation,
    private route: ActivatedRoute, private router: Router, private authService: AuthService,
    private contentHomeService: ContentHomeService, private dialog: MatDialog) { }


  modeADMINorHOME = '';


  ngOnInit() {

    if (this.router.url.startsWith('/admin/blog')) {
      this.modeADMINorHOME = 'ADMIN'
    }

    const id = this.route.snapshot.paramMap.get('id');
    if (!id) return;
    this.id_blog = id;

    this.contentHomeService.getBlogById(id).subscribe({
      next: blog => {
        this.blogSubject.next(blog),
          this.theMedia = this.blogSubject.value.media
        this.creat_at = TimeAgo(this.blogSubject.value.creat_at);
        this.creat_by = this.blogSubject.value.createdByUsername;

        const user = this.authService.getUser();
        if (user != null && user?.username == this.creat_by) {
          this.iSmyBlog = true;
        }

      },
      error: () => {
        this.errorService.showMessage('Cannot load blog ):', 'error')
      }
    });
  }

  deleteBlog(id: string) {
    if (this.loading) return
    this.loading = true;
    if (!id || id.length <= 0) return;

    this.confirmService.open(
      'Are you sure you want to delete this blog?',
      () => {
        this.contentHomeService.deleteOneBlog(id).subscribe({
          next: (res) => {
            this.errorService.showMessage(res.message, 'success')
            this.router.navigate(['home']);
          },
          error: (err) => {
            this.errorService.showMessage("Error delete Blog", 'error')
          }
        })
      }
    )



  }

  editBlog(id: string) {
    this.router.navigate([`home/blog/${id}/edit`]);
  }


  reportBlog(id: string) {
    if (this.loading || !id || id.length <=0 ) return;

    this.loading = true;

    const dialogRef = this.dialog.open(Report, {
      width: '700px',
      data: { }
    });

    dialogRef.afterClosed().subscribe(reason => {
      if (reason) {
        if (reason.length > 200) {
          this.errorService.showMessage('Reason cannot exceed 200 characters', 'error')
        }

        if (reason.length < 5) {
          this.errorService.showMessage(' ', 'error')
        }

        this.contentHomeService.ReportUserOrBlog('BLOG', reason, id).subscribe({
          next: res => {
            if (res.success) {
              this.errorService.showMessage('Report Created', 'success')
            }
          },
          error: () => {
            this.errorService.showMessage('error report blog ):', 'error')
          }
        });
      }
    });



    this.loading = false;
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
    if (this.modeADMINorHOME == 'ADMIN') {
      this.router.navigate(['/admin/profile', creatBy]);
      return
    }
    this.router.navigate(['/home/profile', creatBy]);
  }
}
