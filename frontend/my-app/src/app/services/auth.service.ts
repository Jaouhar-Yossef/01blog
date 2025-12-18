import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface AuthCheckResponse {
  registered: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  constructor(private http: HttpClient) {}

  checkUser(): Observable<AuthCheckResponse> {
    return this.http.get<AuthCheckResponse>('http://localhost:8080/auth/check');
  }
}
