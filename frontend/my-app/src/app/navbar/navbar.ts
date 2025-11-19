import { Component } from '@angular/core';
import { Setting } from '../setting/setting'; 

@Component({
  selector: 'app-navbar',
   standalone: true,
  imports: [Setting],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css'
})

export class Navbar {
  
  showSettings: boolean = false;

  toggleSettings() {
    this.showSettings = !this.showSettings;
  }

}
