import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ErrorService } from '../error/error.service';

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
  @Input() showCreatBlog = false;
  @Output() close = new EventEmitter<void>();

  form: FormGroup;
  files: MediaFile[] = [];

  constructor(private fb: FormBuilder, private errorService: ErrorService) {
    this.form = this.fb.group({
      title: ['', [Validators.required, Validators.maxLength(20)]],
      content: ['', [Validators.required, Validators.maxLength(1000)]],
    });
  }

  onCancel() {
    setTimeout(() => this.close.emit());
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
      this.errorService.showMessage('Please upload at least one image or video.', 'error');
      return;
    }

    console.log({
      title: this.form.value.title,
      content: this.form.value.content,
      files: this.files,
    });


    this.form.reset();
  
    this.files.forEach(file => URL.revokeObjectURL(file.url)); 
    this.files = [];

    this.close.emit();
    
    // send to backend later
  }

  clearForm() {
    this.form.reset();
    this.files.forEach(file => URL.revokeObjectURL(file.url)); 
    this.files = [];
  }


}
