import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from './../auth/auth.service'
@Component({
  selector: 'app-setting',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './setting.html',
  styleUrls: ['./setting.css'],
})

export class Setting {
  show: boolean = false;
  loding: boolean =  false;

  constructor(
    private authService: AuthService,
  ) {}

  userlogout() {
    if (this.loding) {
      return;
    }
    this.loding = true;
    this.authService.logout();
  }
}
