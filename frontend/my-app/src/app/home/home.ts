import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Navbar } from '../navbar/navbar'; 
import { AuthService } from '../services/auth.service';
import { PageWelcome } from '../page-welcome/page-welcome';

@Component({
  selector: 'app-home',
  standalone: true,  
  imports: [CommonModule, Navbar, PageWelcome],
  templateUrl: './home.html',
  styleUrls: ['./home.css']
})

export class HomeComponent implements OnInit {
  userRegistered = signal(false); 
  
  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    this.authService.checkUser().subscribe(res => {
      this.userRegistered.set(res.registered);
    });
  }
}
