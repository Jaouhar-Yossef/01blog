import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ErrorService } from '../error/error.service';
import { ContentHomeService } from '../content-home/content-home.service';
import { AuthService } from '../auth/auth.service';
import { Router } from '@angular/router';


import {MatInputModule} from '@angular/material/input';
import {MatFormFieldModule} from '@angular/material/form-field';
import {FormsModule} from '@angular/forms';

type MediaType = 'image' | 'video';

interface MediaFile {
  file: File;
  url: string;
  type: MediaType;
}


@Component({
  selector: 'app-creat-blog',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule , FormsModule, MatFormFieldModule, MatInputModule],
  templateUrl: './creat-blog.html',
  styleUrls: ['./creat-blog.css'],
})
export class CreatBlog {

  form: FormGroup;
  files: MediaFile[] = [];

  isSubmitting= false;

  constructor(private fb: FormBuilder, private errorService: ErrorService , private router: Router,
     private authService: AuthService , private blogService: ContentHomeService ) {
    this.form = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(2),  Validators.maxLength(20)]],
      content: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(1000)]],
    });
  }

  onCancel() {

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
       if (file.size > 20 * 1024 * 1024) {
        this.errorService.showMessage( 'Video too large (max 20MB)', 'warning');
        return
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
    if (this.isSubmitting) return;

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.isSubmitting = true;
   
    const formData = new FormData();
    formData.append('title', this.form.value.title);
    formData.append('content', this.form.value.content);
  
    for (let i = 0; i < this.files.length; i++) {
      const file = this.files[i];
      if (file.file.size > 20 * 1024 * 1024) {
        this.errorService.showMessage( 'Video too large (max 20MB)', 'warning');
        continue;
      }
      formData.append('files', file.file);
    }

    this.clearForm();

    this.blogService.creatBlogs(formData).subscribe({
      next: res => {
        if (!res.success) {
          this.errorService.showMessage('Error creating blog', 'error');
          this.isSubmitting = false;
          return;
        }
        this.errorService.showMessage('Blog Created (:', 'success');
        this.isSubmitting = false;
      },
      error: err => {
        this.errorService.showMessage('Error creating blog', 'error');
        this.isSubmitting = false;
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
