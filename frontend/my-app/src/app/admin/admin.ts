import { Component } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';





// import { ActivatedRoute, Router } from '@angular/router';

// import { ErrorService } from '../error/error.service';

// import { CommonModule } from '@angular/common';


// import { BlogListComponent } from '../blog-list-component/blog-list-component';

// import { BehaviorSubject, Observable, of } from 'rxjs';

// import { ApiResponse } from '../content-home/content-home.service';



// import { MatDividerModule } from '@angular/material/divider';

// import { MatButtonModule } from '@angular/material/button';

// import { Users } from '../users/users';
import {MatTabsModule} from '@angular/material/tabs';

import { CommonModule } from '@angular/common'; 

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule , MatIconModule , MatTabsModule],
  templateUrl: './admin.html',
  styleUrl: './admin.css',
})
export class AdminComponent {
  show: boolean = true;

  



}
