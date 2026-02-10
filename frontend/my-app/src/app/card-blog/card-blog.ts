import { Component, Input, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CardBlogService } from './card-blog.service';
import { ErrorService } from '../error/error.service';

import { Router } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { TimeAgo } from '../content-home/content-home.service';


@Component({
  selector: 'app-card-blog',
  standalone: true,
  imports: [CommonModule , MatIconModule],
  templateUrl: './card-blog.html',
  styleUrls: ['./card-blog.css'],
})
export class CardBlog  {
  @Input() blog!: any;
  
  baseUrl = 'http://localhost:8080';
  currentIndex = 0;
  intervalId: any;
  loading = false;

  creat_at = "";

  showAllOfTheBlog = false;

  
  private blogService = inject(CardBlogService);
  constructor(private router: Router , private errorService: ErrorService) {}


  ngOnInit() {
    this.creat_at = TimeAgo(this.blog.creat_at);
  }

  showTheBlog(id : string) {
    this.router.navigate(['/home/blog', id]);
  }

  

  toggleSave() {
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
    if (this.loading) {return}
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
    this.router.navigate([`/home/profile` , this.blog.createdByUsername]);
  }

}
