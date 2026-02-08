import { Component, inject, PLATFORM_ID } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { AuthService } from './../auth/auth.service'
import { ServiceConfirmation } from '../service-confirmation/service-confirmation.service';
import { ErrorService } from '../error/error.service';
import { Router } from '@angular/router';

import { MatIconModule } from '@angular/material/icon';
import { UiService } from '../content-home/ui.service';



@Component({
  selector: 'app-setting',
  standalone: true,
  imports: [CommonModule, MatIconModule],
  templateUrl: './setting.html',
  styleUrls: ['./setting.css'],
})

export class Setting {
  show: boolean = false;
  platformId = inject(PLATFORM_ID);
  private ui = inject(UiService);

  constructor(
    private authService: AuthService,
    private confirmService: ServiceConfirmation,
    private errorService: ErrorService,
    private router: Router
  ) { }



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
    const userStr = localStorage.getItem('user');
    if (userStr) {
      const user = JSON.parse(userStr);
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
