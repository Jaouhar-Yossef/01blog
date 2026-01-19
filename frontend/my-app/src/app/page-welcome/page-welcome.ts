import { Component } from '@angular/core';
import { Navbar } from '../navbar/navbar'; 


@Component({
  selector: 'app-page-welcome',
  standalone: true,
  imports: [Navbar],

  templateUrl: './page-welcome.html',
  styleUrl: './page-welcome.css',
})
export class PageWelcome {

}
