import { Component, inject, PLATFORM_ID } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { AuthService } from './../auth/auth.service'
import { ServiceConfirmation } from '../service-confirmation/service-confirmation.service';
import { ErrorService } from '../error/error.service';
import { Router } from '@angular/router';

import { MatIconModule } from '@angular/material/icon';



@Component({
  selector: 'app-setting',
  standalone: true,
  imports: [CommonModule, MatIconModule],
  templateUrl: './setting.html',
  styleUrls: ['./setting.css'],
})

export class Setting {
  show: boolean = false;
  showDelete: boolean = true;
  platformId = inject(PLATFORM_ID);

  constructor(
    private authService: AuthService,
    private confirmService: ServiceConfirmation,
    private errorService: ErrorService,
    private router: Router
  ) { }


  ngOnInit() {
    if (!isPlatformBrowser(this.platformId)) return;
    const user = this.authService.getUser();
    if (user != null && user.role  == "ADMIN") {
      this.showDelete = false;
    }

  }

  home() {
    this.router.navigate(['/home']);
  }

  SearchForAll() {
    this.router.navigate(['/home/Search']);
  }

  getUsers() {
    this.router.navigate(['/home/Users']);
  }


  profile() {
    if (!isPlatformBrowser(this.platformId)) return;
    const user = this.authService.getUser();
    if (user != null) {
      this.router.navigate([`/home/profile`, user.username]);
    }
  }


  savedBlog() {
    this.router.navigate([`/home/blogsSaved`]);
  }
  logout() {
    this.confirmService.open(
      'Are you sure you want to logout?',
      () => {
        this.authService.logout();
      }
    );
  }

  deleteAccount() {
    if (!this.showDelete) {
      return
    }
    this.confirmService.open(
      'Are you sure you want to delete your account?',
      () => {
        this.authService.deleteAccount().subscribe({
          next: (res) => {
            this.errorService.showMessage(`${res.message}`, 'success');
            this.authService.logout()
          },
          error: (err) => {
            this.errorService.showMessage(err.error.message, 'error');
          }
        })
      }
    );
  }

}
