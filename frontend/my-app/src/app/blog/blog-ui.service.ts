import { Injectable, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class BlogUiService {
  showBlog = signal(false);
  bigContent = signal(false);
  selectedBlog = signal<any>(null);

  openBlog(blog: any) {
    this.selectedBlog.set(blog);
    const value = blog?.content; 
    if ( value.length > 30) {
      this.bigContent.set(true)
    } else {
      this.bigContent.set(false)
    }

    this.showBlog.set(true);
  }

  closeBlog() {
    this.showBlog.set(false);
    this.selectedBlog.set(null);
  }

  setBigContent() {
    this.bigContent.set(!this.bigContent)
  }
}
