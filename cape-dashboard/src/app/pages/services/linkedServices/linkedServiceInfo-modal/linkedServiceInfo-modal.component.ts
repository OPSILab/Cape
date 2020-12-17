import { Component, OnInit } from '@angular/core';
import { NbDialogRef } from '@nebular/theme';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'add-service-modal',
  styleUrls: [('./linkedServiceInfo-modal.component.scss')],
  templateUrl: './linkedServiceInfo-modal.component.html'
})

export class LinkedServiceInfoModalComponent implements OnInit {

  modalHeader: string = "";
  description: string = "";
  created: any = "";
  serviceUri: any = "";
  slrId: any = "";
  data: any = [];
  provider: any = "";
  processings: any = [];
  keywords: any = [];
  serviceId: any = "";
  iconUrl: any = "";
  locale: string;

  constructor(protected ref: NbDialogRef<unknown>, private router: Router, private route: ActivatedRoute) {
  }

  ngOnInit() { }

  closeModal() {
    this.ref.close();
  }

  openConsents() {
    console.log("Open Consents:" + this.route.snapshot['_routerState'].url + "- serviceID " + this.serviceId);
    this.router.navigate(['/pages/consents', { serviceId: this.serviceId, serviceName: this.modalHeader }]);
    this.ref.close();
  }
}
