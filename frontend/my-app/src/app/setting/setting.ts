import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from './../auth/auth.service'
import { ServiceConfirmation } from '../service-confirmation/service-confirmation.service';
import { ErrorService } from '../error/error.service';
import { Router } from '@angular/router';


@Component({
  selector: 'app-setting',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './setting.html',
  styleUrls: ['./setting.css'],
})

export class Setting {
  show: boolean = false;

  constructor(
    private authService: AuthService,
    private confirmService: ServiceConfirmation,
    private errorService: ErrorService,
    private router: Router
  ) {}



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
