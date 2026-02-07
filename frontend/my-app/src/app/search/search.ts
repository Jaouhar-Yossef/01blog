import { Component, inject } from '@angular/core';
import { FormControl } from '@angular/forms';
import { CommonModule } from '@angular/common';

import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { ReactiveFormsModule } from '@angular/forms';
import { UiService } from './../content-home/ui.service'

@Component({
  selector: 'app-search',
  standalone: true,
  imports: [
    CommonModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatButtonModule,
    ReactiveFormsModule
  ],
  templateUrl: './search.html',
  styleUrl: './search.css',
})
export class Search {
  searchControl = new FormControl('');

  ui = inject(UiService);

  
  closeSearch() {
    this.ui.closeSearch();
  }
}
