import { Component, OnInit, Input, Output, EventEmitter, OnDestroy } from '@angular/core';
import { LocalDataSource } from 'ng2-smart-table';
import { ActivatedRoute, Router, NavigationExtras } from '@angular/router';
import { takeUntil } from 'rxjs/operators';
import { Subject } from 'rxjs';
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


export interface Row {
  serviceName: string;
  serviceUri: string;
  created: string;
  status: SlStatusEnum;
  serviceId: string;
  slrId: string;
  locale: string;
}

@Component({
  selector: 'linked-services-smart-table',
  templateUrl: './linkedServices.component.html',
  styleUrls: [('./linkedServices.component.scss')]
})
export class LinkedServicesComponent implements OnInit, OnDestroy {

  serviceLabel: string = 'Service';
  createdLabel: string = 'Created';
  serviceUriLabel: string = 'Url';
  serviceEnabledLabel: string = 'Enabled';
  actionsMenuLabel: string = 'Actions';
  detailsLabel: string = 'Details';

  settings: any;
  private locale: string;
  source: LocalDataSource = new LocalDataSource();


  constructor(private service: LinkedServicesService, private route: ActivatedRoute, private router: Router,
    private translate: TranslateService, private configService: NgxConfigureService, private loginService: LoginService,
    private errorDialogService: ErrorDialogService, private toastrService: NbToastrService) {

    this.settings = this.loadTableSettings();
    this.locale = this.configService.config.i18n.locale;  // TODO change with user language preferences
  }


  async ngOnInit() {

    try {
      const serviceLinks: ServiceLinkRecordDoubleSigned[] = await this.service.getServiceLinks();

      this.source.load(serviceLinks.map(slr => {
        var date=new Date(slr.payload.iat).toLocaleString();
        return {
          serviceName: slr.payload.service_name,
          serviceUri: slr.payload.service_uri,
          created: date, 
          status: slr.serviceLinkStatuses[slr.serviceLinkStatuses.length - 1].payload.sl_status,
          serviceId: slr.payload.service_id,
          slrId: slr.payload.link_id,
          locale: this.locale
        } as Row;
      }));


    } catch (error) {
      if (error.error.statusCode === '401') {
        this.loginService.logout();
        this.router.navigate(['/login']);
      } else
        this.errorDialogService.openErrorDialog(error);
    }


    const queryParams = this.route.snapshot.queryParams;
    // Check input params to filter the table
    if (queryParams.serviceName || queryParams.serviceId)
      this.source.setFilter([{ field: 'serviceName', search: queryParams.serviceName }, { field: 'serviceId', search: queryParams.serviceId }]);

    // Open a Toastr if there is a message in input query
    if (queryParams.toastrMessage)
      this.toastrService.primary('', queryParams.toastrMessage, { position: NbGlobalLogicalPosition.BOTTOM_END, duration: 3500 });
  }

  ngOnDestroy() {
    console.log('ngOnDestroy');
  }

  private loadTableSettings() {

    this.serviceLabel = this.translate.instant('general.services.service');
    this.createdLabel = this.translate.instant('general.services.created');
    this.serviceUriLabel = this.translate.instant('general.services.url');
    this.serviceEnabledLabel = this.translate.instant('general.services.enabled');
    this.actionsMenuLabel = this.translate.instant('general.services.actions');
    this.detailsLabel = this.translate.instant('general.services.details');


    return {
      mode: 'external',
      attr: {
        class: 'table table-bordered'
      },
      actions: {
        add: false,
        edit: false,
        delete: false
      },
      add: {
        addButtonContent: '<i class="nb-plus"></i>',
        createButtonContent: '<i class="nb-checkmark"></i>',
        cancelButtonContent: '<i class="nb-close"></i>',
        confirmCreate: true
      },
      edit: {
        editButtonContent: '<i class="nb-edit"></i>',
        saveButtonContent: '<i class="nb-checkmark"></i>',
        cancelButtonContent: '<i class="nb-close"></i>',
        confirmSave: true
      },
      delete: {
        deleteButtonContent: '<i class="nb-trash"></i>',
        confirmDelete: true
      },
      columns: {
        serviceName: {
          title: this.serviceLabel,
          type: 'text',
          width: '50%'
        },
        serviceUri: {
          title: this.serviceUriLabel,
          sort: false,
          width: '5%',
          type: 'custom',
          filter: false,
          valuePrepareFunction: (cell, row) => row,
          renderComponent: ServiceUrlButtonRenderComponent
        },
        created: {
          title: this.createdLabel,
          type: 'text',
          width: '20%'         
        },
        details: {
          title: this.detailsLabel,
          filter: false,
          class: 'text-center',
          width: '5%',
          sort: false,
          type: 'custom',
          valuePrepareFunction: (cell, row) => row,
          renderComponent: LinkedServiceInfoRenderComponent
        },
        status: {
          title: this.serviceEnabledLabel,
          width: '5%',
          filter: false,
          sort: false,
          type: 'custom',
          valuePrepareFunction: (cell, row) => row,
          renderComponent: EnableServiceLinkButtonRenderComponent
        },
        actions: {
          title: this.actionsMenuLabel,
          sort: false,
          width: '5%',
          filter: false,
          type: 'custom',
          valuePrepareFunction: (cell, row) => row,
          renderComponent: ActionsServiceLinkMenuRenderComponent
        }
      },
    };
  }

  onDeleteConfirm(event): void {

    //if (event.source.data.length < 2 || event.data.status.serviceId == "_cape") {
    //  event.confirm.reject();
    //  return;
    //}
    //if (window.confirm('Are you sure you want to delete?')) {

    //  this.service.deleteServiceLink(event).subscribe(
    //    result => {
    //      event.confirm.resolve();
    //    },
    //    error => {
    //      if (error.error.statusCode == '401') {
    //        this.loginService.logout();
    //        this.router.navigate(['/login']);
    //      } else
    //        alert(`${error.error.statusCode}: ${error.error.message}`);
    //    });
    //} else {
    //  event.confirm.reject();
    //}
  }

  onCreateConfirm(event): void {
    console.log(event);
  }

  onEditConfirm(event): void {

    console.log(event);
  }

  resetfilters() {
    this.source.reset();
  }

}
