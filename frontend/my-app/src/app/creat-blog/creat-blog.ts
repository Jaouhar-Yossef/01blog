import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ErrorService } from '../error/error.service';
import { ContentHomeService } from '../content-home/content-home.service';

type MediaType = 'image' | 'video';

interface MediaFile {
  file: File;
  url: string;
  type: MediaType;
}



interface Data {
  title: string;
  content: string;
  media: MediaFile[];
}



@Component({
  selector: 'app-creat-blog',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './creat-blog.html',
  styleUrls: ['./creat-blog.css'],
})
export class CreatBlog {
  @Input() showCreatBlog = false;
  @Output() close = new EventEmitter<void>();

  form: FormGroup;

  files: MediaFile[] = [];


  constructor(private fb: FormBuilder, private errorService: ErrorService , private blogService: ContentHomeService ) {
    this.form = this.fb.group({
      title: ['', [Validators.required, Validators.maxLength(20)]],
      content: ['', [Validators.required, Validators.maxLength(1000)]],
    });
  }

  onCancel() {
    this.close.emit();
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

    const formData : Data = {
      title : this.form.value.title,
      content :  this.form.value.content,
      media : this.files
    }

     
    this.blogService.creatBlogs(formData).subscribe({
      next: res => {
        this.errorService.showMessage('Blog Created', 'success');
        this.clearForm();
      },
      error: err => {
        this.errorService.showMessage('Error creating blog', 'error');
      }
    });

    this.clearForm();
  }

  clearForm() {
    this.form.reset();
    this.files.forEach(file => URL.revokeObjectURL(file.url)); 
    this.files = [];
    
  }


}
