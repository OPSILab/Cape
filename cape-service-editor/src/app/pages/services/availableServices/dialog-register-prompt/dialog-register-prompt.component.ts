import { Component } from '@angular/core';
import { NbDialogRef } from '@nebular/theme';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'ngx-dialog-register-prompt',
  templateUrl: 'dialog-register-prompt.component.html',
  styleUrls: ['dialog-register-prompt.component.scss'],
})
export class DialogRegisterPromptComponent {

 serviceId:string;

  constructor(protected ref: NbDialogRef<any>, private translate: TranslateService) {}

  cancel() {
    this.ref.close();
  }

  confirmRegisterService(){ 
    console.log ("On confirm service"); 
    this.ref.close(true);
  }

  
}
