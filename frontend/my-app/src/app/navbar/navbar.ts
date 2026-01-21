import { Component } from '@angular/core';
import { Setting } from '../setting/setting';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { CreatBlog } from '../creat-blog/creat-blog';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [Setting, CommonModule , CreatBlog],
  templateUrl: './navbar.html',
  styleUrls: ['./navbar.css']
})

export class Navbar {
  showSettings = false;

  showCreatBlog = false;

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

  creatBlog() {
    this.showCreatBlog = !this.showCreatBlog;
  }

}
