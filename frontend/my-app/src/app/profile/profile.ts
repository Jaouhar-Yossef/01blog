import { Component, Inject, PLATFORM_ID } from '@angular/core';

import { ActivatedRoute, Router } from '@angular/router';
import { ErrorService } from '../error/error.service';
import { isPlatformBrowser, CommonModule } from '@angular/common';
import { ProfileService, UserProfile } from './profile.service';
import { BlogListComponent } from '../blog-list-component/blog-list-component';
import { BehaviorSubject } from 'rxjs';
import { ApiResponse } from '../content-home/content-home.service';

import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, BlogListComponent, MatIconModule, MatDividerModule, MatButtonModule],
  templateUrl: './profile.html',
  styleUrl: './profile.css',
})
export class Profile {

  ProfileSubject = new BehaviorSubject<any>(null);
  profile$ = this.ProfileSubject.asObservable();

  isBrowser = false;

  loading = false;
  username!: string;

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

    this.route.paramMap.subscribe((params) => {
      const username = params.get('name');
      if (!username) {
        this.errorService.showMessage('Profile not found ):', 'error');
        return;
      }

      this.username = username;
      this.loadProfile(username);
    });
  }


  loadProfile(username: string) {
    this.loading = true;
    this.ProfileSubject.next(null);
    this.profileService.getProfileByUsername(username).subscribe({
      next: (res: ApiResponse<UserProfile>) => {
        this.ProfileSubject.next(res.anyData)
        this.loading = false;
      },
      error: (err) => {
        this.loading = false;
        this.ProfileSubject.next(null);
        this.errorService.showMessage(`Profile not found ):`, 'error');
        this.router.navigate(['/home']);
      }
    });
  }


  FollowTheUser() {
    if (this.loading) return;

    this.loading = true;
    this.profileService.follow(this.username).subscribe({
      next: (res: ApiResponse<any>) => {
        if (res.success) {
          const profile = this.ProfileSubject.value;
          if (profile) {
            profile.following = true;
            profile.countFollowers += 1;
            this.ProfileSubject.next(profile);
          }
          this.errorService.showMessage(`${res.message} (:`, 'success');
        }
        this.loading = false;
      },
      error: (err) => {
        this.loading = false;
        this.errorService.showMessage(`Error Follow ):`, 'error');
      }
    })

  }



  UnFollowTheUser() {
    if (this.loading) return;

    this.loading = true;
    this.profileService.unfollow(this.username).subscribe({
      next: (res: ApiResponse<any>) => {
        if (res.success) {
          const profile = this.ProfileSubject.value;

          if (profile) {
            profile.following = false;
            if (profile.countFollowers > 0) {
              profile.countFollowers -= 1; 
            } 
            this.ProfileSubject.next(profile); 
          }
          this.errorService.showMessage(`${res.message} (:`, 'success');
        }
        this.loading = false;
      },
      error: (err) => {

        this.loading = false;
        this.errorService.showMessage(`Error unFollow ):`, 'error');
      }
    })

  }

}
