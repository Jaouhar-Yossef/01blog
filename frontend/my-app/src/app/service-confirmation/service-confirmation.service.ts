import { Injectable } from '@angular/core';


@Injectable({ providedIn: 'root' })
export class ServiceConfirmation {

  show = false;
  text = '';

  private onConfirm?: () => void;
  private onCancel?: () => void;

  open(
    text: string,
    onConfirm: () => void,
    onCancel?: () => void
  ) {
    this.text = text;
    this.show = true;
    this.onConfirm = onConfirm;
    this.onCancel = onCancel;
  }

  confirm() {
    this.show = false;
    this.onConfirm?.();
  }

  cancel() {
    this.show = false;
    this.onCancel?.();
  }
}

