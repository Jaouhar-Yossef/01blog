import { Component, ElementRef, Inject, inject, Input, PLATFORM_ID, ViewChild } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';

import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ContentHomeService } from '../content-home/content-home.service';
import { ErrorService } from '../error/error.service';
import { BehaviorSubject, catchError, Observable, of } from 'rxjs';

import {MatInputModule} from '@angular/material/input';
import {MatFormFieldModule} from '@angular/material/form-field';
import {FormsModule} from '@angular/forms';

import { ActivatedRoute, Router } from '@angular/router';
import { BlogUiService } from './blog-ui.service';

export interface CreateCommentDto {
  content: string;
  blogId: number;
}


interface Comment {
  id: number;
  comment: string;
  urlString: string;
  creatorUsername: string;
}

interface ApiResponse {
  success: boolean;
  message: string;
  anyData: Comment[];
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
    FormsModule
  ],
  templateUrl: './blog.html',
  styleUrl: './blog.css',
})

export class Blog {

  blog$!: Observable<any>;
  
  id_blog :  string = "";

  AllTheDiscription = false;
  showTheComment = false;

  private CommentSubject = new BehaviorSubject<any[]>([]);
  comments$ = this.CommentSubject.asObservable();
  
  page = 0;
  size = 10;
  loading = false;
  hasMore = true;

    private isBrowser: boolean;

  
  commentForm: FormGroup;

  
  constructor( private errorService: ErrorService ,  @Inject(PLATFORM_ID) private platformId: Object,
    private route: ActivatedRoute ,private router: Router, private  contentHomeService : ContentHomeService  , private fb: FormBuilder) {
      this.commentForm = this.fb.group({
        title: ['', [Validators.required, Validators.maxLength(300)]]
      });
      this.isBrowser = isPlatformBrowser(this.platformId);
    }
    

    ngOnInit() {
      if (!this.isBrowser) return;
  
      const id = this.route.snapshot.paramMap.get('id');
  
      if (!id) {
        this.errorService.showMessage('Blog ID not found', 'error');
        this.router.navigate(['/home']);
        return;
      }
  
      this.id_blog = id;

      this.blog$ = this.contentHomeService.getBlogById(id).pipe(
        catchError(err => {
          this.errorService.showMessage('Cannot load blog', 'error');
          return of(null); 
        })
      );

      this.loadNextPage()

    }



  toggleDescription() {
    this.AllTheDiscription = !this.AllTheDiscription;
  }





  private io!: IntersectionObserver;

  @ViewChild('observer') observer!: ElementRef;

  ngAfterViewInit() {
    if (!isPlatformBrowser(this.platformId)) return;
     if (!this.observer) return;
    this.io = new IntersectionObserver(entries => {
      if (
        entries[0].isIntersecting && !this.loading && this.hasMore
      ) { this.loadNextPage();}
    });
  
    this.io.observe(this.observer.nativeElement);
  }
  

  loadNextPage() {

    this.contentHomeService.getComment( this.id_blog ,this.page , this.size).subscribe({
      next: (res : ApiResponse) => {
        
        this.CommentSubject.next([
          ...this.CommentSubject.value,
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
    })

  }


  submitComment() {
    if (this.commentForm.valid) {
      const payload = {
        comment: this.commentForm.value.title,
        id_blog: this.id_blog
      };
      this.contentHomeService.creatComment(payload).subscribe({
          next: res => {
            if (!res.success) {
              this.errorService.showMessage('Error creating comment', 'error');
              return;
            }
            console.log("==> " , res )
            this.errorService.showMessage('comment Created (:', 'success');
          },
          error: err => {
            this.errorService.showMessage('Error creating comment', 'error');
          }
      })

      this.commentForm.reset();
    }
  }



  trackById(_: number, comment: any) {
    return comment.id;
  }


}
