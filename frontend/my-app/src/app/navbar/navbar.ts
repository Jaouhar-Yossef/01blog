import { Component, Input, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Setting } from '../setting/setting';

import { Router } from '@angular/router';


@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [Setting , CommonModule],
  templateUrl: './navbar.html',
  styleUrls: ['./navbar.css']
})

export class Navbar {
  

  showSettings: boolean = false;

  toggleSettings() {
    this.showSettings = !this.showSettings;
  }



  constructor(private router: Router) {}

  isHome(): boolean {
    return this.router.url === '/home';
  }

  isRoot(): boolean {
    return this.router.url === '/';
  }

  
}
