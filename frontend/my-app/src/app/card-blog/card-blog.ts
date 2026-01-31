import { Component, Input, OnChanges, SimpleChanges, OnDestroy, NgZone, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CardBlogService } from './card-blog.service';
import { ErrorService } from '../error/error.service';
import { BlogUiService } from '../blog/blog-ui.service';
import { CreatBlogUiService } from '../creat-blog/creat-blog-ui-service';

@Component({
  selector: 'app-card-blog',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './card-blog.html',
  styleUrls: ['./card-blog.css'],
})
export class CardBlog implements OnChanges, OnDestroy {
  @Input() blog!: any;
  
  baseUrl = 'http://localhost:8080';
  currentIndex = 0;
  intervalId: any;
  loading = false;

  showAllOfTheBlog = false;

  private ui = inject(BlogUiService)
  private uiui = inject(CreatBlogUiService)
  
  private blogService = inject(CardBlogService);
  constructor(private ngZone: NgZone , private errorService: ErrorService) {}

  showTheBlog(blog : any) {
    if (this.uiui.showCreatBlogHere()) {
      this.uiui.closeCreatBlog();
    }
    this.ui.openBlog(blog);
    
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['blog'] && this.blog?.media?.length > 1 && !this.intervalId) {
      this.startSlideshow();
    }
  }

  startSlideshow() {
    this.ngZone.runOutsideAngular(() => {
      this.intervalId = setInterval(() => {
        this.ngZone.run(() => {
          this.next();
        });
      }, 3000);
    });
  }

  next() {
    if (this.blog?.media?.length) {
      this.currentIndex = (this.currentIndex + 1) % this.blog.media.length;
    }
  }

  ngOnDestroy() {
    if (this.intervalId) {
      clearInterval(this.intervalId);
    }
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

      console.log("hhhh ==>  " , this.blog.id)
        
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


}
