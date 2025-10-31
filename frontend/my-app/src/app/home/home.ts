import { Component} from '@angular/core';
import { CommonModule } from '@angular/common';
import { Navbar } from '../navbar/navbar'; 

@Component({
  selector: 'app-home',
  standalone: true,  
  imports: [CommonModule , Navbar],
  templateUrl: './home.html',
  styleUrls: ['./home.css']
})

export class HomeComponent {}
