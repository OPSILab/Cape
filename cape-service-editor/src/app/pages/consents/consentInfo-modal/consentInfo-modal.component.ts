import { Component, OnInit } from '@angular/core';
import { NbDialogRef } from '@nebular/theme';

@Component({
  selector: 'consent-modal',
  styleUrls: [('./consentInfo-modal.component.scss')],
  templateUrl: './consentInfo-modal.component.html'
})

export class ConsentInfoModalComponent implements OnInit {

  modalHeader: string= "Consent Details";
  consent_record:string;
  cr_id: string = "";
  issued: string = "";
  status: string = "";
  service: string = "";
  source: string = "";
  userId: string = "";
  purposeCat: string = "";
  processingCat: string = "";
  storage: any;
  concepts: any = [];
  shareWith: any = [];
  consentSignedToken:string="";
  notarizationStatus:string="";
  notarizationLink:string="";
  policy:string="";
  collectionMethod:string="";
  termination: string="";
  operator: string="";
  history: any=[];


  //constructor(private activeModal: NgbActiveModal) {
  constructor(protected ref: NbDialogRef<any>) {
  }

  ngOnInit() { }

  closeModal() {
    this.ref.close();
  }
}
