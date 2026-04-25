import { Component, inject, Input, OnInit, SimpleChanges } from '@angular/core';
import { ErrorService } from '../error/error.service';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ProfileService } from '../profile/profile.service';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { ApiResponse, ContentHomeService, User, UserMode } from '../content-home/content-home.service';
import { MatIconModule } from '@angular/material/icon';
import { ObserveIntersectionDirective } from '../content-home/observe-intersection.directive';
import { MatDialog } from '@angular/material/dialog';
import { Report } from './../report/report';
import { AuthService } from '../auth/auth.service';
import { ShowAdminMessage } from '../content-home/ui.showAdminMsg.service';


@Component({
  selector: 'app-users',
  standalone: true,
  imports: [CommonModule, MatIconModule, ObserveIntersectionDirective],
  templateUrl: './users.html',
  styleUrl: './users.css',
})

export class Users implements OnInit {

  UsersSubject = new BehaviorSubject<User[]>([]);
  Users$ = this.UsersSubject.asObservable()

  @Input() mode: UserMode = 'ALLUSERS';
  @Input() username_or_searchWord: string = '';

  page = 0;
  size = 10;
  loading = false;
  hasMore = true;
  isUserBanned = false;

  baseUrl = 'http://localhost:8080';

  private profileService = inject(ProfileService);
  constructor(
    private errorService: ErrorService,
    private route: ActivatedRoute,
    private router: Router,
    private dialog: MatDialog,
    private contentHomeService: ContentHomeService,
    private authService: AuthService,
    private showAdminMessage: ShowAdminMessage,
  ) { }


  modeADMINorHOME = ''

  ngOnInit() {
    if (this.router.url.startsWith('/admin/profile')) {
      this.modeADMINorHOME = 'ADMIN'
    }

    const user = this.authService.getUser();
    if (user != null && user?.status == "BANNED") {
      this.isUserBanned = true;
    }
  }

  ReportUsers(username: string) {
    if (this.isUserBanned) {
      this.showAdminMessage.showAdminMessageUserBanned()
      return
    }
    if (this.loading || !username || username.length <= 0) return;
    this.loading = true;

    const dialogRef = this.dialog.open(Report, {
      width: '700px',
      data: {}
    });


    dialogRef.afterClosed().subscribe(reason => {
      if (!reason) {
        this.loading = false;
        return
      }
      if (reason.length > 200) {
        this.errorService.showMessage('Reason cannot exceed 200 characters', 'error')
        this.loading = false;
        return
      }

      if (reason.length < 5) {
        this.errorService.showMessage('wa  cherif', 'error')
        this.loading = false;
        return
      }

      this.contentHomeService.ReportUserOrBlog('USER', reason, username).subscribe({
        next: res => {
          if (res.success) {
            this.errorService.showMessage('Report Created', 'success')
            this.loading = false;
          }
        },
        error: () => {
          this.errorService.showMessage('error report User ):', 'error')
          this.loading = false;
        }
      });
    });
    this.loading = false;
  }

  ngOnChanges(changes: SimpleChanges) {

    if (
      changes['mode'] ||
      changes['username_or_searchWord']
    ) {

      this.page = 0;
      this.hasMore = true;
      this.UsersSubject.next([]);

      if (
        this.mode === 'SEARCH' &&
        (!this.username_or_searchWord || this.username_or_searchWord.trim().length === 0)
      ) {
        this.hasMore = false;
        return;
      }

      this.loadUsers();
    }

  }


  loadUsers() {
    if (this.loading || !this.hasMore) return;
    this.loading = true;


    if (this.mode === 'SEARCH' && this.username_or_searchWord.trim().length === 0) {
      this.UsersSubject.next([]);
      this.hasMore = false;
      return
    }

    this.contentHomeService.getUsers(this.page, this.size, this.mode, this.username_or_searchWord).subscribe({
      next: (res: ApiResponse<User[]>) => {
        this.UsersSubject.next([
          ...this.UsersSubject.value,
          ...res.anyData
        ])

        if (res.anyData.length < 9) {
          this.hasMore = false;
        } else {
          this.page++;
        }

        this.loading = false;
      },

      error: err => {
        console.log(err)
        this.errorService.showMessage('Error getting Users ):', 'error');
        this.loading = false
      }
    })

  }


  goToProfile(username: string) {
    if (this.modeADMINorHOME === 'ADMIN') {
      this.router.navigate(['/admin/profile', username]);
      return
    }
    this.router.navigate(['/home/profile', username]);
  }


  ResponseFollow: Observable<ApiResponse<any>> = of(null as any);

  FollowTheUser(username: string, typeFollow: string) {
    if (this.isUserBanned) {
      this.showAdminMessage.showAdminMessageUserBanned()
      return
    }
    if (this.loading) return;
    this.loading = true;

    if (typeFollow === 'follow' || typeFollow === 'followBack') {
      this.ResponseFollow = this.profileService.follow(username);
    }
    else if (typeFollow === 'unfollow') {
      this.ResponseFollow = this.profileService.unfollow(username);
    }

    this.ResponseFollow.subscribe({
      next: (res) => {
        if (res?.success) {

          const users = this.UsersSubject.getValue();

          const updatedUsers = users.map(u => {
            if (u.username === username) {

              if (typeFollow === 'follow' || typeFollow === 'followBack') {
                return {
                  ...u,
                  following: true
                };
              }

              if (typeFollow === 'unfollow') {
                return {
                  ...u,
                  following: false
                };
              }

            }
            return u;
          });

          this.UsersSubject.next(updatedUsers);
          this.errorService.showMessage(res.message, 'success');
        }

        this.loading = false;
      },

      error: () => {
        this.loading = false;
        this.errorService.showMessage(`Follow action failed`, 'error');
      }
    });
  }


  trackById(index: number, user: any) {
    return index;
  }

}
