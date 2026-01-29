import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ErrorService } from '../error/error.service';
import { ContentHomeService } from '../content-home/content-home.service';
import { AuthService } from '../auth/auth.service';
import { CreatBlogUiService } from './creat-blog-ui-service';

type MediaType = 'image' | 'video';

interface MediaFile {
  file: File;
  url: string;
  type: MediaType;
}


@Component({
  selector: 'app-creat-blog',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './creat-blog.html',
  styleUrls: ['./creat-blog.css'],
})
export class CreatBlog {

  ui = inject(CreatBlogUiService);

  form: FormGroup;
  files: MediaFile[] = [];

  constructor(private fb: FormBuilder, private errorService: ErrorService , private authService: AuthService , private blogService: ContentHomeService ) {
    this.form = this.fb.group({
      title: ['', [Validators.required, Validators.maxLength(20)]],
      content: ['', [Validators.required, Validators.maxLength(1000)]],
    });
  }

  onCancel() {
    this.ui.closeCreatBlog()
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (!input.files) return;

    const selectedFiles = Array.from(input.files);

    if (this.files.length + selectedFiles.length > 5) {
      this.errorService.showMessage('You can upload a maximum of 5 files.', 'error');
      input.value = '';
      return;
    }

    selectedFiles.forEach(file => {
       if (file.size > 50 * 1024 * 1024) {
        this.errorService.showMessage( 'Video too large (max 50MB)', 'warning');
      }
      const type: MediaType = file.type.startsWith('image') ? 'image' : 'video';
      this.files.push({
        file,
        url: URL.createObjectURL(file),
        type,
      });
    });

    input.value = '';
  }

  removeFile(index: number) {
    URL.revokeObjectURL(this.files[index].url);
    this.files.splice(index, 1);
  }

  submit() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    if (this.files.length === 0) {
      this.errorService.showMessage('You can upload images or videos.', 'warning');
    }


    const formData = new FormData();
    formData.append('title', this.form.value.title);
    formData.append('content', this.form.value.content);
  
    for (let i = 0; i < this.files.length; i++) {
      const file = this.files[i];
      if (file.file.size > 50 * 1024 * 1024) {
        this.errorService.showMessage( 'Video too large (max 50MB)', 'warning');
        continue;
      }
      formData.append('files', file.file);
    }

    this.blogService.creatBlogs(formData).subscribe({
      next: res => {
        this.errorService.showMessage('Blog Created (:', 'success');
        this.clearForm();
        this.onCancel();
      },
      error: err => {
        this.errorService.showMessage('Error creating blog', 'error');
      }
    });
  }

  clearForm() {
    this.form.reset();
    this.files.forEach(file => URL.revokeObjectURL(file.url)); 
    this.files = [];   
    this.onCancel()
  }
}
