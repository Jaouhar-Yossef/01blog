import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-setting',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './setting.html',
  styleUrls: ['./setting.css'],
})
export class Setting {
  show: boolean = false;
}
