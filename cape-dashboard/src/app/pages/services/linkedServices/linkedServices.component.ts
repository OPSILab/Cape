import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { LocalDataSource } from 'ng2-smart-table';
import { ActivatedRoute } from '@angular/router';
import { LinkedServiceInfoRenderComponent } from './linkedServiceInfoRender.component';
import { EnableServiceLinkButtonRenderComponent } from './enableServiceLinkButtonRender.component';
import { LinkedServicesService } from './linkedServices.service';

import { TranslateService } from '@ngx-translate/core';
import { NgxConfigureService } from 'ngx-configure';
import { LoginService } from '../../../login/login.service';
import { ActionsServiceLinkMenuRenderComponent } from './actionsServiceLinkMenuRender.component';
import { ServiceUrlButtonRenderComponent } from './serviceUrlButtonRender.component';
import { NbToastrService, NbGlobalLogicalPosition } from '@nebular/theme';
import { ServiceLinkRecordDoubleSigned } from '../../../model/service-linking/serviceLinkRecordDoubleSigned';
import { ErrorDialogService } from '../../error-dialog/error-dialog.service';
import { SlStatusEnum } from '../../../model/service-linking/serviceLinkStatusRecordPayload';
import { AppConfig } from '../../../model/appConfig';
import { ServiceLinkRecordPayload } from '../../../model/service-linking/serviceLinkRecordPayload';

export interface LinkedServiceRow extends ServiceLinkRecordPayload {
  created: string;
  status: SlStatusEnum;
  locale: string;
}

@Component({
  selector: 'linked-services-smart-table',
  templateUrl: './linkedServices.component.html',
  styleUrls: ['./linkedServices.component.scss'],
})
export class LinkedServicesComponent implements OnInit, OnDestroy {
  serviceLabel: string;
  createdLabel: string;
  serviceUriLabel: string;
  serviceEnabledLabel: string;
  actionsMenuLabel: string;
  detailsLabel: string;

  settings: Record<string, unknown>;
  private locale: string;
  source: LocalDataSource = new LocalDataSource();

  @ViewChild('linkedServiceInfoModal', { static: true }) linkedServiceInfoModal;

  constructor(
    private service: LinkedServicesService,
    private route: ActivatedRoute,
    private translate: TranslateService,
    private configService: NgxConfigureService,
    private loginService: LoginService,
    private errorDialogService: ErrorDialogService,
    private toastrService: NbToastrService
  ) {
    this.settings = this.loadTableSettings();
    this.locale = (this.configService.config as AppConfig).i18n.locale; // TODO change with user language preferences
  }

  async ngOnInit(): Promise<void> {
    try {
      const serviceLinks: ServiceLinkRecordDoubleSigned[] = await this.service.getServiceLinks();

      void this.source.load(
        serviceLinks.map((slr) => {
          return {
            ...slr.payload,
            created: new Date(slr.payload.iat).toLocaleString(),
            status: slr.serviceLinkStatuses[slr.serviceLinkStatuses.length - 1].payload.sl_status,
            locale: this.locale,
          } as LinkedServiceRow;
        })
      );
    } catch (error) {
      // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
      if (error.error.statusCode === '401') {
        this.loginService.logout().catch((error) => this.errorDialogService.openErrorDialog(error));
        // this.router.navigate(['/login']);
      } else this.errorDialogService.openErrorDialog(error);
    }

    const queryParams = this.route.snapshot.queryParams;
    // Check input params to filter the table
    if (queryParams.serviceName || queryParams.serviceId)
      this.source.setFilter([
        { field: 'serviceName', search: queryParams.serviceName as string },
        { field: 'serviceId', search: queryParams.serviceId as string },
      ]);

    // Open a Toastr if there is a message in input query
    if (queryParams.toastrMessage)
      this.toastrService.primary('', queryParams.toastrMessage, {
        position: NbGlobalLogicalPosition.BOTTOM_END,
        duration: 3500,
      });
  }

  ngOnDestroy(): void {
    console.log('ngOnDestroy');
  }

  private loadTableSettings(): Record<string, unknown> {
    this.serviceLabel = this.translate.instant('general.services.service') as string;
    this.createdLabel = this.translate.instant('general.services.created') as string;
    this.serviceUriLabel = this.translate.instant('general.services.url') as string;
    this.serviceEnabledLabel = this.translate.instant('general.services.enabled') as string;
    this.actionsMenuLabel = this.translate.instant('general.services.actions') as string;
    this.detailsLabel = this.translate.instant('general.services.details') as string;

    return {
      mode: 'external',
      attr: {
        class: 'table table-bordered',
      },
      actions: {
        add: false,
        edit: false,
        delete: false,
      },
      columns: {
        serviceName: {
          title: this.serviceLabel,
          type: 'text',
          width: '50%',
          valuePrepareFunction: (cell, row: LinkedServiceRow) => row.service_name,
        },
        serviceUri: {
          title: this.serviceUriLabel,
          sort: false,
          width: '5%',
          type: 'custom',
          filter: false,
          valuePrepareFunction: (cell, row: LinkedServiceRow) => row,
          renderComponent: ServiceUrlButtonRenderComponent,
        },
        created: {
          title: this.createdLabel,
          type: 'text',
          width: '20%',
        },
        details: {
          title: this.detailsLabel,
          filter: false,
          class: 'text-center',
          width: '5%',
          sort: false,
          type: 'custom',
          valuePrepareFunction: (cell, row: LinkedServiceRow) => row,
          renderComponent: LinkedServiceInfoRenderComponent,
        },
        status: {
          title: this.serviceEnabledLabel,
          width: '5%',
          filter: false,
          sort: false,
          type: 'custom',
          valuePrepareFunction: (cell, row: LinkedServiceRow) => row,
          renderComponent: EnableServiceLinkButtonRenderComponent,
        },
        actions: {
          title: this.actionsMenuLabel,
          sort: false,
          width: '5%',
          filter: false,
          type: 'custom',
          valuePrepareFunction: (cell, row: LinkedServiceRow) => row,
          renderComponent: ActionsServiceLinkMenuRenderComponent,
        },
      },
    };
  }

  resetfilters(): void {
    this.source.reset();
  }
}
