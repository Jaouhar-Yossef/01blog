import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-card-blog',
  imports: [],
  templateUrl: './card-blog.html',
  styleUrl: './card-blog.css',
})
export class CardBlog {
  
  @Input() Blog: any;


  media = [

    { type: 'image', src: './../../assets/img5.jpg' },

    { type: 'video', src: './../../assets/vdback.mp4' },
    
    { type: 'image', src: './../../assets/img4.jpeg' },

  ];


  currentIndex = 0;
  intervalId: any;

  ngOnInit() {
    this.intervalId = setInterval(() => {
      this.next();
    }, 3000); 
  }

  next() {
    this.currentIndex = (this.currentIndex + 1) % this.media.length;
  }

  ngOnDestroy() {
    clearInterval(this.intervalId);
  }

}
