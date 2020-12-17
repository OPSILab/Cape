import { Component, Input, OnInit, ViewChild, ChangeDetectorRef } from '@angular/core';
import { AvailableServicesService } from './availableServices.service';
import { Router } from '@angular/router';
import { NbDialogService } from '@nebular/theme';
import { ErrorDialogService } from '../../error-dialog/error-dialog.service';
import { LoginService } from '../../../login/login.service';
import { NgxConfigureService } from 'ngx-configure';
import { LinkedServicesService } from '../linkedServices/linkedServices.service';
import { ServiceLinkRecordDoubleSigned } from '../../../model/service-linking/serviceLinkRecordDoubleSigned';
import { LinkingFromEnum } from '../../../model/service-linking/LinkingFromEnum';


@Component({
  template: `
	<button nbButton ghost [disabled]="isAlreadyLinked" shape="round" size="small" status="primary" (click)="startServiceLinking()">
         <i class="material-icons">link</i>
  </button>
  `,
})
export class LinkButtonRenderComponent implements OnInit {

  isAlreadyLinked: boolean = true;

  @Input() value;

  constructor(private configService: NgxConfigureService, private availableServicesService: AvailableServicesService,
    private linkedServicesService: LinkedServicesService, private router: Router, private modalService: NbDialogService,
    private errorDialogService: ErrorDialogService, private loginService: LoginService, private cdr: ChangeDetectorRef) { }

  async ngOnInit() {

    try {
      const linkedServiceResponse: ServiceLinkRecordDoubleSigned = await this.linkedServicesService.getServiceLinkByServiceId(this.value.serviceId);

    } catch (error) {
      if (error.status === 404) {
        this.isAlreadyLinked = false;
        this.cdr.detectChanges();
      } else
        this.errorDialogService.openErrorDialog(error);
    }
  }

  startServiceLinking() {
    this.router.navigate(['/serviceLinking'],
      {
        queryParams: {
          serviceId: this.value.serviceId,
          linkingFrom: LinkingFromEnum.Operator, locale: this.value.locale
        }
      });
  }


}
