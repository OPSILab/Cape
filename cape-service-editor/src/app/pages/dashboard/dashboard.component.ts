import { Component, OnInit } from '@angular/core';
import { DashboardService, AuditLog } from './dashboard.service';
import { takeUntil } from 'rxjs/operators';
import { Subject } from 'rxjs';
import { LoginService } from '../../login/login.service';
import { Router } from '@angular/router';
import { CardSeriesComponent } from '@swimlane/ngx-charts';
import { ErrorDialogService } from '../error-dialog/error-dialog.service';

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
export class DashboardComponent implements OnInit {
  private unsubscribe: Subject<void> = new Subject();
  public auditLog: AuditLog;
  public auditCardSet: CardSettings[] = [];

  availableServicesCard: CardSettings = {
    title: 'Available Services',
    iconClass: 'nb-grid-b-outline',
    type: 'primary',
    value: '',
  };
  linkedServicesCard: CardSettings = {
    title: 'Service linked with you',
    iconClass: 'nb-checkmark-circle',
    type: 'success',
    value: '',
  };
  consentsCard: CardSettings = {
    title: 'Given Consents',
    iconClass: 'nb-compose',
    type: 'info',
    value: '',
  };
  personalDataCard: CardSettings = {
    title: 'Personal data processed',
    iconClass: 'nb-gear',
    type: 'warning',
    value: '',
  };

  constructor(
    private service: DashboardService,
    private router: Router,
    private loginService: LoginService,
    private errorDialogService: ErrorDialogService
  ) {}

  ngOnInit(): void {
    // TODO get Services;
    /*
  this.service.getAuditLog().pipe(takeUntil(this.unsubscribe)).subscribe(
      (audit: AuditLog) => {

        this.auditLog = audit;

        if (this.auditLog) {
          this.service.getServicesCount().pipe(takeUntil(this.unsubscribe))
            .subscribe(result => this.availableServicesCard.value = result);

          this.linkedServicesCard.value = this.auditLog.linkedServicesCount.toString();
          this.consentsCard.value = this.auditLog.givenConsentsCount.toString();
          this.personalDataCard.value = this.auditLog.totalProcessedPersonalDataCount.toString();

          this.auditCardSet = [
            this.availableServicesCard,
            this.linkedServicesCard,
            this.consentsCard,
            this.personalDataCard
          ];
        }
      },
      error => {
        if (error.error.statusCode == '401') {
          this.loginService.logout();
          this.router.navigate(['/login']);
        } else
          this.errorDialogService.openErrorDialog(error);
      });
      */
  }
}
