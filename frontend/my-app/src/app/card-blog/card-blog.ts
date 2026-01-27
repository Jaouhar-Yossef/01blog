import { Component, Input, OnChanges, SimpleChanges, OnDestroy, NgZone } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-card-blog',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './card-blog.html',
  styleUrls: ['./card-blog.css'],
})
export class CardBlog implements OnChanges, OnDestroy {
  @Input() blog!: any;

  baseUrl = 'http://localhost:8080';
  currentIndex = 0;
  intervalId: any;

  constructor(private ngZone: NgZone) {}

  ngOnChanges(changes: SimpleChanges) {
    if (changes['blog'] && this.blog?.media?.length > 1 && !this.intervalId) {
      this.startSlideshow();
    }
  }

  startSlideshow() {
    this.ngZone.runOutsideAngular(() => {
      this.intervalId = setInterval(() => {
        this.ngZone.run(() => {
          this.next();
        });
      }, 3000); // كل 3 ثواني
    });
  }

  next() {
    if (this.blog?.media?.length) {
      this.currentIndex = (this.currentIndex + 1) % this.blog.media.length;
    }
  }

  ngOnDestroy() {
    if (this.intervalId) {
      clearInterval(this.intervalId);
    }
  }
}
