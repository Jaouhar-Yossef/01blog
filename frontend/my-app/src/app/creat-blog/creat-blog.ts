import { Component , Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ErrorService } from '../error/error.service';
import { ContentHomeService } from '../content-home/content-home.service';
import { AuthService } from '../auth/auth.service';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { FormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { Router } from '@angular/router';
import { MgsAdhmin } from '../mgs-admin/mgs-admin';
import { MatDialog } from '@angular/material/dialog';


type MediaType = 'IMAGE' | 'VIDEO';

interface MediaFile {
  file: File;
  url: string;
  type: MediaType;
}

interface MediaOldFile {
  fileName: string;
  type: MediaType;
  url: string;
}


@Component({
  selector: 'app-creat-blog',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule,
    MatIconModule, FormsModule, MatFormFieldModule, MatInputModule],
  templateUrl: './creat-blog.html',
  styleUrls: ['./creat-blog.css'],
})
export class CreatBlog {

  @Input() mode = '';

  ModeEditblog = false;

  baseUrl = 'http://localhost:8080';


  form: FormGroup;
  files: MediaFile[] = [];
  imgsandvids: MediaOldFile[] = [];

  isSubmitting = false;

  constructor(private fb: FormBuilder, private errorService: ErrorService, private router: Router, private contentHomeService: ContentHomeService,
    private dialog: MatDialog, private authService: AuthService, private blogService: ContentHomeService, private route: ActivatedRoute) {
    this.form = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(50)]],
      content: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(2000)]],
    });
  }

  blog_id: string = '';

  ngOnInit() {
    const view = this.route.snapshot.data['view'];
    if (view == "editBlog") {
      this.ModeEditblog = true;
      const id = this.route.snapshot.paramMap.get('id');
      if (!id) return;
      this.blog_id = id;
      this.contentHomeService.getBlogById(id).subscribe({
        next: res => {
          this.form.patchValue({
            title: res.anyData.title,
            content: res.anyData.content,
          });
          this.imgsandvids = res.anyData.media;
        },
        error: () => {
          this.errorService.showMessage('Cannot load blog ):', 'error')
        }
      });
    }

  }

  showAdminMessage() {
    const dialogRef = this.dialog.open(MgsAdhmin, {
      width: '400px',
      disableClose: true,
      data: { message: "You are banned from this platform. You can't perform any actions." }
    });
  }

  goback() {

  }

  onCancel() {

  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (!input.files) return;

    const selectedFiles = Array.from(input.files);

    if (this.files.length + this.imgsandvids.length + selectedFiles.length > 5) {
      this.errorService.showMessage('You can upload a maximum of 5 files.', 'error');
      input.value = '';
      return;
    }


    selectedFiles.forEach(file => {
      if (file.size > 20 * 1024 * 1024) {
        this.errorService.showMessage('Video too large (max 20MB)', 'warning');
        return
      }
      const type: MediaType = file.type.startsWith('image') ? 'IMAGE' : 'VIDEO';
      this.files.push({
        file,
        url: URL.createObjectURL(file),
        type,
      });

    });

    input.value = '';
  }

  removeFileBlog(index: number) {
    URL.revokeObjectURL(this.imgsandvids[index].url);
    this.imgsandvids.splice(index, 1);
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
        this.errorService.showMessage('Video too large (max 20MB)', 'warning');
        continue;
      }
      formData.append('files', file.file);
    }
    if (this.ModeEditblog) {

      for (let i = 0; i < this.imgsandvids.length; i++) {
        const file = this.imgsandvids[i];
        formData.append('filesupdated', file.url);
      }
      formData.append('idBlog_update', this.blog_id);

      this.blogService.updateBlogs(formData).subscribe({
        next: res => {
          if (!res.success) {
            this.errorService.showMessage('Error updateBlog', 'error');
            this.isSubmitting = false;
            return;
          }
          this.errorService.showMessage('Blog updated (:', 'success');
          this.clearForm();
          this.isSubmitting = false;
        },
        error: err => {
          this.errorService.showMessage('Error updateBlog blog', 'error');
          this.isSubmitting = false;
        }
      });

      return;
    }


    this.blogService.creatBlogs(formData).subscribe({
      next: res => {
        if (!res.success) {
          this.errorService.showMessage('Error creating blog', 'error');
          this.isSubmitting = false;
          return;
        }
        this.errorService.showMessage('Blog Created (:', 'success');
        this.clearForm();
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
    this.imgsandvids = [];
    this.onCancel()
  }

}
