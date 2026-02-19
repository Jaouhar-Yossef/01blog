import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Navbar } from '../navbar/navbar';

import { RouterModule } from '@angular/router';
import { AuthService } from '../auth/auth.service';
import { Router } from '@angular/router';
import { MgsAdhmin } from '../mgs-admin/mgs-admin';
import { MatDialog } from '@angular/material/dialog';


@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, Navbar, RouterModule],
  templateUrl: './home.html',
  styleUrls: ['./home.css']
})
export class HomeComponent {

  constructor(
    private authService: AuthService,
    private router: Router,
    private dialog: MatDialog
  ) { }

  ngOnInit() {
    const user = this.authService.getUser();
    if (!user || user == null) {
      this.router.navigate(['/']);
      return
    }
    if (user.status == "BANNED") {
      this.showAdminMessage();
    }
  }
  
  showAdminMessage() {
    const dialogRef = this.dialog.open(MgsAdhmin, {
      width: '400px',
      disableClose: true,
      data: { message: "You are banned from this platform. You can't perform any actions." }
    });
  }
}