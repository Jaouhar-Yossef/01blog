import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Setting } from '../setting/setting';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [Setting , CommonModule],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css'
})

export class Navbar {
  
  showSettings: boolean = false;

  toggleSettings() {
    this.showSettings = !this.showSettings;
  }


  userRegistered = signal(false); 
  
  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    this.authService.checkUser().subscribe(res => {
      this.userRegistered.set(res.registered);
    });
  }

}
