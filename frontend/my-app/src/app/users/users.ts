import { AfterViewInit, Component, ElementRef, inject, Input, OnInit, SimpleChanges, ViewChild } from '@angular/core';
import { ErrorService } from '../error/error.service';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ProfileService, User, UserMode } from '../profile/profile.service';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { ApiResponse } from '../content-home/content-home.service';
import { MatIconModule } from '@angular/material/icon';
import { ObserveIntersectionDirective } from '../content-home/observe-intersection.directive';


@Component({
  selector: 'app-users',
  standalone: true,
  imports: [CommonModule, MatIconModule , ObserveIntersectionDirective],
  templateUrl: './users.html',
  styleUrl: './users.css',
})

export class Users implements OnInit {

  UsersSubject = new BehaviorSubject<User[]>([]);
  Users$ = this.UsersSubject.asObservable()

  @Input() mode: UserMode = 'AllUsers';
  @Input() username: string = '';


  page = 0;
  size = 10;
  loading = false;
  hasMore = true;

  baseUrl = 'http://localhost:8080';

  private profileService = inject(ProfileService);
  constructor(
    private errorService: ErrorService,
    private route: ActivatedRoute,
    private router: Router
  ) { }


  modeADMINorHOME = ''

  ngOnInit() {
    if (this.router.url.startsWith('/admin/profile')) {
      this.modeADMINorHOME = 'ADMIN'
    }
  }


  ngOnChanges(changes: SimpleChanges) {
    if ((changes['mode'] && !changes['mode'].firstChange) ||
      (changes['username'] && !changes['username'].firstChange)) {
      this.page = 0;
      this.hasMore = true;
      this.UsersSubject.next([]);
      this.loadUsers();
    }
  }


  loadUsers() {
    if (this.loading || !this.hasMore) return;
    this.loading = true;
    this.profileService.getUsers(this.page, this.size, this.mode, this.username).subscribe({
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
    if (this.modeADMINorHOME = 'ADMIN') {
      this.router.navigate(['/admin/profile', username]);
      return
    }
    this.router.navigate(['/home/profile', username]);
  }


  ResponseFollow: Observable<ApiResponse<any>> = of(null as any);

  FollowTheUser(username: string, typeFollow: string) {
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
