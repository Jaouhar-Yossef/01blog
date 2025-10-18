import { Component, OnInit, signal } from '@angular/core';
import { MessageService } from './message';

@Component({
  selector: 'app-root',
  templateUrl: './app.html',
  styleUrls: ['./app.css']
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
