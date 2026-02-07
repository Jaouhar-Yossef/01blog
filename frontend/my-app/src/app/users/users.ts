import { Component, ElementRef, Inject, PLATFORM_ID, ViewChild } from '@angular/core';
import { ErrorService } from '../error/error.service';
import { ActivatedRoute, Router } from '@angular/router';
import { isPlatformBrowser } from '@angular/common';
import { ProfileService, User } from '../profile/profile.service';
import { BehaviorSubject } from 'rxjs';
import { ApiResponse } from '../content-home/content-home.service';
import { error } from 'console';

@Component({
  selector: 'app-users',
  imports: [],
  templateUrl: './users.html',
  styleUrl: './users.css',
})
export class Users {

  UsersSubject = new BehaviorSubject<any[]>([]);
  Users$ = this.UsersSubject.asObservable()

  isBrowser: boolean;



  page = 0;
  size = 10;
  loading = false;
  hasMore = true;

  baseUrl = 'http://localhost:8080';

  constructor(
    private profileService: ProfileService,
    private errorService: ErrorService,
    @Inject(PLATFORM_ID) private platformId: Object,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.isBrowser = isPlatformBrowser(this.platformId);
  }


  ngOnInit() {
    if (!this.isBrowser) return;
    this.loadUsers();
  }


  private io!: IntersectionObserver;
  @ViewChild('observer') observer!: ElementRef;

  ngAfterViewInit() {
    if (!isPlatformBrowser(this.platformId)) return;

    this.io = new IntersectionObserver(entries => {
      if (
        entries[0].isIntersecting &&
        !this.loading &&
        this.hasMore
      ) {
        this.loadUsers();
      }
    });

    this.io.observe(this.observer.nativeElement);
  }


  loadUsers() {
    if (this.loading || !this.hasMore) {
      return
    }
    this.loading = true;

    this.profileService.getUsers(this.page, this.size).subscribe({
      next: (res: ApiResponse<User[]>) => {
        this.UsersSubject.next([
          ...this.UsersSubject.value,
          ...res.anyData
        ])

        if (res.anyData.length < this.size) {
          this.hasMore = false;
        } else {
          this.page++;
        }

        this.loading = false;
      },

      error: err => {
        this.errorService.showMessage('Error getting Users ):', 'error');
        this.loading = false
      }
    })

  }



  trackById(index: number, user: any) {
    return index;
  }

}
