import { Component, ElementRef, Input, ViewChild } from '@angular/core';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ErrorService } from '../error/error.service';
import { ContentHomeService } from '../content-home/content-home.service';
import { BehaviorSubject } from 'rxjs';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { ApiResponse } from '../content-home/content-home.service';
import { ObserveIntersectionDirective } from '../content-home/observe-intersection.directive';
import { AuthService } from '../auth/auth.service';
import { ShowAdminMessage } from '../content-home/ui.showAdminMsg.service';
import { ImageService } from '../content-home/ImageService.service';


export interface CreateCommentDto {
  content: string;
  blogId: number;
}

interface comment {
  id: number;
  comment: string;
  urlString: string;
  creatorUsername: string;
}


@Component({
  selector: 'app-comment',
  standalone: true,
  imports: [
    CommonModule,
    MatButtonModule,
    MatIconModule,
    ReactiveFormsModule,
    MatInputModule,
    MatFormFieldModule,
    FormsModule,
    ObserveIntersectionDirective
  ],
  templateUrl: './comment.html',
  styleUrl: './comment.css',
})
export class Comment {
  id_blog: string = "";

  @Input() statuBlog: string = "";

  private CommentSubject = new BehaviorSubject<any[]>([]);
  comments$ = this.CommentSubject.asObservable();


  page = 0;
  size = 10;
  loading = false;
  hasMore = true;

  isUserBanned = false;

  commentForm: FormGroup;

  baseUrl = 'http://localhost:8080';

  constructor(private errorService: ErrorService,
    private route: ActivatedRoute, private router: Router,
    private contentHomeService: ContentHomeService,
    private authService: AuthService,
    private showAdminMessage: ShowAdminMessage,
    public imageService : ImageService,
    private fb: FormBuilder) {
    this.commentForm = this.fb.group({
      comment: ['', [Validators.required, Validators.maxLength(300), Validators.minLength(1)]]
    });
  }


  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) {
      this.errorService.showMessage('Blog ID not found', 'error');
      this.router.navigate(['/home']);
      return;
    }
    this.id_blog = id;
    const user = this.authService.getUser();
    if (user != null && user?.status == "BANNED") {
      this.isUserBanned = true;
    }
  }

  loadComment() {
    if (this.loading || !this.hasMore) return;
    this.loading = true;
    this.contentHomeService.getComment(this.id_blog, this.page, this.size)
      .subscribe({
        next: (res: ApiResponse<comment[]>) => {
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
        error: () => {
          this.errorService.showMessage('Error getting comments', 'error')
          this.loading = false
        }
      });

  }

  submitComment() {
    if (this.isUserBanned) {
      this.showAdminMessage.showAdminMessageUserBanned()
      this.commentForm.reset();
      return
    }
    if (this.statuBlog == "HIDDEN") {
      this.showAdminMessage.showAdminMessageBlogHidden()
      this.commentForm.reset();
      return;
    }
    if (this.loading) return;
    this.loading = true;
    if (this.commentForm.valid) {
      const payload = {
        comment: this.commentForm.value.comment,
        id_blog: this.id_blog
      };

      this.contentHomeService.creatComment(payload).subscribe({
        next: res => {
          if (!res.success) {
            this.errorService.showMessage('Error creating comment', 'error');
            return;
          }

          const newComment = res.anyData;

          this.CommentSubject.next([
            newComment,
            ...this.CommentSubject.value
          ]);
          this.loading = false;
          this.errorService.showMessage('comment Created (:', 'success');
        },
        error: err => {
          this.loading = false;
          this.errorService.showMessage('Error creating comment', 'error');
        }
      })

      this.commentForm.reset();
    }
    this.loading = false;
  }

  trackById(_: number, comment: any) {
    return comment.id;
  }

  goToProfile(creatBy: string) {
    if (this.loading) return;
    this.router.navigate(['/home/profile', creatBy]);
  }
}
