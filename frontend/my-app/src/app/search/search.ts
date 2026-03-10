import { Component, OnInit } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { BlogListComponent } from '../blog-list-component/blog-list-component';
import { Users } from '../users/users';

@Component({
  selector: 'app-search',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatButtonModule,
    BlogListComponent, 
    Users
  ],
  templateUrl: './search.html',
  styleUrls: ['./search.css'],
})
export class Search implements OnInit {

  searchControl = new FormControl<string>('');
  isBlogs = true;

  showTheBlogs = false;
  showTheUsers = false;

  ngOnInit() {
    this.searchControl.valueChanges
      .subscribe(value => {
        if (!value || value?.trim().length == 0) {
          this.showTheBlogs = false
          this.showTheUsers = false
        }
        if (this.isBlogs) {
          this.showTheBlogs = true
          this.showTheUsers = false 
        } else {
          this.showTheBlogs = false
          this.showTheUsers = true
        }
      });
  }

  showBlogs() {
    this.isBlogs = true;
    this.showTheBlogs = true;
    this.showTheUsers = false;
  }

  showUsers() {
    this.isBlogs = false;
    this.showTheBlogs = false;
    this.showTheUsers = true;
  }


}
