import { Injectable, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class BlogUiService {
  showBlog = signal(false);
  selectedBlog = signal<any>(null);

  openBlog(blog: any) {
    this.selectedBlog.set(blog);
    this.showBlog.set(true);
  }

  closeBlog() {
    this.showBlog.set(false);
    this.selectedBlog.set(null);
  }
}
