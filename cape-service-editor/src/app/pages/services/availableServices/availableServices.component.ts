import { Component, OnInit, Input, Output, EventEmitter, OnDestroy } from '@angular/core';
import { LocalDataSource } from 'ng2-smart-table';
import { AvailableServicesService } from './availableServices.service';
import { Router, NavigationExtras, ActivatedRoute } from '@angular/router';
import { ServiceInfoRenderComponent } from './serviceInfoRender.component';
import { LoginService } from '../../../login/login.service';
import { TranslateService } from '@ngx-translate/core';
import { NgxConfigureService } from 'ngx-configure';
import { ErrorDialogService } from '../../error-dialog/error-dialog.service';
import { NbToastrService, NbGlobalLogicalPosition } from '@nebular/theme';
import { ServiceEntry } from '../../../model/service-linking/serviceEntry';
import { RegisterButtonRenderComponent } from './registerButtonRender.component';
import { ActionsServiceMenuRenderComponent } from './actionsServiceMenuRender.component';

@Component({
  selector: 'available-services-smart-table',
  templateUrl: './availableServices.component.html',
  styleUrls: [('./availableServices.component.scss')]
})
export class AvailableServicesComponent implements OnInit, OnDestroy {

  private serviceLabel: string = 'Service';
  private descriptionLabel: string = 'Description';
  private actionsLabel: string = 'Actions';
  private detailsLabel: string = 'Details';

  public settings: unknown;
  private locale: string;
  public source: LocalDataSource = new LocalDataSource();
  private availableServices: ServiceEntry[];

  constructor(private service: AvailableServicesService, private route: ActivatedRoute, private router: Router,
    private translate: TranslateService, private configService: NgxConfigureService,
    private loginService: LoginService, private errorDialogService: ErrorDialogService, private toastrService: NbToastrService) {

    this.settings = this.loadTableSettings();
    this.locale = this.configService.config.i18n.locale;  // TODO change with user language preferences
  }


  async ngOnInit() {

    try {
      this.availableServices = await this.service.getServices();
      this.source.load(this.availableServices.map(serviceDescr => {

        /* Get Localized Human readable description of the Service, default en */
        const localizedHumanReadableDescription = serviceDescr.humanReadableDescription.filter(d =>
          d.locale === this.locale)[0] || serviceDescr.humanReadableDescription.filter(d => d.locale === 'en')[0];

        /* Get Localized Purposes descriptions, default en */
        serviceDescr.processingBases = serviceDescr.processingBases.map(b => {
          const firstMatch = b.description.filter(d =>
            d.locale === this.locale);

          b.description = firstMatch.length > 0 ? firstMatch : b.description.filter(d => d.locale === 'en');
          return b;
        });

        return {
          serviceId: serviceDescr.serviceId,
          serviceName: serviceDescr.name,
          humanReadableDescription: localizedHumanReadableDescription,
          serviceDescription: serviceDescr,
          locale: this.locale
        };
      }));

    } catch (error) {
      this.errorDialogService.openErrorDialog(error);
    }


    // Open a Toastr if there is a message in input query
    const queryParams = this.route.snapshot.queryParams;
    if (queryParams.toastrMessage)
      this.toastrService.primary('', queryParams.toastrMessage, { position: NbGlobalLogicalPosition.BOTTOM_END, duration: 3500 });

  }

  ngOnDestroy() {

  }

  loadTableSettings() {

    this.serviceLabel = this.translate.instant('general.services.service');
    this.descriptionLabel = this.translate.instant('general.services.description');
    this.actionsLabel = this.translate.instant('general.services.actions');
    this.detailsLabel = this.translate.instant('general.services.details');

    return {
      mode: 'external',
      attr: {
        class: 'table table-bordered'
      },
      actions: {
        add: true,
        edit: false,
        delete: false,
        columnTitle: ""
      },
      add: {
        addButtonContent: '<i class="nb-plus"></i>',
        createButtonContent: '<i class="nb-checkmark"></i>',
        cancelButtonContent: '<i class="nb-close"></i>',
      },
      edit: {
        editButtonContent: '<i class="nb-edit"></i>',
        saveButtonContent: '<i class="nb-checkmark"></i>',
        cancelButtonContent: '<i class="nb-close"></i>',
      },
      delete: {
        deleteButtonContent: '<i class="nb-trash"></i>',
        confirmDelete: true,
      },
      columns: {

        serviceName: {
          title: this.serviceLabel,
          type: 'text',
          width: '25%'
        },
        humanReadableDescription: {
          title: this.descriptionLabel,
          editor: {
            type: 'textarea'
          },
          width: '65%',
          valuePrepareFunction: (cell) => cell.description
        },
        details: {
          title: this.detailsLabel,
          filter: false,
          sort: false,
          width: '5%',
          type: 'custom',
          valuePrepareFunction: (cell, row) => row,
          renderComponent: ServiceInfoRenderComponent
        },
        /* actions: {
           title: this.actionsLabel,
           filter: false,
           sort: false,
           width: '5%',
           type: 'custom',
           valuePrepareFunction: (cell, row) => row,
           renderComponent: RegisterButtonRenderComponent,
           onComponentInitFunction: (instance) => {
             instance.updateResult.subscribe(updatedServiceData => {
               this.handleUpdatedService(updatedServiceData);
             });
           }
         },*/

        actions: {
          sort: false,
          width: '5%',
          filter: false,
          type: 'custom',
          valuePrepareFunction: (cell, row) => row,
          renderComponent: ActionsServiceMenuRenderComponent,
          onComponentInitFunction: (instance) => {
            instance.updateResult.subscribe(updatedServiceData => {
              this.handleUpdatedService(updatedServiceData);
            });
          }
        }

      }
    };
  }

  handleUpdatedService(updatedServiceData: any) {
    this.source.refresh();
  }


  onCreate(event): void {
    this.router.navigate(['/pages/services/service-editor', {}]);
  }



  onDeleteConfirm(event): void {
    if (window.confirm('Are you sure you want to delete?')) {
      event.confirm.resolve();
    } else {
      event.confirm.reject();
    }
  }


  onEdit(event): void {
    console.log(event.data)
    this.router.navigate(['/pages/services/service-editor', { "serviceId": event.data.serviceId }]);
  }

  resetfilters() {
    this.source.reset();
  }

}
