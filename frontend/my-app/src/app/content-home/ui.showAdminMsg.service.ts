import { Injectable } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { MgsAdhmin } from "../mgs-admin/mgs-admin";

@Injectable({ providedIn: 'root' })
export class ShowAdminMessage {

    constructor(private dialog: MatDialog) { }
    
    showAdminMessageBlogHidden() {
        const dialogRef = this.dialog.open(MgsAdhmin, {
            width: '400px',
            disableClose: true,
            data: { message: "This blog has been hidden by the admin." },
        });
    }

    showAdminMessageUserBanned() {
        const dialogRef = this.dialog.open(MgsAdhmin, {
            width: '400px',
            disableClose: true,
            data: { message: "You are banned from this platform. You can't perform any actions." },
        });
    }

}