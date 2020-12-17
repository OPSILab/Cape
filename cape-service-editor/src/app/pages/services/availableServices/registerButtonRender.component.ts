import { Component, Input, Output, OnInit, TemplateRef, ChangeDetectorRef, EventEmitter } from '@angular/core';
import { AvailableServicesService } from './availableServices.service';
import { Router } from '@angular/router';

import { ErrorDialogService } from '../../error-dialog/error-dialog.service';
import { NbToastrService,  NbDialogService, NbDialogRef,NbGlobalLogicalPosition } from '@nebular/theme';

import { TranslateService } from '@ngx-translate/core';



@Component({
  template: `
	<button nbButton ghost [disabled]="isAlreadyRegistered" [title]="titleButton" shape="round" size="small" status="primary" (click)="openRegisterService(registerService)">
         <i class="material-icons">playlist_add_check</i>
         
  </button>
  <ng-template #registerService let-ref="dialogRef">
  <nb-card>
    <nb-card-header class="d-flex justify-content-between">
      <h5>{{'general.services.register_service'|translate}}</h5>
      <button nbButton ghost shape="rectangle" size="tiny" class="close" (click)="ref.close()">
        <i class="material-icons">close</i>
      </button>
    </nb-card-header>
    <nb-card-body class="p-5">{{ 'general.services.register_services_message'|translate }}</nb-card-body>
    <nb-card-footer class="d-flex justify-content-center">
      <button nbButton status="danger" size="small"
              (click)="onRegisterService(ref)">
        {{'general.services.register_service'|translate}}
      </button>
      <button nbButton class="ml-2" ghost shape="rectangle" status="primary" (click)="ref.close()">
        {{'general.services.close' | translate}}
      </button>
    </nb-card-footer>
  </nb-card>
</ng-template>
  `,
})
export class RegisterButtonRenderComponent implements OnInit {

  isAlreadyRegistered: boolean = true;
  titleButton: string = "Already Registered"

  @Input() value;

  @Output() updateResult = new EventEmitter<any>();

  constructor( private service: AvailableServicesService,
    private router: Router, private errorDialogService: ErrorDialogService, 
 private toastrService: NbToastrService,private dialogService: NbDialogService,private translate: TranslateService
    
    ) { }

  async ngOnInit() {
    
    var cert = this.value.serviceDescription.serviceInstance.cert?.x5c;
      if(cert == undefined || cert == null || cert.length == 0){
        
          this.isAlreadyRegistered=false;
          this.titleButton="Register Service"
      } 
  }

  async  registerService() {
    console.log("EDIT: "+  this.isAlreadyRegistered)
    console.log (this.value.serviceDescription.serviceInstance.cert.x5c);

    this.isAlreadyRegistered=true;
  }
  
  openRegisterService = (registerServiceTemplate: TemplateRef<unknown>) => {

    this.dialogService.open(registerServiceTemplate, {
      hasScroll: false
    });
  }


  onRegisterService = async (dialogRef: NbDialogRef<unknown>) => {

    try {
      this.value.serviceDescription = await this.service.registerService(this.value.serviceDescription.serviceId);

      dialogRef.close();
      this.toastrService.primary('', this.translate.instant('general.services.service_registered_message', { serviceName: this.value.serviceDescription.serviceName }),
      { position: NbGlobalLogicalPosition.BOTTOM_END, duration: 2500 });
      this.updateResult.emit(this.value.serviceDescription);
      
    } catch (error) {
      if (error.error?.statusCode === '401') {
         this.router.navigate(['/login']);
      } else
        this.errorDialogService.openErrorDialog(error);
    }
  }

}
