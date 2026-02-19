
import { AdminService } from './../admin/admin.service';
import { ErrorService } from '../error/error.service';
import { ProfileService } from '../profile/profile.service';
import { Router } from '@angular/router';
import { Component, ElementRef, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatTabsModule } from '@angular/material/tabs';
import { MatTableModule } from '@angular/material/table';
import { BehaviorSubject } from 'rxjs';
import { ApiResponse } from '../content-home/content-home.service';

import { TimeAgo } from '../content-home/content-home.service'
import { MatMenu } from '@angular/material/menu';



import { MatMenuModule } from '@angular/material/menu';
import { MatButtonModule } from '@angular/material/button';
import { ServiceConfirmation } from '../service-confirmation/service-confirmation.service';


import { ObserveIntersectionDirective } from '../content-home/observe-intersection.directive';


export interface Report {
  id: number;
  type: 'BLOG' | 'USER';
  created_by: string;
  reason: string;
  reported_blog: string | null;
  reported_user: string | null;
  status: 'PENDING' | 'RESOLVED' | 'DECLAINED';
}


export interface Blogs {
  blogId: string;
  title: string;
  created_by: string;
  status: string | null;
  updated_at: string;
}


export interface Users {
  username: string;
  email: string;
  role: string;
  status: string | null;
}

enum AdminTabs {
  Analytics = 0,
  Reports = 1,
  Users = 2,
  Blogs = 3
}


@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    MatIconModule,
    MatTabsModule,
    MatTableModule,
    MatMenu,
    MatMenuModule,
    MatButtonModule,
    ObserveIntersectionDirective
  ],
  templateUrl: './admin-dashboard.html',
  styleUrl: './admin-dashboard.css',
})

export class AdminDashboard {



  private analyticsSubject = new BehaviorSubject<any>(null);
  analytics$ = this.analyticsSubject.asObservable();

  private reportsSubject = new BehaviorSubject<Report[]>([]);
  reports$ = this.reportsSubject.asObservable();

  private usersSubject = new BehaviorSubject<Users[]>([]);
  users$ = this.usersSubject.asObservable();


  private blogsSubject = new BehaviorSubject<Blogs[]>([]);
  blogs$ = this.blogsSubject.asObservable();


  isReports = false;
  isUsers = false;
  isBlogs = false;


  loadingAnalytics = false;
  loadingReports = false;
  loadingUsers = false;
  loadingBlogs = false;



  analyticsLoaded = false;
  reportsLoaded = false;
  usersLoaded = false;
  blogsLoaded = false;



  pageReports = 0;
  sizeReports = 15;
  hasMoreReports = true;


  pageUsers = 0;
  sizeUsers = 15;
  hasMoreUsers = true;

  pageBlogs = 0;
  sizeBlogs = 15;
  hasMoreBlogs = true;

  displayedReportsColumns: string[] = [
    'type',
    'created_by',
    'reason',
    'reported',
    'status',
    'Action'
  ];

  displayedUsersColumns: string[] = [
    'username',
    'email',
    'role',
    'status',
    'Action'
  ];




  displayedBlogsColumns: string[] = [
    'blogId',
    'title',
    'created_by',
    'updated_at',
    'status',
    'Action'
  ];




  constructor(
    private adminService: AdminService,
    private errorService: ErrorService,
    private profileService: ProfileService,
    private router: Router,
    private confirmService: ServiceConfirmation
  ) { }


  ngOnInit() {
    this.loadAnalytics();
  }


  changeStatusUser(row: any, status: string) {
    if (this.loadingUsers) return
    if (row.status == status) return
    this.adminService.updateUserStatus(row.username, status).subscribe({
      next: res => {
        if (res.success) {
          this.errorService.showMessage('update successfully!', 'success');
        }
      },
      error: err => {
        this.errorService.showMessage('Error update :(', 'error');
      }
    });

    row.status = status
  }

  changeStatusBlog(row: any, status: string) {
    if (this.loadingBlogs) return
    if (row.status == status) return
    this.adminService.updateBlogStatus(row.blogId, status).subscribe({
      next: res => {
        if (res.success) {
          this.errorService.showMessage('update successfully ', 'success');
        }
      },
      error: err => {
        this.errorService.showMessage('Error update', 'error');
      }
    });

    row.status = status
  }


  changeStatus(row: any, status: string) {
    if (this.loadingReports) return
    if (row.status == status) return
    this.adminService.updateReportStatus(row.id, status).subscribe({
      next: res => {
        if (res.success) {
          this.errorService.showMessage('update successfully!', 'success');
        }
      },
      error: err => {
        this.errorService.showMessage('Error update', 'error');
      }
    });
    row.status = status;
  }

  deleteUser(username: string) {

    this.confirmService.open(
      'Are you sure you want to delete this User?',
      () => {
        this.adminService.deleteOneUser(username).subscribe({
          next: res => {
            if (res.success) {
              this.errorService.showMessage('Delete successfully!', 'success');
              const currentUsers = this.usersSubject.getValue();
              const updatedUsers = currentUsers.filter(user => user.username !== username);
              this.usersSubject.next(updatedUsers);
            }
          },
          error: err => {
            this.errorService.showMessage('Error DELETE User :(', 'error');
          }
        });
      }
    );
  }

  deleteBlog(id: string) {
    this.confirmService.open(
      'Are you sure you want to delete this Blog?',
      () => {
        this.adminService.deleteOneBlog(id).subscribe({
          next: res => {
            if (res.success) {
              this.errorService.showMessage('Delete successfully!', 'success');
              const currentBlogs = this.blogsSubject.getValue();
              const updatedBlogs = currentBlogs.filter(blog => blog.blogId !== id);
              this.blogsSubject.next(updatedBlogs);
            }
          },
          error: err => {
            this.errorService.showMessage('Error DELETE Blog :(', 'error');
          }
        });

      }
    );
  }
  deleteReport(id: number) {
    this.confirmService.open(
      'Are you sure you want to delete this Report?',
      () => {

        this.adminService.deleteReport(id).subscribe({
          next: res => {
            if (res.success) {
              this.errorService.showMessage('Delete successfully!', 'success');

              const currentReports = this.reportsSubject.getValue();
              const updatedReports = currentReports.filter(report => report.id !== id);
              this.reportsSubject.next(updatedReports);
            }
          },
          error: err => {
            this.errorService.showMessage('Error DELETE Report :(', 'error');
          }
        });

      }
    );


  }

  timeAgo(date: string): string {
    return TimeAgo(date);
  }


  goToProfile(name: string) {
    this.router.navigate(['/admin/profile', name]);
  }

  showTheBlog(id: string) {
    this.router.navigate(['/admin/blog', id]);
  }


  loadAnalytics() {
    if (this.loadingAnalytics) return;
    this.loadingAnalytics = true;

    this.adminService.getAnalytics().subscribe({
      next: (res) => {
        this.analyticsSubject.next(res.anyData);
        this.analyticsLoaded = true;
        this.loadingAnalytics = false;
      },
      error: () => {
        this.loadingAnalytics = false;
        this.analyticsSubject.next(null);
        this.errorService.showMessage('Error getting Analytics', 'error');
      }
    });
  }



  onTabIndexChange(index: number) {
    this.isReports = this.isUsers = this.isBlogs = false;
    switch (index) {
      case AdminTabs.Reports:
        this.isReports = true;
        break;
      case AdminTabs.Users:
        this.isUsers = true;
        break;
      case AdminTabs.Blogs:
        this.isBlogs = true;
        break;
    }
  }


  loadReports() {
    if (this.loadingReports || !this.hasMoreReports) return;
    this.loadingReports = true;
    this.adminService.getReports(this.pageReports, this.sizeReports).subscribe({
      next: (res: ApiResponse<Report[]>) => {
        this.reportsSubject.next([
          ...this.reportsSubject.value,
          ...res.anyData
        ]);
        if (res.anyData.length < this.sizeReports) {
          this.hasMoreReports = false;
        } else {
          this.pageReports++;
        }

        this.loadingReports = false;
      },
      error: () => {
        this.loadingReports = false;
        this.errorService.showMessage('Error getting Reports', 'error');
      }
    });
  }

  loadUsers() {
    if (this.loadingUsers || !this.hasMoreUsers) return;
    this.loadingUsers = true;

    this.adminService.getUsers(this.pageUsers, this.sizeUsers).subscribe({
      next: (res: ApiResponse<Users[]>) => {
        this.usersSubject.next([
          ...this.usersSubject.value,
          ...res.anyData
        ]);


        if (res.anyData.length < this.sizeUsers) {
          this.hasMoreUsers = false;
        } else {
          this.pageUsers++;
        }

        this.loadingUsers = false;
      },
      error: () => {
        this.loadingUsers = false;
        this.errorService.showMessage('Error getting users', 'error');
      }
    })

  }



  loadBlogs() {
    if (this.loadingBlogs || !this.hasMoreBlogs) return;
    this.loadingBlogs = true;

    this.adminService.getBlogs(this.pageBlogs, this.sizeBlogs).subscribe({
      next: (res: ApiResponse<Blogs[]>) => {
        this.blogsSubject.next([
          ...this.blogsSubject.value,
          ...res.anyData
        ]);

        if (res.anyData.length < this.sizeBlogs) {
          this.hasMoreBlogs = false;
        } else {
          this.pageBlogs++;
        }

        this.loadingBlogs = false;
      },
      error: () => {
        this.loadingBlogs = false;
        this.errorService.showMessage('Error getting blogs', 'error');
      }
    })

  }

}
