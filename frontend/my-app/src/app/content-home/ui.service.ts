import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UiService {

  private showSearchSubject = new BehaviorSubject<boolean>(false);

  showSearch$ = this.showSearchSubject.asObservable();

  showSearchHere(): boolean {
    return this.showSearchSubject.value;
  }

  openSearch() {
    console.log("==> hhhhh")
    this.showSearchSubject.next(true);
  }

  closeSearch() {
    this.showSearchSubject.next(false);
  }

  toggleSearch() {
    this.showSearchSubject.next(!this.showSearchSubject.value);
  }
}
