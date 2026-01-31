import { Component, inject, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BlogUiService } from './blog-ui.service';

import { MatButtonModule } from '@angular/material/button';

import { RouterModule } from '@angular/router';

import { MatIconModule } from '@angular/material/icon';

import {MatInputModule} from '@angular/material/input';
import {MatFormFieldModule} from '@angular/material/form-field';

@Component({
  selector: 'app-blog',
  standalone: true,
  imports: [CommonModule , MatButtonModule ,MatFormFieldModule, MatInputModule ,RouterModule , MatIconModule],
  templateUrl: './blog.html',
  styleUrl: './blog.css',
})
export class Blog {
  @Input() blog!: any;
  ui = inject(BlogUiService);
  AllTheDiscription : boolean = false;
  showTheComment : boolean = false;

  showAllTheDiscription() {
    this.AllTheDiscription = !this.AllTheDiscription;
  }

  showLess () {
    this.AllTheDiscription = !this.AllTheDiscription;
  }

  showComment() {
    this.showTheComment = !this.showTheComment;
  }
}
