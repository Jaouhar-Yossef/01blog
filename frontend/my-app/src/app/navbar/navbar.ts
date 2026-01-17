import { Component, Input, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Setting } from '../setting/setting';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [Setting , CommonModule],
  templateUrl: './navbar.html',
  styleUrls: ['./navbar.css']
})

export class Navbar {
  
  loggedIn: boolean = false; 

  showSettings: boolean = false;

  toggleSettings() {
    this.showSettings = !this.showSettings;
  }

}
