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

  name : string | null = ""
  
  theuser : TheUser | null = null;

  baseUrl = 'http://localhost:8080';

  imgUrl = "";
  theUserEmail = "";

  
  constructor(private fb: FormBuilder , private authService: AuthService) {
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
      this.theuser = u 
      this.name = this.theuser?.username || "";
      if (this.theuser?.imageUrl && this.theuser?.imageUrl.length > 0) {
        this.imgUrl = this.baseUrl + this.theuser.imageUrl;
      }
      this.theUserEmail = this.theuser?.email || "";
      console.log("==> " ,this.theuser?.email);
    }
    
  }

  onFileSelected(event: any) {
    this.file = event.target.files[0];
  }

  submit() {
    if (this.form.invalid) return;
    const data = this.form.value;
    console.log(data);
    console.log(this.file);
  }
}