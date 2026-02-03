import { Component } from '@angular/core';
import { Navbar } from '../navbar/navbar'; 
import { MatButtonModule } from '@angular/material/button';
import { RouterModule } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';


@Component({
  selector: 'app-page-welcome',
  standalone: true,
  imports: [Navbar , MatButtonModule , RouterModule , MatIconModule],
  templateUrl: './page-welcome.html',
  styleUrl: './page-welcome.css',
})
export class PageWelcome {}
