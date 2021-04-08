import { Component, OnInit, OnDestroy } from '@angular/core';
import { LocalDataSource } from 'ng2-smart-table';
import { AvailableServicesService } from './availableServices.service';
import { ActivatedRoute } from '@angular/router';
import { LinkButtonRenderComponent } from './linkButtonRender.component';
import { ServiceInfoRenderComponent } from './serviceInfoRender.component';
import { LoginService } from '../../../login/login.service';
import { TranslateService } from '@ngx-translate/core';
import { NgxConfigureService } from 'ngx-configure';
import { ErrorDialogService } from '../../error-dialog/error-dialog.service';
import { NbToastrService, NbGlobalLogicalPosition } from '@nebular/theme';
import { ServiceEntry } from '../../../model/service-linking/serviceEntry';
import { AppConfig } from '../../../model/appConfig';

import { HumanReadableDescription } from '../../../model/humanReadableDescription';
import { Description2 } from '../../../model/description2';
import { ProcessingBasis } from '../../../model/processingBasis';
export interface AvailableServiceRow extends ServiceEntry {
  locale: string;
}

@Component({
  selector: 'available-services-smart-table',
  templateUrl: './availableServices.component.html',
  styleUrls: ['./availableServices.component.scss'],
})
export class AvailableServicesComponent implements OnInit, OnDestroy {
  private serviceLabel = 'Service';
  private descriptionLabel = 'Description';
  private actionsLabel = 'Actions';
  private detailsLabel = 'Details';

  public settings: Record<string, unknown>;
  private locale: string;
  public source: LocalDataSource = new LocalDataSource();
  private availableServices: ServiceEntry[];

  constructor(
    private availableServicesService: AvailableServicesService,
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
      this.availableServices = await this.availableServicesService.getRegisteredServices();
      void this.source.load(
        this.availableServices.map((availableServiceDescr) => {
          /* Get Localized Human readable description of the Service, default en */
          availableServiceDescr.humanReadableDescription = this.getLocalizedDescription(availableServiceDescr);

          /* Get Localized Purposes descriptions, default en */
          availableServiceDescr.processingBases = this.getLocalizedPurposesDescription(availableServiceDescr);

          return {
            ...availableServiceDescr,
            locale: this.locale,
          } as AvailableServiceRow;
        })
      );
    } catch (error) {
      // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
      if (error.error.statusCode === '401') {
        void this.loginService.logout().catch((error) => this.errorDialogService.openErrorDialog(error));
        // this.router.navigate(['/login']);
      } else this.errorDialogService.openErrorDialog(error);
    }

    // Open a Toastr if there is a message in input query
    const queryParams = this.route.snapshot.queryParams;
    if (queryParams.toastrMessage)
      this.toastrService.primary('', queryParams.toastrMessage, {
        position: NbGlobalLogicalPosition.BOTTOM_END,
        duration: 3500,
      });
  }

  private getLocalizedPurposesDescription(availableServiceDescr: ServiceEntry): ProcessingBasis[] {
    return availableServiceDescr.processingBases.map((processingBase) => {
      return {
        ...processingBase,
        description: processingBase.description.reduce((filtered: Description2[], description: Description2) => {
          if (this.locale !== 'en' && description.locale === this.locale) filtered = [description, ...filtered];
          else if (description.locale === 'en') filtered = [...filtered, description];
          return filtered;
        }, []),
      };
    });
  }

  private getLocalizedDescription(availableServiceDescr: ServiceEntry): HumanReadableDescription[] {
    return availableServiceDescr.humanReadableDescription.reduce((filtered: HumanReadableDescription[], description: HumanReadableDescription) => {
      if (this.locale !== 'en' && description.locale === this.locale) filtered = [description, ...filtered];
      else if (description.locale === 'en') filtered = [...filtered, description];
      return filtered;
    }, []);
  }

  ngOnDestroy(): void {
    console.log('ngOnDestroy');
  }

  loadTableSettings(): Record<string, unknown> {
    this.serviceLabel = this.translate.instant('general.services.service') as string;
    this.descriptionLabel = this.translate.instant('general.services.description') as string;
    this.actionsLabel = this.translate.instant('general.services.actions') as string;
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
          width: '25%',
          valuePrepareFunction: (cell, row: AvailableServiceRow) => row.name,
        },
        humanReadableDescription: {
          title: this.descriptionLabel,
          editor: {
            type: 'textarea',
          },
          width: '65%',
          valuePrepareFunction: (cell: HumanReadableDescription[]) => cell[0]?.description,
        },
        details: {
          title: this.detailsLabel,
          filter: false,
          sort: false,
          width: '5%',
          type: 'custom',
          // eslint-disable-next-line
          valuePrepareFunction: (cell, row: AvailableServiceRow) => row,
          renderComponent: ServiceInfoRenderComponent,
        },
        actions: {
          title: this.actionsLabel,
          filter: false,
          sort: false,
          width: '5%',
          type: 'custom',
          // eslint-disable-next-line
          valuePrepareFunction: (cell, row: AvailableServiceRow) => row,
          renderComponent: LinkButtonRenderComponent,
        },
      },
    };
  }

  resetfilters(): void {
    this.source.reset();
  }
}
