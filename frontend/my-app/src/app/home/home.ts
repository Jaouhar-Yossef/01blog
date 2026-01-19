import { Component, Inject, Input, PLATFORM_ID, effect } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Navbar } from '../navbar/navbar'; 

@Component({
  selector: 'app-home',
  standalone: true,  
  imports: [CommonModule, Navbar],
  templateUrl: './home.html',
  styleUrls: ['./home.css']
})
export class HomeComponent {}
