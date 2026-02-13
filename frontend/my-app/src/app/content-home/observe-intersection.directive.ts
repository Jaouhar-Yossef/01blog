import { Directive, ElementRef, EventEmitter, Output } from '@angular/core';
import { IntersectionService } from './intersection.service';

@Directive({
    selector: '[observeIntersection]'
})
export class ObserveIntersectionDirective {

    @Output() visible = new EventEmitter<void>();

    constructor(
        private el: ElementRef,
        private service: IntersectionService
    ) { }

    ngAfterViewInit() {
        this.service.observe(this.el.nativeElement, () => {
            this.visible.emit();
        });
    }

    ngOnDestroy() {
        this.service.unobserve(this.el.nativeElement);
    }
}
