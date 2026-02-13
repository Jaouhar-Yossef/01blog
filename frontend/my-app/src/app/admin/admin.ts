import { Component } from '@angular/core';

import { CommonModule } from '@angular/common';


import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../auth/auth.service';



@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule
  ],
  templateUrl: './admin.html',
  styleUrl: './admin.css',
})
export class AdminComponent {
  show: boolean = false;


  constructor(private router: Router , private authService: AuthService) { }


  goToHome() {
    this.router.navigate(['/home']);
  }


  goToMyProfile() {
    const user = this.authService.getUser(); 
    if (!user || user == null ) {
      return
    }
    if (user.role == "ADMIN") {
      this.router.navigate(['/admin/profile/' , user.username]);
    }
  }
}