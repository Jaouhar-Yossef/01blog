import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';

import { MatButtonModule } from '@angular/material/button';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-mgs-admin',
  standalone: true,
  imports: [
    CommonModule,
    MatDialogModule,
    MatButtonModule
  ],
  templateUrl: './mgs-admin.html',
  styleUrl: './mgs-admin.css',
})
export class MgsAdhmin {


  msg = ''

  constructor(
    public dialogRef: MatDialogRef<MgsAdhmin>,
    @Inject(MAT_DIALOG_DATA) public data: { message: string } 
  ) { }

  open(message: string) {
    this.msg = message;
  }
  close() {
    this.dialogRef.close();
  }

}
