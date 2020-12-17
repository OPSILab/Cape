import { Component } from '@angular/core';
import { NbDialogRef } from '@nebular/theme';

@Component({
  selector: 'ngx-dialog-import-prompt',
  templateUrl: 'dialog-import-prompt.component.html',
  styleUrls: ['dialog-import-prompt.component.scss'],
})
export class DialogImportPromptComponent {
  selectedFile: File;
  json:string;
  constructor(protected ref: NbDialogRef<DialogImportPromptComponent>) {}

  cancel() {
    this.ref.close();
  }

  
  onFileChanged(event) {
    this.selectedFile = event.target.files[0];
    const fileReader = new FileReader(); 
    fileReader.readAsText(this.selectedFile, "UTF-8");
    fileReader.onload = () => {
    this.json= JSON.parse(""+fileReader.result); 
    
  }
  
  fileReader.onerror = (error) => {
      console.log(error);
    }
  }

  onUpload() {
  // upload code goes here
  this.ref.close(this.json);
  }
}
