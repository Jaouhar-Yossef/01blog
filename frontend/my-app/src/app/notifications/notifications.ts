import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ObserveIntersectionDirective } from '../content-home/observe-intersection.directive';
import { ActivatedRoute, Router } from '@angular/router';
import { ErrorService } from '../error/error.service';
import { ApiResponse, ContentHomeService } from '../content-home/content-home.service';
import { AuthService } from '../auth/auth.service';
import { BehaviorSubject } from 'rxjs';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { TimeAgo } from '../content-home/content-home.service';

@Component({
  selector: 'app-notifications',
  standalone: true,
  imports: [CommonModule, ObserveIntersectionDirective,
    MatIconModule, MatCardModule],
  templateUrl: './notifications.html',
  styleUrl: './notifications.css',
})
export class Notifications {



  constructor(private errorService: ErrorService,
    private route: ActivatedRoute, private router: Router,
    private contentHomeService: ContentHomeService,
    private authService: AuthService,
  ) {
  }

  page = 0;
  size = 15;
  loading = false;
  hasMore = true;


  private NotificationSubject = new BehaviorSubject<any[]>([]);
  Notification$ = this.NotificationSubject.asObservable();

  loadNotification() {
    if (this.loading || !this.hasMore) return;
    this.loading = true;
    this.contentHomeService.getNotification(this.page, this.size).subscribe({
      next: (res: ApiResponse<any[]>) => {
        console.log("==> ", res)
        this.NotificationSubject.next([
          ...this.NotificationSubject.value,
          ...res.anyData
        ]);
        if (res.anyData.length < this.size) {
          this.hasMore = false;
        } else {
          this.page++;
        }

        this.loading = false;
      },
      error: () => {
        this.errorService.showMessage('Error getting Notifications', 'error')
        this.loading = false
      }
    })

  }


  timeis(time: string): string {
    return TimeAgo(time);
  }



  deleteAll() {

  }

  deleteNotification(id: number) {

    console.log("==> " , id) 
    if (this.loading) return;

    this.contentHomeService.deleteOnenotification(id).subscribe({
      next: (res) => {
        this.errorService.showMessage('Notification deleted (:', 'success');

        this.loading = false;
      },
      error: () => {
        this.errorService.showMessage('Error delete Notification :(', 'error')
        this.loading = false
      }
    })

  }


  trackById(index: number, user: any) {
    return index;
  }
}
