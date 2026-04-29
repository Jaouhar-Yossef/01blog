import { Component } from '@angular/core';

import { ActivatedRoute, Router } from '@angular/router';
import { ErrorService } from '../error/error.service';
import { CommonModule } from '@angular/common';
import { ProfileService, UserProfile } from './profile.service';
import { BlogListComponent } from '../blog-list-component/blog-list-component';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { ApiResponse, ContentHomeService, UserMode } from '../content-home/content-home.service';

import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { MatButtonModule } from '@angular/material/button';
import { Users } from '../users/users';
import { AuthService } from '../auth/auth.service';
import { ShowAdminMessage } from '../content-home/ui.showAdminMsg.service';
import { MatDialog } from '@angular/material/dialog';
import { Report } from '../report/report';
import { ImageService } from '../content-home/ImageService.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, BlogListComponent, MatIconModule, MatDividerModule, MatButtonModule, Users],
  templateUrl: './profile.html',
  styleUrl: './profile.css',
})
export class Profile {

  ProfileSubject = new BehaviorSubject<any>(null);
  profile$ = this.ProfileSubject.asObservable();

  loading = false;
  username!: string;
  isBlogs = true;
  isUserBanned = false;

  mode = ""

  baseUrl = 'http://localhost:8080';

  constructor(
    private profileService: ProfileService,
    public imageService: ImageService,
    private contentHomeService: ContentHomeService,
    private dialog: MatDialog,
    private errorService: ErrorService,
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService,
    private showAdminMessage: ShowAdminMessage
  ) {
  }

  usersMode: UserMode = 'FOLLOWERS';

  ReportUser(username: string) {
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

  showFollowers() {

    this.isBlogs = false;
    this.usersMode = 'FOLLOWERS';
  }

  showFollowing() {
    this.isBlogs = false;
    this.usersMode = 'FOLLOWING';
  }

  showBlogs() {
    this.isBlogs = true;
  }

  ngOnInit() {
    const user = this.authService.getUser();
    if (user != null && user.status === "ADMIN") {
      this.mode = "ADMIN"
    }

    this.route.paramMap.subscribe((params) => {
      const username = params.get('name');
      if (!username) {
        this.errorService.showMessage('Profile not found ):', 'error');
        return;
      }
      this.username = username;
      this.loadProfile(username);

      const user = this.authService.getUser();
      if (user != null && user?.status == "BANNED") {
        this.isUserBanned = true;
      }
    });
  }

  loadProfile(username: string) {
    this.loading = true;
    this.ProfileSubject.next(null);

    if (this.mode == "ADMIN") {
      this.profileService.getProfileByUsernameToTheAdmin(username).subscribe({
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

      return
    }

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

  editProfile() {
    this.router.navigate(['/home/EditProfile']);
  }

  ResponseFollow: Observable<ApiResponse<any>> = of(null as any);

  FollowTheUser(typeFollow: string) {
    if (this.isUserBanned) {
      this.showAdminMessage.showAdminMessageUserBanned()
      return
    }
    if (this.loading) return;

    this.loading = true;
    if (typeFollow === 'follow' || typeFollow === 'followBack') {
      this.ResponseFollow = this.profileService.follow(this.username);
    }
    else if (typeFollow === 'unfollow') {
      this.ResponseFollow = this.profileService.unfollow(this.username);
    }

    this.ResponseFollow.subscribe({
      next: (res: ApiResponse<any>) => {
        if (res.success) {
          const profile = this.ProfileSubject.value;
          if (typeFollow === 'follow' || typeFollow === 'followBack') {
            if (profile) {
              profile.following = true;
              profile.countFollowers += 1;
            }
          } else if (typeFollow === 'unfollow') {
            if (profile) {
              profile.following = false;
              if (profile.countFollowers > 0) {
                profile.countFollowers -= 1;
              }
            }
          }
          this.ProfileSubject.next(profile);
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



  get modeClass(): string {
    return this.mode + '-style';
  }
}
