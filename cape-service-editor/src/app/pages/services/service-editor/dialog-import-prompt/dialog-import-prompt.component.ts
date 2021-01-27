import { Component } from '@angular/core';
import { NbDialogRef } from '@nebular/theme';
import { ErrorDialogService } from '../../../error-dialog/error-dialog.service';

@Component({
  selector: 'ngx-dialog-import-prompt',
  templateUrl: 'dialog-import-prompt.component.html',
  styleUrls: ['dialog-import-prompt.component.scss'],
})
export class DialogImportPromptComponent {

  selectedFile: File;
  json: string;

  constructor(protected ref: NbDialogRef<DialogImportPromptComponent>, private errorService: ErrorDialogService) { }

  cancel() {

    this.ref.close();
  }


  onFileChanged(event) {

    try {
      this.selectedFile = event.target.files[0];
      const fileReader = new FileReader();
      fileReader.readAsText(this.selectedFile, "UTF-8");
      fileReader.onload = () => {
        try {
          this.json = JSON.parse("" + fileReader.result);
        } catch (error) { this.errorService.openErrorDialog(error); this.ref.close(); }
      }

      fileReader.onerror = (error) => {
        this.errorService.openErrorDialog(error);
      }

    } catch (error) {

      this.errorService.openErrorDialog(error);
    }

  }

  onUpload() {
    // upload code goes here
    this.ref.close(this.json);
  }
}
