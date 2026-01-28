import { Component, inject } from '@angular/core';
import { Setting } from '../setting/setting';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { CreatBlog } from '../creat-blog/creat-blog';
import { BlogUiService } from '../blog/blog-ui.service';
import { CreatBlogUiService } from '../creat-blog/creat-blog-ui-service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [Setting, CommonModule , CreatBlog],
  templateUrl: './navbar.html',
  styleUrls: ['./navbar.css']
})

export class Navbar {

  constructor(private router: Router) {}
  isHome(): boolean {
    return this.router.url === '/home';
  }
  isRoot(): boolean {
    return this.router.url === '/';
  }

  private ui = inject(BlogUiService)
  private uiui = inject(CreatBlogUiService)
  
  creatBlog() {
    if (this.uiui.showCreatBlogHere()) {
      this.uiui.closeCreatBlog()
      return
    }
    if (this.ui.showBlog()) {
      this.ui.closeBlog()
    }
    this.uiui.openCreatBlog()
  }
}
