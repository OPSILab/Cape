
import { Component, OnInit, OnDestroy, ViewChild, ElementRef } from '@angular/core';
import { AuditLogsService } from './auditlogs.service';
import { NbRangepickerComponent, NbSelectComponent } from '@nebular/theme';
import { NgxConfigureService } from 'ngx-configure';
import { LoginService } from '../../login/login.service';
import { TranslateService } from '@ngx-translate/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ErrorDialogService } from '../error-dialog/error-dialog.service';
import { EventLog, EventType, DateRange, ConsentEventLog, ServiceLinkEventLog, ConsentActionType, ServiceLinkActionType } from '../../model/auditlogs/auditlogs.model';
import { ProcessingBasisLegalBasis, ProcessingBasisProcessingCategories } from '../../model/processingBasis';
import { FormGroup, FormControl } from '@angular/forms';



@Component({
  selector: 'auditlogs-component',
  styleUrls: ['./auditlogs.component.scss'],
  templateUrl: './auditlogs.component.html',
})
export class AuditLogsComponent implements OnInit, OnDestroy {

  loading: boolean = true;
  @ViewChild('rangepicker', { static: true }) rangepicker: NbRangepickerComponent<any>;
  @ViewChild('inputrange', { static: true }) inputRange: ElementRef;

  dateRangeFilter: DateRange;
  eventLogs: unknown[];

  private processingCategoryEnum = ProcessingBasisProcessingCategories;
  public processingCategoryOptions;
  private legalBasisEnum = ProcessingBasisLegalBasis;
  public legalBasisOptions;

  public filtersForm = new FormGroup({
    legalBasis: new FormControl(),
    processingCategory: new FormControl(),
    dateRange: new FormControl()
  });

  constructor(private service: AuditLogsService, private translate: TranslateService, private configService: NgxConfigureService,
    private loginService: LoginService, private router: Router, private route: ActivatedRoute, private errorDialogService: ErrorDialogService) {

    this.processingCategoryOptions = Object.keys(this.processingCategoryEnum);
    this.legalBasisOptions = Object.keys(this.legalBasisEnum);
  }

  async ngOnInit() {

    this.service.getEventLogs().then(result => {
      this.eventLogs = this.groupEventLogsByMonthDays(result);
      this.loading = false;
    }).catch(error => {
      if (error.status === 401) {
        this.loginService.logout();
        this.router.navigate(['/login']);
      } else
        this.errorDialogService.openErrorDialog(error);
    });
  }

  ngOnDestroy() {

    console.log('ngOnDestroy');
  }

  openConsent(consentId, message) {

    this.router.navigate(['/pages/consents'], { queryParams: { consentId: consentId, eventName: message } });
  }

  openServiceLink(serviceId, serviceName) {

    this.router.navigate(['/pages/services/linkedServices'], { queryParams: { serviceId: serviceId, serviceName: serviceName } });
  }


  async onFilterSubmit() {

    try {
      this.eventLogs = this.groupEventLogsByMonthDays(await this.service.getFilteredEventLogs(
        this.filtersForm.get('legalBasis').value,
        this.filtersForm.get('dateRange').value,
        this.filtersForm.get('processingCategory').value));

    } catch (error) {
      if (error.status === 401) {
        this.loginService.logout();
        this.router.navigate(['/login']);
      } else
        this.errorDialogService.openErrorDialog(error);
    }

  }


  resetFilters() {

    this.filtersForm.reset({
      legalBasis: [],
      processingCategory: [],
      dateRange: ''
    });

    this.onFilterSubmit();
  }


  groupEventLogsByMonthDays(eventLogs: EventLog[]): unknown[] {

    const groupedEvents: unknown[] = [];
    const map: Map<string, Map<string, EventLog[]>> = new Map<string, Map<string, EventLog[]>>();

    for (const event of eventLogs) {

      const date = new Date(event.created);
      const month = `${(date.getUTCMonth() + 1).toString()}/${date.getUTCFullYear().toString()}`;
      const day = `${date.getUTCDate().toString()}/${month}`;

      const mapMonthEntry: Map<string, EventLog[]> = map.get(month);

      if (mapMonthEntry) {
        const dayEvents: EventLog[] = mapMonthEntry.get(day);
        if (dayEvents) {
          dayEvents.push(event);
        } else {
          mapMonthEntry.set(day, [event]);
        }

      } else {
        // Add the event in a new Map for that day and insert it in the new month entry
        const dayMap = new Map<string, EventLog[]>([[day, [event]]]);
        map.set(month, dayMap);
      }
    }

    for (const [monthKey, monthValue] of map.entries()) {

      const monthDay = [];
      for (const [dayKey, dayEvents] of monthValue.entries()) {

        const typedEvents = dayEvents.map((event) => {

          if (event.type === EventType.Consent) {

            return {
              context: event.type && event.type[0].toUpperCase() + event.type.slice(1),
              legalBasis: event.legalBasis,
              message: event.message,
              created: new Date(event.created),
              type: this.translate.instant('general.auditlogs.'+event.type),
              action:this.translate.instant('general.auditlogs.'+(event as ConsentEventLog).action),
              purpose: {
                purposeId: (event as ConsentEventLog).usageRules.purposeId,
                purposeName: (event as ConsentEventLog).usageRules.purposeName,
                purposeCategory: (event as ConsentEventLog).usageRules.purposeCategory
              },
              process: (event as ConsentEventLog).usageRules.processingCategories.join(', '),
              source: (event as ConsentEventLog).sourceId,
              target: (event as ConsentEventLog).sinkId,
              dataConcepts: (event as ConsentEventLog).dataConcepts,
              consent_id: (event as ConsentEventLog).consentRecordId
            };
          }

          if (event.type === EventType.ServiceLink) {
            return {
              context: event.type && event.type[0].toUpperCase() + event.type.slice(1),
              legalBasis: event.legalBasis,
              message: event.message,
              type: this.translate.instant("general.auditlogs."+event.type),
              action:this.translate.instant("general.auditlogs."+(event as ServiceLinkEventLog).action),
              created: new Date(event.created),
              serviceId: (event as ServiceLinkEventLog).serviceId,
              serviceName: (event as ServiceLinkEventLog).serviceName,
              serviceUri: (event as ServiceLinkEventLog).serviceUri
            };
          }

          if (event.type === EventType.DataProcessing) {
            return {};
          }

        });

        monthDay.push({
          day: dayKey,
          events: typedEvents
        });
      }

      groupedEvents.push({
        month: monthKey,
        days: monthDay
      });
    }

    return groupedEvents;
  }


}
