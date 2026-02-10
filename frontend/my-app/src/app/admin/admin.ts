import { Component } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';





// import { ActivatedRoute, Router } from '@angular/router';

// import { ErrorService } from '../error/error.service';

// import { CommonModule } from '@angular/common';


// import { BlogListComponent } from '../blog-list-component/blog-list-component';

// import { BehaviorSubject, Observable, of } from 'rxjs';

// import { ApiResponse } from '../content-home/content-home.service';



// import { MatDividerModule } from '@angular/material/divider';

// import { MatButtonModule } from '@angular/material/button';

// import { Users } from '../users/users';
import { MatTabsModule } from '@angular/material/tabs';

import { CommonModule } from '@angular/common';

import { MatTableModule } from '@angular/material/table';
import { AdminService } from './admin.service';
import { BehaviorSubject } from 'rxjs';
import { ErrorService } from '../error/error.service';


@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, MatIconModule, MatTabsModule, MatTableModule],
  templateUrl: './admin.html',
  styleUrl: './admin.css',
})
export class AdminComponent {
  show: boolean = false;




  AnalyticsSubject = new BehaviorSubject<any>(null);
  analytics$ = this.AnalyticsSubject.asObservable();

  loading = false;

  displayedColumns: string[] = ['username', 'email', 'status', 'role'];

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


  constructor(private adminService: AdminService , private errorService: ErrorService) { }




  ngOnInit() {
    this.loadingAnalytics()
  }


  loadingAnalytics() {
    this.loading = true;
    this.adminService.getAnalytics().subscribe({
      next: (res) => {
        this.AnalyticsSubject.next(res.anyData)
        this.loading = false;
      },

      error: (err) => {
        this.loading = false
         this.loading = false;
        this.AnalyticsSubject.next(null);
        this.errorService.showMessage(`Error getting Analytics ):`, 'error');
      }

    })
  }

}
