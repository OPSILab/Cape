import { Component, Input, OnInit, ChangeDetectorRef } from '@angular/core';
import { Router } from '@angular/router';
import { ErrorDialogService } from '../../../error-dialog/error-dialog.service';
import { LinkedServicesService } from '../linkedServices/linkedServices.service';
import { LinkingFromEnum } from '../../../model/service-linking/LinkingFromEnum';
import { AvailableServiceRow } from './availableServices.component';

@Component({
  template: `
    <button nbButton ghost [disabled]="isAlreadyLinked" shape="round" size="small" status="primary" (click)="startServiceLinking()">
      <i class="material-icons">link</i>
    </button>
  `,
})
export class LinkButtonRenderComponent implements OnInit {
  @Input() value: AvailableServiceRow;

  isAlreadyLinked = true;

  constructor(
    private linkedServicesService: LinkedServicesService,
    private router: Router,
    private errorDialogService: ErrorDialogService,
    private cdr: ChangeDetectorRef
  ) {}

  async ngOnInit(): Promise<void> {
    try {
      await this.linkedServicesService.getServiceLinkByServiceId(this.value.serviceId);
    } catch (error) {
      // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
      if (error.status === 404) {
        this.isAlreadyLinked = false;
        this.cdr.detectChanges();
      } else this.errorDialogService.openErrorDialog(error);
    }
  }

  startServiceLinking(): void {
    void this.router.navigate(['/serviceLinking'], {
      queryParams: {
        serviceId: this.value.serviceId,
        linkingFrom: LinkingFromEnum.Operator,
        locale: this.value.locale,
      },
    });
  }
}
