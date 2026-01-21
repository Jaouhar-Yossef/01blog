import { Component, Input } from '@angular/core';
import { CardBlog } from '../card-blog/card-blog';
import { ContentHomeService } from './content-home.service';

@Component({
  selector: 'app-content-home',
  imports: [CardBlog],
  templateUrl: './content-home.html',
  styleUrl: './content-home.css',
})
export class ContentHome {

  Blogs: any[] = [];
  constructor(private postService: ContentHomeService) {}

  ngOnInit() {
    this.postService.getBlogs().subscribe({
      next: data => this.Blogs = data,
      error: err => console.error(err)
    });
  }

}
