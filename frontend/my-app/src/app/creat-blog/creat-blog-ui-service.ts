import { Injectable, Input, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class CreatBlogUiService {
    showCreatBlogHere = signal(false);

    openCreatBlog() {
        this.showCreatBlogHere.set(true);
        console.log("in open: ", this.showCreatBlogHere())
    }

    closeCreatBlog() {
        this.showCreatBlogHere.set(false);
        console.log("in close: ", this.showCreatBlogHere())
    }
}