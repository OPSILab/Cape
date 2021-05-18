import { Component } from '@angular/core';
import { LocalDataSource } from 'ng2-smart-table';
import { ConsentInfoLinkRenderComponent } from './consentInfoRender.component';
import { ConsentsService } from './consents.service';
import { Router, ActivatedRoute } from '@angular/router';
import { ErrorDialogService } from '../error-dialog/error-dialog.service';
import { TranslateService } from '@ngx-translate/core';
import { NgxConfigureService } from 'ngx-configure';
import { LoginService } from '../../auth/login/login.service';
import { QuerySortEnum } from '../../model/querySortEnum';
import { ConsentRecordSigned } from '../../model/consents/consentRecordSigned';
import { FormControl, FormGroup } from '@angular/forms';

@Component({
  selector: 'consents-smart-table',
  templateUrl: './consents.component.html',
  styleUrls: ['./consents.component.scss'],
})
export class ConsentsComponent {
  private serviceLabel: string = 'Service';
  private sourceLabel: string = 'Source';
  private surrogateLabel: string = 'User Id';
  private purposeLabel: string = 'Purpose';
  private issuedLabel: string = 'Issued';
  private statusLabel: string = 'Status';

  public settings: unknown;
  private locale: string;

  source: LocalDataSource = new LocalDataSource();

  public filtersForm = new FormGroup({
    user: new FormControl(),
  });

  constructor(
    private consentsService: ConsentsService,
    private translate: TranslateService,
    private configService: NgxConfigureService,
    private loginService: LoginService,
    private errorDialogService: ErrorDialogService
  ) {
    this.settings = this.loadTableSettings();
  }

  async ngOnInit() {
    try {
      this.locale = this.configService.config.i18n.locale; // TODO change with user language preferences
      this.source.load((await this.consentsService.getConsents(QuerySortEnum.ASC)).filter((consent) => consent.payload.common_part.role == 'Sink'));
    } catch (error) {
      // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
      if (error?.error?.statusCode === '401') {
        this.loginService.logout().catch((error) => this.errorDialogService.openErrorDialog(error));
      } else this.errorDialogService.openErrorDialog(error);
    }
  }

  loadTableSettings() {
    this.serviceLabel = this.translate.instant('general.services.service');
    this.sourceLabel = this.translate.instant('general.consents.dataprovider');
    this.surrogateLabel = this.translate.instant('general.consents.surrogateid');
    this.purposeLabel = this.translate.instant('general.consents.purpose');
    this.issuedLabel = this.translate.instant('general.consents.issued');
    this.statusLabel = this.translate.instant('general.consents.status');

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
          valuePrepareFunction: (cell, row: ConsentRecordSigned) => row.payload.common_part.subject_name,
        },
        serviceSource: {
          title: this.sourceLabel,
          type: 'text',

          valuePrepareFunction: (cell, row: ConsentRecordSigned) => row.payload.common_part.source_subject_name,
        },
        surrogateId: {
          title: this.surrogateLabel,
          editor: {
            type: 'text',
          },
          valuePrepareFunction: (cell, row: ConsentRecordSigned) => row.payload.common_part.surrogate_id,
        },
        purpose: {
          title: this.purposeLabel,
          editor: {
            type: 'text',
          },
          valuePrepareFunction: (cell, row: ConsentRecordSigned) =>
            row.payload.common_part.rs_description.resource_set.datasets.map((d) => d.purposeName).join(', '),
        },
        issued: {
          title: this.issuedLabel,
          editor: {
            type: 'text',
          },
          valuePrepareFunction: (cell, row: ConsentRecordSigned) => new Date(row.payload.common_part.iat).toLocaleString(),
        },
        status: {
          title: this.statusLabel,
          editor: {
            type: 'text',
            valuePrepareFunction: (cell, row: ConsentRecordSigned) => row.payload.common_part.consent_status,
          },
        },
        viewInfo: {
          filter: false,
          type: 'custom',
          valuePrepareFunction: (cell, row: ConsentRecordSigned) => row,
          renderComponent: ConsentInfoLinkRenderComponent,
        },
      },
    };
  }

  /**************************
   * FILTERING
   ***************************/

  async onFilterSubmit(): Promise<void> {
    this.source.load(
      (await this.consentsService.getConsentsByUserIdAndQuery(this.filtersForm.get('user').value, QuerySortEnum.ASC)).filter(
        (consent) => consent.payload.common_part.role == 'Sink'
      )
    );
  }

  resetFilters(): void {
    this.filtersForm.reset({
      user: '',
    });

    void this.ngOnInit();
  }

  resetFilter(controlName: string): void {
    this.filtersForm.get(controlName)?.setValue('');
  }
}
