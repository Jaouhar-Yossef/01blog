import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class IntersectionService {

  private io = new IntersectionObserver(entries => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        this.map.get(entry.target)?.();
      }
    });
  });

  private map = new Map<Element, () => void>();

  observe(el: Element, cb: () => void) {
    this.map.set(el, cb);
    this.io.observe(el);
  }

  unobserve(el: Element) {
    this.map.delete(el);
    this.io.unobserve(el);
  }
}
