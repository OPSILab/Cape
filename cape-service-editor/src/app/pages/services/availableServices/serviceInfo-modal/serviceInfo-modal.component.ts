import { Component, OnInit } from '@angular/core';
import { NbDialogRef } from '@nebular/theme';

@Component({
  selector: 'add-service-modal',
  styleUrls: [('./serviceInfo-modal.component.scss')],
  templateUrl: './serviceInfo-modal.component.html'
})

export class ServiceInfoModalComponent implements OnInit {

  modalHeader: string;
  description: string;
  provider: any;
  purpose: any;
  keywords: any;
  data: any;
  serviceUri: any;
  processings: any;
  iconUrl: any;
  locale: string;

  constructor(protected ref: NbDialogRef<any>) {
  }

  ngOnInit() { }

  closeModal() {
    this.ref.close();
  }
}
