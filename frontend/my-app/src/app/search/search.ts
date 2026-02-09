import { Component, OnInit } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { debounceTime } from 'rxjs/operators';

@Component({
  selector: 'app-search',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatButtonModule
  ],
  templateUrl: './search.html',
  styleUrls: ['./search.css'],
})
export class Search implements OnInit {

  searchControl = new FormControl<string>('');
  isBlogs = true;

  results: any[] = [];

  ngOnInit() {
    this.searchControl.valueChanges
      .pipe(debounceTime(300))
      .subscribe(value => {
        if (value !== null) this.onSearch(value);
      });
  }

  showBlogs() {
    this.isBlogs = true;
    if (this.searchControl.value) this.onSearch(this.searchControl.value);
  }

  showUsers() {
    this.isBlogs = false;
    if (this.searchControl.value) this.onSearch(this.searchControl.value);
  }

  onSearch(query: string) {
    query = query.trim(); 
    if (!query) {
      this.results = [];
      return;
    }

    if (this.isBlogs) {
      console.log("search blogs:", query);

      
      
     
    } else {
      console.log("search users :", query);
      
    }
  }

}
