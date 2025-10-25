import { Component, OnInit, signal } from '@angular/core';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms'; 

@Component({
  selector: 'app-root',
  standalone: true, 
  imports: [RouterModule ,  FormsModule],
   template: `<router-outlet></router-outlet>`
})

export class App {}
