import { Component, inject } from '@angular/core';
import { Setting } from '../setting/setting';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { CreatBlog } from '../creat-blog/creat-blog';

import { CreatBlogUiService } from '../creat-blog/creat-blog-ui-service';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [Setting, CommonModule , CreatBlog , MatButtonModule , MatIconModule , RouterModule],
  templateUrl: './navbar.html',
  styleUrls: ['./navbar.css']
})

export class Navbar {

  constructor(private router: Router) {}
  isHome(): boolean {
    return this.router.url !== '/';
  }
  isRoot(): boolean {
    return this.router.url === '/';
  }


  private uiui = inject(CreatBlogUiService)
  
  creatBlog() {
    if (this.uiui.showCreatBlogHere()) {
      this.uiui.closeCreatBlog()
      return
    }
   
    this.uiui.openCreatBlog()
  }
}
