import { Component, OnInit, OnDestroy } from '@angular/core';
import { LoginService } from '../../login/login.service';
import { Router } from '@angular/router';
import { ErrorDialogService } from '../error-dialog/error-dialog.service';
import { AuditLogsService } from '../auditlogs/auditlogs.service';
import { AvailableServicesService } from '../services/availableServices/availableServices.service';
import { TranslateService } from '@ngx-translate/core';
import { AuditLog } from '../../model/auditlogs/auditlogs.model';

interface CardSettings {
  title: string;
  iconClass: string;
  type: string;
  value: string;
}


@Component({
  selector: 'ngx-d3',
  styleUrls: ['./dashboard.component.scss'],
  templateUrl: './dashboard.component.html',
})
export class DashboardComponent implements OnInit, OnDestroy {

  public auditLog: AuditLog;
  public auditCardSet: CardSettings[] = [];


  constructor(private auditService: AuditLogsService, private servicesService: AvailableServicesService,
    private router: Router, private translateService: TranslateService,
    private loginService: LoginService, private errorDialogService: ErrorDialogService) {
  }


  async ngOnInit() {

    try {
      this.auditLog = await this.auditService.getAuditLog();

      if (this.auditLog) {

        this.auditCardSet = [
          {
            title: this.translateService.instant('general.dashboard.statusCards.availableServices'),
            iconClass: 'nb-grid-b-outline',
            type: 'primary',
            value: (await this.servicesService.getServicesCount()).toString()
          },
          {
            title: this.translateService.instant('general.dashboard.statusCards.linkedServices'),
            iconClass: 'nb-checkmark-circle',
            type: 'success',
            value: this.auditLog.linkedServicesCount.toString()
          },
          {
            title: this.translateService.instant('general.dashboard.statusCards.givenConsents'),
            iconClass: 'nb-compose',
            type: 'info',
            value: this.auditLog.givenConsentsCount.toString()
          },
          {
            title: this.translateService.instant('general.dashboard.statusCards.personalDataProcessed'),
            iconClass: 'nb-gear',
            type: 'warning',
            value: this.auditLog.totalProcessedPersonalDataCount.toString()
          }
        ];
      }

    } catch (error) {
      if (error.error.statusCode === '401') {
        this.loginService.logout();
        this.router.navigate(['/login']);
      } else
        this.errorDialogService.openErrorDialog(error);
    }

  }

  ngOnDestroy() {
    console.log('ngOnDestroy');
  }


}


