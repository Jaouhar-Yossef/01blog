import { Component, Inject, Input, PLATFORM_ID, effect } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Navbar } from '../navbar/navbar'; 
import { PageWelcome } from '../page-welcome/page-welcome';

@Component({
  selector: 'app-home',
  standalone: true,  
  imports: [CommonModule, Navbar, PageWelcome],
  templateUrl: './home.html',
  styleUrls: ['./home.css']
})
export class HomeComponent {}
