import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

import { MatDialogModule } from '@angular/material/dialog';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatButtonModule } from '@angular/material/button';

import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-report-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatInputModule,
    MatFormFieldModule,
    FormsModule,
    MatDialogModule
  ],
  templateUrl: './report.html',
  styleUrls: ['./report.css']
})
export class Report {

  reasonForm: FormGroup;

  confirming = false;

  constructor(
    public dialogRef: MatDialogRef<Report>,
    @Inject(MAT_DIALOG_DATA) public data: { type: string, targetId: string },
    private fb: FormBuilder
  ) {
    this.reasonForm = this.fb.group({
      reason: ['', [
        Validators.required,
        Validators.minLength(5),
        Validators.maxLength(200)
      ]]
    });
  }

  close() {
    this.dialogRef.close();
  }

  submit() {
    if (this.reasonForm.invalid) return;

    if (!this.confirming) {
      this.confirming = true;
      return;
    }

    const reason = this.reasonForm.value.reason.trim();
    this.dialogRef.close(reason);
  }

  back() {
    this.confirming = false;
  }
}