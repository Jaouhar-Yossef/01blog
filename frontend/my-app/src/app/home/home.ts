import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Navbar } from '../navbar/navbar'; 

import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-home',
  standalone: true,  
  imports: [CommonModule, Navbar , RouterModule],
  templateUrl: './home.html',
  styleUrls: ['./home.css']
})
export class HomeComponent {}