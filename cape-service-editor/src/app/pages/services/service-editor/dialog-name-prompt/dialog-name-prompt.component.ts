import { Component } from '@angular/core';
import { NbDialogRef } from '@nebular/theme';

@Component({
  selector: 'ngx-dialog-name-prompt',
  templateUrl: 'dialog-name-prompt.component.html',
  styleUrls: ['dialog-name-prompt.component.scss'],
})
export class DialogNamePromptComponent {
  constructor(protected ref: NbDialogRef<DialogNamePromptComponent>) {}

  cancel(): void {
    this.ref.close();
  }

  submit(name: string): void {
    this.ref.close(name);
  }
}
