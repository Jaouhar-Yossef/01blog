import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatTabsModule } from '@angular/material/tabs';
import { MatTableModule } from '@angular/material/table';
import { BehaviorSubject } from 'rxjs';

import { AdminService } from './admin.service';
import { ErrorService } from '../error/error.service';

/* =======================
   Models
======================= */

export interface Report {
  id: number;
  type: 'BLOG' | 'USER';
  created_by: string;
  reason: string;
  reported_blog: string | null;
  reported_user: string | null;
  status: 'PENDING' | 'RESOLVED' | 'DECLAINED';
}

/* =======================
   Tabs Enum
======================= */

enum AdminTabs {
  Analytics = 0,
  Reports = 1,
  Users = 2,
  Blogs = 3
}

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [
    CommonModule,
    MatIconModule,
    MatTabsModule,
    MatTableModule
  ],
  templateUrl: './admin.html',
  styleUrl: './admin.css',
})
export class AdminComponent {

  /* =======================
     State Subjects
  ======================= */

  show: boolean = false;

  private analyticsSubject = new BehaviorSubject<any>(null);
  analytics$ = this.analyticsSubject.asObservable();

  private reportsSubject = new BehaviorSubject<Report[]>([]);
  reports$ = this.reportsSubject.asObservable();

  /* =======================
     UI State
  ======================= */

  isReports = false;
  isUsers = false;
  isBlogs = false;

  /* =======================
     Loading Flags
  ======================= */

  loadingAnalytics = false;
  loadingReports = false;
  loadingUsers = false;
  loadingBlogs = false;

  /* =======================
     Lazy Load Flags
  ======================= */

  analyticsLoaded = false;
  reportsLoaded = false;
  usersLoaded = false;
  blogsLoaded = false;

  /* =======================
     Pagination
  ======================= */

  page = 0;
  size = 10;
  hasMore = true;

  /* =======================
     Tables
  ======================= */

  displayedReportsColumns: string[] = [
    'type',
    'created_by',
    'reason',
    'reported',
    'status'
  ];

  displayedUsersColumns: string[] = [
    'username',
    'email',
    'status',
    'role'
  ];

  usersData = [
    {
      username: 'john_doe',
      email: 'john@example.com',
      status: 'Active',
      role: 'User'
    },
    {
      username: 'jane_smith',
      email: 'jane@example.com',
      status: 'Inactive',
      role: 'Admin'
    }
  ];

  /* =======================
     Constructor
  ======================= */

  constructor(
    private adminService: AdminService,
    private errorService: ErrorService
  ) { }

  /* =======================
     Init
  ======================= */

  ngOnInit() {
    this.loadAnalytics(); // auto-load analytics
  }

  /* =======================
     Analytics
  ======================= */

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

  /* =======================
     Tabs Logic
  ======================= */

  onTabIndexChange(index: number) {
    this.isReports = this.isUsers = this.isBlogs = false;

    switch (index) {

      case AdminTabs.Reports:
        if (!this.reportsLoaded) {
          this.loadReports();
          this.reportsLoaded = true;
        }
        this.isReports = true;
        break;

      case AdminTabs.Users:
        if (!this.usersLoaded) {
          console.log('fetch users');
          this.usersLoaded = true;
        }
        this.isUsers = true;
        break;

      case AdminTabs.Blogs:
        if (!this.blogsLoaded) {
          console.log('fetch blogs');
          this.blogsLoaded = true;
        }
        this.isBlogs = true;
        break;
    }
  }

  /* =======================
     Reports
  ======================= */

  loadReports() {
    if (this.loadingReports || !this.hasMore) return;
    this.loadingReports = true;

    this.adminService.getReports(this.page, this.size).subscribe({
      next: (res) => {
        this.reportsSubject.next([
          ...this.reportsSubject.value,
          ...res.anyData
        ]);

        console.log("==> ", res)
        if (res.anyData.length < this.size) {
          this.hasMore = false;
        } else {
          this.page++;
        }

        this.loadingReports = false;
      },
      error: () => {
        this.loadingReports = false;
        this.errorService.showMessage('Error getting Reports', 'error');
      }
    });
  }

}
