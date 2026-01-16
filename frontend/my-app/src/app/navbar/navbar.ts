import { Component, Input, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Setting } from '../setting/setting';
import { AuthStore } from '../auth/auth-store.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [Setting , CommonModule],
  templateUrl: './navbar.html',
  styleUrls: ['./navbar.css']
})

export class Navbar {
  
   @Input() loggedIn: boolean = false; 
  @Input() user: any = null;          

  showSettings: boolean = false;

  toggleSettings() {
    this.showSettings = !this.showSettings;
  }

}
