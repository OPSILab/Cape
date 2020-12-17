import { Component } from '@angular/core';
import { NbDialogRef } from '@nebular/theme';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'ngx-dialog-deregister-prompt',
  templateUrl: 'dialog-deregister-prompt.component.html',
  styleUrls: ['dialog-deregister-prompt.component.scss'],
})
export class DialogDeRegisterPromptComponent {

 serviceId:string;

  constructor(protected ref: NbDialogRef<any>, private translate: TranslateService) {}

  cancel() {
    this.ref.close();
  }

  confirmDeRegisterService(){ 
   
    this.ref.close(true);
  }

  
}
