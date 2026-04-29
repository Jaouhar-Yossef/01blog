import { Injectable } from "@angular/core";

@Injectable({
  providedIn: 'root'
})
export class ImageService {

  baseUrl = 'http://localhost:8080';

  getUserImage(imgUrl?: string): string {
    if (!imgUrl || imgUrl.trim().length === 0) {
      return "./../../assets/blank-profile-picture-973460_640.webp";
    }
    return this.baseUrl + imgUrl;
  }

  getBlogImage(imgUrl?: string): string {
    if (!imgUrl || imgUrl.trim().length === 0) {
      return "./../../assets/images/close.svg";
    }
    return this.baseUrl + imgUrl;
  }

  onImgUserError(event: any) {
    event.target.src = "./../../assets/blank-profile-picture-973460_640.webp";
  }

  onImgBlogError(event: any) {
    event.target.src = "./../../assets/images/close.svg";
  }
}