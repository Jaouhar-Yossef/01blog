import { Component, inject } from '@angular/core';
import { Setting } from '../setting/setting';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [Setting, CommonModule, MatButtonModule, MatIconModule, RouterModule],
  templateUrl: './navbar.html',
  styleUrls: ['./navbar.css']
})

export class Navbar {

  constructor(private router: Router) { }
  isHome(): boolean {
    return this.router.url !== '/';
  }
  isRoot(): boolean {
    return this.router.url === '/';
  }

  searchHere() {
    this.router.navigate(['/home/Search']);
  }

  creatBlog() {
    this.router.navigate(['/home/CreatBlog']);
  }

}
