import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ObserveIntersectionDirective } from '../content-home/observe-intersection.directive';
import { ActivatedRoute, Router } from '@angular/router';
import { ErrorService } from '../error/error.service';
import { ApiResponse, ContentHomeService, Notification } from '../content-home/content-home.service';
import { AuthService } from '../auth/auth.service';
import { BehaviorSubject, forkJoin } from 'rxjs';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { TimeAgo } from '../content-home/content-home.service';
import { MatMenuModule } from '@angular/material/menu';

@Component({
  selector: 'app-notifications',
  standalone: true,
  imports: [CommonModule, ObserveIntersectionDirective,
    MatIconModule, MatCardModule, MatMenuModule],
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


  private NotificationSubject = new BehaviorSubject<Notification[]>([]);

  Notification$ = this.NotificationSubject.asObservable();

  loadNotification() {
    if (this.loading || !this.hasMore) return;
    this.loading = true;
    this.contentHomeService.getNotification(this.page, this.size).subscribe({
      next: (res: ApiResponse<Notification[]>) => {
        console.log("=> ", res)
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


  makeAllAsRead() {

    if (this.loading) return;
    this.loading = true;
    const currentNotifications = this.NotificationSubject.value;
    if (currentNotifications.length === 0) {
      this.loading = false;
      return;
    }

    let array_ids: number[] = [];

    currentNotifications.forEach(n => {
      if (n.active) {
        n.active = false;
        array_ids.push(n.id)
      }
    });

    if (array_ids.length > 0) {
      this.contentHomeService.ReadAllNotifications(array_ids).subscribe({
        next: () => {
          this.loading = false;
        },
        error: () => {
          this.loading = false
        }
      })
      this.NotificationSubject.next(currentNotifications);
    } else {
      this.loading = false
    }
  }

  makeAsRead(id: number) {
    if (this.loading) return;
    this.loading = true;
    this.contentHomeService.ReadNotification(id).subscribe({
      next: () => {
        this.loading = false;
      },
      error: () => {
        this.loading = false
      }
    })
  }

  showTheBlog(blog_id: string, Ntf_id: number) {
    this.router.navigate(['/home/blog', blog_id]);
    this.makeAsRead(Ntf_id)
  }


  goToProfile(creatBy: string, Ntf_id: number) {
    this.router.navigate(['/home/profile', creatBy]);
    this.makeAsRead(Ntf_id)
  }

  deleteAll() {
    if (this.loading) return;
    this.loading = true;

    const currentNotifications = this.NotificationSubject.value;
    if (currentNotifications.length === 0) {
      this.errorService.showMessage('No notifications to delete', 'warning');
      this.loading = false;
      return;
    }

    let array_ids: number[] = [];

    currentNotifications.forEach(n => array_ids.push(n.id));

    this.contentHomeService.DeleteAllNotifications(array_ids).subscribe({
      next: () => {
        this.NotificationSubject.next([]);
        this.errorService.showMessage('All notifications deleted (:', 'success');
        this.loading = false;
      },
      error: () => {
        this.errorService.showMessage('Error deleting notifications :(', 'error');
        this.loading = false
      }
    })

  }

  deleteNotification(id: number) {
    if (this.loading) return;
    this.loading = true;
    this.contentHomeService.deleteOnenotification(id).subscribe({
      next: () => {
        const currentNotifications = this.NotificationSubject.value;
        const updatedNotifications = currentNotifications
          .filter(notification => notification.id !== id);

        this.NotificationSubject.next(updatedNotifications);
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
