import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ServiceConfirmation } from './service-confirmation.service';

@Component({
  selector: 'app-ConfirmationComponent',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './service-confirmation.html',
  styleUrls: ['./service-confirmation.css'],
})
export class ConfirmationComponent {

  constructor(public confirmService: ServiceConfirmation) {}

  confirm() {
    this.confirmService.confirm();
  }

  cancel() {
    this.confirmService.cancel();
  }
}
