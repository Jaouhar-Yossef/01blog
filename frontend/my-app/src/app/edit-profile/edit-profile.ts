import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { AuthService, TheUser } from '../auth/auth.service';
import { CommonModule } from '@angular/common';
import { ErrorService } from '../error/error.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-edit-profile',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    ReactiveFormsModule
  ],
  templateUrl: './edit-profile.html',
  styleUrl: './edit-profile.css',
})
export class EditProfile {

  form!: FormGroup;

  file: File | null = null;

  emailRegex: RegExp = /^[^\s@]{3,}@[^\s@]{2,}\.[^\s@]{2,}$/;

  name: string | null = ""

  theuser: TheUser | null = null;

  baseUrl = 'http://localhost:8080';

  imgUrl = "";
  theUserEmail = "";

  hasImg = false;


  originalFormValue: any = null;
  originalImageUrl: string = '';

  imageRemoved = false;

  constructor(private fb: FormBuilder, private authService: AuthService,
    private router: Router, private errorService: ErrorService) {
    this.form = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(20)]],
      email: ['', [
        Validators.required,
        Validators.pattern(this.emailRegex)
      ]],
      password: [''],
      confirmPassword: ['']
    });
  }

  ngOnInit() {
    const u = this.authService.getUser();

    if (u) {
      this.theuser = u;

      this.form.patchValue({
        username: u.username,
        email: u.email
      });

      this.originalFormValue = this.form.value;

      if (u.imageUrl && u.imageUrl.length > 0) {
        this.imgUrl = this.baseUrl + u.imageUrl;
        this.originalImageUrl = this.imgUrl;
        this.hasImg = true;
      }
    }
  }

  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.imgUrl = URL.createObjectURL(file);
      this.file = file;
    }


  }
  removeFile() {
    this.imgUrl = "";
    this.file = null;
    this.imageRemoved = true;
  }
  isFormChanged(): boolean {

    const current = this.form.value;

    const formChanged =
      current.username !== this.originalFormValue.username ||
      current.email !== this.originalFormValue.email ||
      !!current.password ||
      !!current.confirmPassword;

    const imageChanged =
      this.file !== null || this.imageRemoved;

    return formChanged || imageChanged;
  }


  isPasswordValid(): boolean {
    const password = this.form.value.password;
    const confirm = this.form.value.confirmPassword;

    if (!password && !confirm) return true;

    return password === confirm;
  }

  isImageValid(): boolean {
    if (!this.file) return true;

    const allowedTypes = ['image/png', 'image/jpeg', 'image/webp'];

    return allowedTypes.includes(this.file.type);
  }

  isImageSafe(): boolean {
    if (!this.file) return true;

    const allowedTypes = ['image/png', 'image/jpeg', 'image/webp'];

    const isTypeValid = allowedTypes.includes(this.file.type);
    const isSizeValid = this.file.size < 5 * 1024 * 1024;

    return isTypeValid && isSizeValid;
  }



  CancelEdit() {
    const user = this.authService.getUser();
    if (user != null) {
      this.router.navigate([`/home/profile`, user.username]);
      return
    }
    this.router.navigate([`/home`]);
  }

  submit() {
    if (this.form.invalid) return;

    if (!this.isFormChanged()) {
      this.errorService.showMessage('No changes detected', 'error');
      return;
    }

    if (!this.isPasswordValid()) {
      this.errorService.showMessage('Passwords do not match', 'error');
      return;
    }

    if (!this.isImageValid()) {
      this.errorService.showMessage('Invalid image type (png/jpeg/webp)', 'error');
      return;
    }

    const formData = new FormData();

    formData.append('username', this.form.value.username);
    formData.append('email', this.form.value.email);

    if (this.form.value.password) {
      formData.append('password', this.form.value.password);
    }

    if (this.file) {
      formData.append('image', this.file);
    }

    this.authService.updateProfile(formData).subscribe({
      next: (res) => {
        this.authService.saveAuthData(res.anyData);
        this.errorService.showMessage(`Edit profile successfully (:`, 'success');
        this.router.navigate([`home`]);
      },
      error: (err) => {
        this.errorService.showMessage(`Error edit profile ):`, 'error');
      }
    });
  }

}