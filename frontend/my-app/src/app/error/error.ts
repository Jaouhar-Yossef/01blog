import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ErrorService } from './error.service';

@Component({
  selector: 'app-error',
  standalone: true,
  imports: [CommonModule],
   templateUrl: './error.html',
  styleUrls: ['./error.css']
})
export class ErrorComponent {
  constructor(public errorService: ErrorService) {}
}
