import { Component, OnInit, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-home',
  standalone: true,  
  imports: [CommonModule],
  templateUrl: './home.html',
  styleUrls: ['./home.css']
})

export class HomeComponent {
  message = signal(''); 

  private apiUrl = 'http://localhost:8080/api/message';

  constructor(private http: HttpClient) {}

   ngOnInit() {
    this.getMessage().subscribe({
      next: (data) => this.message.set(data),
      error: (err) => console.error('Error fetching message:', err)
    });
  }

  getMessage(): Observable<string> {
    return this.http.get(this.apiUrl, { responseType: 'text' });
  }

}
