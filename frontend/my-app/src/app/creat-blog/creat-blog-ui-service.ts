import { Injectable, Input, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class CreatBlogUiService {
    showCreatBlogHere = signal(false);

    openCreatBlog() {
        this.showCreatBlogHere.set(true);
    }

    closeCreatBlog() {
        this.showCreatBlogHere.set(false);
    }
}