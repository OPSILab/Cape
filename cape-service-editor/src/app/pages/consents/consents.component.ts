import { Component } from '@angular/core';
import { LocalDataSource } from 'ng2-smart-table';
import { ConsentInfoLinkRenderComponent } from './consentinfo-link-render.component';
import { ConsentsService } from './consents.service';
import { Router, ActivatedRoute } from '@angular/router';
import { ErrorDialogService } from '../error-dialog/error-dialog.service';
import { TranslateService } from '@ngx-translate/core';
import { NgxConfigureService } from 'ngx-configure';
import { LoginService } from '../../auth/login/login.service';
import { QuerySortEnum } from '../../model/querySortEnum';

@Component({
  selector: 'consents-smart-table',
  templateUrl: './consents.component.html',
  styleUrls: ['./consents.component.scss'],
})
export class ConsentsComponent {
  private serviceLabel: string = 'Service';
  private sourceLabel: string = 'Source';
  private userLabel: string = 'User Id';
  private purposeLabel: string = 'Purpose';
  private issuedLabel: string = 'Issued';
  private statusLabel: string = 'Status';

  public settings: unknown;
  private locale: string;
  consents: any;

  source: LocalDataSource = new LocalDataSource();

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
      this.consents = await this.consentsService.getConsents(QuerySortEnum.ASC);

      this.locale = this.configService.config.i18n.locale; // TODO change with user language preferences

      this.source.load(
        this.consents.reduce((filtered, elem) => {
          if (elem.payload.common_part.role === 'Sink') {
            var date = new Date(elem.payload.common_part.iat).toLocaleString();
            filtered.push({
              id: elem.payload.common_part.cr_id,
              serviceName: elem.payload.common_part.subject_name,
              serviceSource: elem.payload.common_part.source_name,
              userId: elem.payload.common_part.surrogate_id,
              purpose: elem.payload.role_specific_part.usage_rules.purposeName,
              issued: date,
              status: elem.payload.common_part.consent_status,
              viewInfo: elem,
            });
          }
          return filtered;
        }, [])
      );
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
    this.userLabel = this.translate.instant('general.consents.userid');
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
        },
        serviceSource: {
          title: this.sourceLabel,
          type: 'text',
        },
        userId: {
          title: this.userLabel,
          editor: {
            type: 'text',
          },
        },
        purpose: {
          title: this.purposeLabel,
          editor: {
            type: 'text',
          },
        },
        issued: {
          title: this.issuedLabel,
          editor: {
            type: 'text',
          },
        },
        status: {
          title: this.statusLabel,
          editor: {
            type: 'text',
          },
        },
        viewInfo: {
          filter: false,
          type: 'custom',
          valuePrepareFunction: (cell, row) => row,
          renderComponent: ConsentInfoLinkRenderComponent,
        },
      },
    };
  }
}
