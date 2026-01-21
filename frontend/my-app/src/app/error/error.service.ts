import { Injectable, signal } from '@angular/core';

export type ErrorType = 'success' | 'error' | 'warning';

@Injectable({
  providedIn: 'root'
})
export class ErrorService {
  public message = signal(''); 
  public type = signal<ErrorType>('error'); 

  showMessage(message: string, type: ErrorType = 'error', duration: number = 300000) {
    this.message.set(message);
    this.type.set(type);

    setTimeout(() => {
      this.message.set('');
    }, duration);
  }
}
