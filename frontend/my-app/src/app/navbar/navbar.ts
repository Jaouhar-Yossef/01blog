import { Component } from '@angular/core';
import { Setting } from '../setting/setting';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [Setting, CommonModule],
  templateUrl: './navbar.html',
  styleUrls: ['./navbar.css']
})
export class Navbar {
  showSettings = false;

  constructor(private router: Router) {}

  toggleSettings() {
    this.showSettings = !this.showSettings;
  }

  isHome(): boolean {
    return this.router.url === '/home';
  }

  isRoot(): boolean {
    return this.router.url === '/';
  }
}
