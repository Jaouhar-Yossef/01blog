import { Injectable, signal } from '@angular/core';
import { Router } from '@angular/router';


@Injectable({ providedIn: 'root' })
export class BlogUiService {

  selectedBlog = signal<any>(null);


  constructor(private router: Router) {}


  openBlog(blog: any) {
    this.selectedBlog.set(blog);
  }

  closeBlog() {

    this.selectedBlog.set(null);
    this.router.navigate(['/home/']);
  }

}
