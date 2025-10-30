import { Component, ElementRef, ViewChild, AfterViewInit, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { CommonModule } from '@angular/common';
import { RegisterComponent } from '../register/register';
import { LoginComponent } from '../login/login';

@Component({
  selector: 'app-auth',
  standalone: true,
  imports: [CommonModule, RegisterComponent, LoginComponent],
  templateUrl: './auth.html',
  styleUrls: ['./auth.css'],
})
export class AuthComponent implements AfterViewInit {
  showLogin = false;

  toggleForm() {
    this.showLogin = !this.showLogin;
  }

  @ViewChild('bgVideo') bgVideo!: ElementRef<HTMLVideoElement>;

  constructor(@Inject(PLATFORM_ID) private platformId: Object) {}

  ngAfterViewInit() {

    if (isPlatformBrowser(this.platformId)) {
      const video = this.bgVideo.nativeElement as HTMLVideoElement;

      if (video && typeof video.play === 'function') {
        video.muted = true;
        video.play().catch(err => console.warn('Autoplay blocked:', err));
      } else {
        console.warn('Video element not ready or not a real <video> tag');
      }
    }
  }
}
