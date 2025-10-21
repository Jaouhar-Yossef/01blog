import { Component, OnInit, signal } from '@angular/core';
import { MessageService } from './message';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true, 
  imports: [RouterModule],
  template: `<router-outlet></router-outlet>`,
})

export class App implements OnInit {
  message = signal('');

  constructor(private messageService: MessageService) {}

  ngOnInit() {
    this.messageService.getMessage().subscribe({
      next: (data) => this.message.set(data),
      error: (err) => console.error('Error:', err),
    });
  }
}
