import { Component, OnInit, ChangeDetectorRef, OnDestroy } from '@angular/core';
import { NgxConfigureService } from 'ngx-configure';
import { CapeSdkAngularService, ServiceLinkEvent, ConsentRecordEvent, SlStatusEnum, ConsentStatusEnum } from 'cape-sdk-angular';
import { TranslateService } from '@ngx-translate/core';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

const sampleChartData: any[] = [
  {
    name: 'LDL + HDL',
    series: [
      {
        name: new Date(2020, 0, 1, 2, 34, 17),
        value: 224,
      },
      {
        name: new Date(2020, 1, 1, 2, 34, 17),
        value: 220,
      },
      {
        name: new Date(2020, 2, 1, 2, 34, 17),
        value: 215,
      },
      {
        name: new Date('2020-09-05T12:00:00Z'),
        value: 195,
      },
      {
        name: new Date('2020-09-10T12:00:00Z'),
        value: 194,
      },
      {
        name: new Date('2020-09-15T12:00:00Z'),
        value: 193,
      },
      {
        name: new Date('2020-09-18T12:00:00Z'),
        value: 190,
      },
      {
        name: new Date('2020-09-20T12:00:00Z'),
        value: 189,
      },
      {
        name: new Date('2020-09-23T12:00:00Z'),
        value: 185,
      },
      {
        name: new Date('2020-09-26T12:00:00Z'),
        value: 182,
      },
      {
        name: new Date('2020-09-30T12:00:00Z'),
        value: 179,
      },
    ],
  },
];

@Component({
  selector: 'app-cholesterol',
  templateUrl: './cholesterol.component.html',
  styleUrls: ['./cholesterol.component.scss'],
})
export class CholesterolComponent implements OnInit, OnDestroy {
  private config: any;
  public locale: string;
  public sdkUrl: string;
  public operatorId: string;
  public dashboardUrl: string;
  public serviceId: string;
  public serviceName: string;
  public serviceUrl: string;
  public returnUrl: string;
  public purposeId: string;
  public sourceDatasetId: string;
  public sourceServiceId: string;
  public accountId: string;
  public isRegisteredService: boolean;
  public capeLinkStatus: SlStatusEnum;
  public capeConsentStatus: ConsentStatusEnum;
  public ageConsentActive: boolean = false;
  public sexConsentActive: boolean = false;
  public checkConsentAtOperator: boolean;
  private unsubscribe: Subject<void> = new Subject();

  /*
   * Sample data for demo purpose -> Data will be retrieved by using the Data request flow provided by CaPe
   */
  public sampleAge: string = '32';
  public sampleSex: string = 'Male';

  /*
   *  Chart properties
   * */
  multi: any[];
  view: number[] = [900, 400];
  showXAxis = true;
  showYAxis = true;
  gradient = true;
  showLegend = true;
  showXAxisLabel = true;
  xAxisLabel = 'Date';
  showYAxisLabel = true;
  yAxisLabel = 'Cholesterol Value (mg/dl)';
  timeline = true;
  animations = true;

  colorScheme = {
    domain: ['#5AA454', '#A10A28', '#C7B42C', '#AAAAAA'],
  };

  // line, area
  autoScale = true;

  constructor(
    private configService: NgxConfigureService,
    private translateService: TranslateService,
    private capeService: CapeSdkAngularService,
    private cdr: ChangeDetectorRef
  ) {
    this.config = configService.config;
    this.locale = this.config.i18n.locale;
    this.operatorId = this.config.system.operatorId;
    this.dashboardUrl = this.config.system.dashboardUrl;
    this.sdkUrl = this.config.services.sdkUrl;
    this.purposeId = this.config.services.mywellness.purposes.cholesterol.purposeId;
    this.serviceId = this.config.services.mywellness.serviceId;
    this.serviceName = this.config.services.mywellness.serviceName;
    this.serviceUrl = this.config.services.mywellness.serviceUrl;
    this.sourceDatasetId = this.config.services.mywellness.purposes.cholesterol.sourceDatasetId;
    this.sourceServiceId = this.config.services.mywellness.purposes.cholesterol.sourceServiceId;
    this.returnUrl = this.serviceUrl; // + this.config.services.mywellness.purposes.diet.returnPath;
    this.checkConsentAtOperator = this.config.services.checkConsentAtOperator;

    if (this.locale === 'it') this.sampleSex = this.sampleSex === 'Male' ? 'Maschio' : 'Femmina';
  }

  async ngOnInit() {
    this.accountId = localStorage.getItem('accountId');

    this.multi = sampleChartData;

    this.translateService.use(this.locale);

    this.capeService.isRegisteredService$.pipe(takeUntil(this.unsubscribe)).subscribe(async (isRegistered) => {
      this.isRegisteredService = isRegistered;
    });

    this.capeService.serviceLinkStatus$.pipe(takeUntil(this.unsubscribe)).subscribe(async (event) => {
      event = event as ServiceLinkEvent;

      if (event?.serviceId === this.serviceId) this.capeLinkStatus = event.status;

      /*
       *
       * TODO Depending on config, the successful linking triggers also giving consent
       */

      this.cdr.detectChanges();
    });

    this.capeService.consentRecordStatus$.pipe(takeUntil(this.unsubscribe)).subscribe(async (event) => {
      event = event as ConsentRecordEvent;
      if (event?.serviceId === this.serviceId) {
        this.capeConsentStatus = event.status.consent_status;

        this.ageConsentActive =
          event.status.consent_resource_set.datasets.filter((dataset) => dataset.dataMappings.filter((dm) => dm.property === 'age').length > 0).length > 0 &&
          this.capeConsentStatus === ConsentStatusEnum.Active;

        this.sexConsentActive =
          event.status.consent_resource_set.datasets.filter((dataset) => dataset.dataMappings.filter((dm) => dm.property === 'sex').length > 0).length > 0 &&
          this.capeConsentStatus === ConsentStatusEnum.Active;
      }
    });

    this.cdr.detectChanges();
  }

  ngOnDestroy() {
    this.unsubscribe.next();
    this.unsubscribe.complete();
  }
}
