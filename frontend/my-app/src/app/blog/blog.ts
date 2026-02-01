import { Component, inject, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BlogUiService } from './blog-ui.service';

import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ContentHomeService } from '../content-home/content-home.service';
import { ErrorService } from '../error/error.service';

export interface CreateCommentDto {
  content: string;
  blogId: number;
}


@Component({
  selector: 'app-blog',
  standalone: true,
  imports: [
    CommonModule,
    MatButtonModule,
    MatIconModule,
    ReactiveFormsModule
  ],
  templateUrl: './blog.html',
  styleUrl: './blog.css',
})
export class Blog {
  @Input() blog!: any;

  ui = inject(BlogUiService);

  AllTheDiscription = false;
  showTheComment = false;

  commentForm: FormGroup;

  constructor( private errorService: ErrorService  , private  contentHomeService : ContentHomeService  , private fb: FormBuilder) {
    this.commentForm = this.fb.group({
      title: ['', [Validators.required, Validators.maxLength(300)]]
    });
  }

  toggleDescription() {
    this.AllTheDiscription = !this.AllTheDiscription;
  }

  showComment() {
    this.showTheComment = !this.showTheComment;

    this.contentHomeService.getComment(this.ui.selectedBlog()?.id).subscribe({

    })

  }

  submitComment() {
    if (this.commentForm.valid) {
      const payload = {
        comment: this.commentForm.value.title,
        id_blog: this.ui.selectedBlog()?.id
      };
      this.contentHomeService.creatComment(payload).subscribe({
          next: res => {
            if (!res.success) {
              this.errorService.showMessage('lj creating comment', 'error');
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
}
