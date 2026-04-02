import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { AuthService, TheUser } from '../auth/auth.service';
import { CommonModule } from '@angular/common';

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


  constructor(private fb: FormBuilder, private authService: AuthService) {
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

      if (u.imageUrl && u.imageUrl.length > 0) {
        this.imgUrl = this.baseUrl + u.imageUrl;
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
    this.imgUrl = ""
    this.file = null;
  }


  submit() {
    if (this.form.invalid) return;

    if (this.form.value.password !== this.form.value.confirmPassword) {
      alert("Passwords do not match");
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

    for (let [key, value] of formData.entries()) {
      console.log(key, value);
    }

    this.authService.updateProfile(formData).subscribe({
      next: (res) => console.log('Success:', res),
      error: (err) => console.error('Error:', err)
    });
  }

}