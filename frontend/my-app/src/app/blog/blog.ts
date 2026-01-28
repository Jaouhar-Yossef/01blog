import { Component, inject, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BlogUiService } from './blog-ui.service';

@Component({
  selector: 'app-blog',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './blog.html',
  styleUrl: './blog.css',
})
export class Blog {
  @Input() blog!: any;
  ui = inject(BlogUiService);
}
