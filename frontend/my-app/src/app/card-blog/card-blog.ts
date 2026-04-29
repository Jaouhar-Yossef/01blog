import { Component, Input, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CardBlogService } from './card-blog.service';
import { ErrorService } from '../error/error.service';

import { Router } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { TimeAgo } from '../content-home/content-home.service';
import { HttpClient } from '@angular/common/http';
import { ShowAdminMessage } from '../content-home/ui.showAdminMsg.service';
import { AuthService } from '../auth/auth.service';
import { ImageService } from '../content-home/ImageService.service';


@Component({
  selector: 'app-card-blog',
  standalone: true,
  imports: [CommonModule, MatIconModule],
  templateUrl: './card-blog.html',
  styleUrls: ['./card-blog.css'],
})
export class CardBlog {
  @Input() blog!: any;

  baseUrl = 'http://localhost:8080';
  currentIndex = 0;
  intervalId: any;
  loading = false;

  creat_at = "";

  showAllOfTheBlog = false;
  isUserBanned = false;

  isblogishedden = false

  private blogService = inject(CardBlogService);
  constructor(private router: Router, private http: HttpClient, private authService: AuthService,
    private errorService: ErrorService, private showAdminMessage: ShowAdminMessage,
    public imageService: ImageService) { }

  modeADMINorHOME = '';

  ngOnInit() {
    if (this.router.url.startsWith('/admin/profile')) {
      this.modeADMINorHOME = 'ADMIN'
    }

    if (this.blog.status == "HIDDEN") {
      this.isblogishedden = true;
    }

    const user = this.authService.getUser();
    if (user != null && user?.status == "BANNED") {
      this.isUserBanned = true;
    }
    this.creat_at = TimeAgo(this.blog.creat_at);
  }


  checkImage(url: string): string {
    this.http.get(this.baseUrl + url, { responseType: 'blob' }).subscribe({
      next: () => { return this.baseUrl + url; },
      error: () => { return './../../assets/no-picture.png'; }
    });
    return "";
  }


  showTheBlog(id: string) {
    if (this.modeADMINorHOME == 'ADMIN') {
      this.router.navigate(['/admin/blog', id]);
      return
    }
    this.router.navigate(['/home/blog', id]);
  }

  toggleSave() {
    if (this.isUserBanned) {
      this.showAdminMessage.showAdminMessageUserBanned()
      return
    }
    if (this.isblogishedden) {
      this.showAdminMessage.showAdminMessageBlogHidden()
      return
    }
    if (this.loading) return;
    this.loading = true;

    const previousState = this.blog.saved;
    this.blog.saved = !previousState;

    const request$ = previousState
      ? this.blogService.unsave_Blogs(this.blog.id)
      : this.blogService.save_Blogs(this.blog.id);

    request$.subscribe({
      next: () => {
        this.errorService.showMessage(
          previousState ? 'Unsaved successfully!' : 'Saved successfully!',
          'success'
        );
        this.loading = false;
      },
      error: () => {
        this.blog.saved = previousState;
        this.loading = false;
        this.errorService.showMessage('Something went wrong 😢', 'error');
      }
    });
  }


  toggleLike() {
    if (this.isUserBanned) {
      this.showAdminMessage.showAdminMessageUserBanned()
      return
    }
    if (this.isblogishedden) {
      this.showAdminMessage.showAdminMessageBlogHidden()
      return
    }
    if (this.loading) { return }
    this.loading = true

    const previousState = this.blog.liked;
    this.blog.liked = !previousState;

    const request$ = previousState
      ? this.blogService.unliked_Blogs(this.blog.id)
      : this.blogService.liked_Blogs(this.blog.id);


    request$.subscribe({
      next: () => {
        this.errorService.showMessage(
          previousState ? 'UnLiked successfully!' : 'liked blog successfully!',
          'success'
        );
        this.loading = false;
      },

      error: (err) => {
        this.blog.liked = previousState;
        this.loading = false;
        this.errorService.showMessage('Something went wrong 😢', 'error');
      }
    });
  }


  goToProfile() {
    if (this.modeADMINorHOME == 'ADMIN') {
      this.router.navigate([`/admin/profile`, this.blog.createdByUsername]);
      return
    }
    this.router.navigate([`/home/profile`, this.blog.createdByUsername]);
  }

}
