import { Component, Inject, Input, PLATFORM_ID, effect } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Navbar } from '../navbar/navbar'; 
import { ContentHome } from '../content-home/content-home';

@Component({
  selector: 'app-home',
  standalone: true,  
  imports: [CommonModule, Navbar , ContentHome],
  templateUrl: './home.html',
  styleUrls: ['./home.css']
})
export class HomeComponent {}
