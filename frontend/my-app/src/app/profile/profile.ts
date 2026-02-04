import { Component, Inject, PLATFORM_ID } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ErrorService } from '../error/error.service';
import { isPlatformBrowser, CommonModule } from '@angular/common';
import { ProfileService } from './profile.service';
import { BlogListComponent } from '../blog-list-component/blog-list-component';

export interface UserProfile {
  id: number;
  username: string;
  email: string;
  imageUrl: string;
  bio?: string;
  followersCount?: number;
  followingCount?: number;
}


@Component ({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule , BlogListComponent ],
  templateUrl: './profile.html',
  styleUrl: './profile.css',
})
export class Profile {

  isBrowser = false;
  profile!: UserProfile;
  loading = false;
  username!: string;

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

    const username = this.route.snapshot.paramMap.get('name');
    if (!username) {
      this.errorService.showMessage('Profile not found', 'error');
      return;
    }


  this.username = username;

    this.loadProfile(username);
  }

  loadProfile(username: string) {
    this.loading = true;

    this.profileService.getProfileByUsername(username).subscribe({
      next: (res) => {
        this.loading = false;
      },
      error: (err) => {
        this.loading = false;

        this.errorService.showMessage('Cannot load profile 😢', 'error');
        this.router.navigate(['/home']);
      }
    });
  }

}
