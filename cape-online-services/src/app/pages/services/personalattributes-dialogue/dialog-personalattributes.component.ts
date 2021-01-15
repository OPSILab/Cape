import { Component } from '@angular/core';
import { FormBuilder, FormControl, Validators } from '@angular/forms';
import { NbDialogRef } from '@nebular/theme';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'ngx-dialog-personalattributes',
  templateUrl: 'dialog-personalattributes.component.html',
  styleUrls: ['dialog-personalattributes.component.scss'],
})
export class DialogPersonalAttributesComponent {

  generalInformationForm = this.fb.group({
   
    address: new FormControl('', [
      Validators.required,
      Validators.minLength(3)]),
    email: new FormControl('', [
      Validators.required,
      Validators.pattern('^[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,4}$')]),
    phone: new FormControl('', [
      Validators.required,
      Validators.pattern('^[(]{0,1}[0-9]{3}[)]{0,1}[-\s\.]{0,1}[0-9]{3}[-\s\.]{0,1}[0-9]{4}$')])
  });
  accountId: string="";
  accountEmail: string="";
  accountAddress: string="";
  accountPhone: string="";

  constructor(protected ref: NbDialogRef<DialogPersonalAttributesComponent>, private fb: FormBuilder, private translateService: TranslateService,) {}

  

  async ngOnInit() {

    this.accountId = localStorage.getItem('accountId');
   
    this.accountEmail = localStorage.getItem('accountEmail')==="null" ? "": localStorage.getItem('accountEmail') ;
    this.accountAddress = localStorage.getItem('accountAddress')==="null" ? "": localStorage.getItem('accountAddress') ;
    this.accountPhone =localStorage.getItem('accountPhone') ==="null" ? "": localStorage.getItem('accountPhone');
    
   
    
    this.generalInformationForm.patchValue({
      address: this.accountAddress,
      email: this.accountEmail,
      phone: this.accountPhone
    });

   

  }
  
  
  cancel() {
    this.ref.close();
  }

  submit() {
    localStorage.setItem('accountEmail', this.generalInformationForm.value.email);
    localStorage.setItem('accountAddress', this.generalInformationForm.value.address);
    localStorage.setItem('accountPhone', this.generalInformationForm.value.phone);
    this.ref.close();
  }

  get address() { return this.generalInformationForm.get('address'); }
  get email() { return this.generalInformationForm.get('email'); }
  get phone() { return this.generalInformationForm.get('phone'); }
}
