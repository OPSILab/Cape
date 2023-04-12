import { AfterViewInit, Component, OnDestroy, OnInit } from '@angular/core';
import { Router, ActivatedRoute, NavigationExtras, Params } from '@angular/router';
import { NgxConfigureService } from 'ngx-configure';
import { NbDialogService } from '@nebular/theme';
import { TranslateService } from '@ngx-translate/core';
import { ConsentFormComponent2 } from '../consent-form/consent-form.component';
import {
  CapeSdkAngularService,
  RoleEnum,
  ConsentRecordSigned,
  SlStatusEnum,
  ConsentStatusEnum,
  CapeSdkDialogService,
  dialogType,
  ConsentRecordEvent,
} from 'cape-sdk-angular';
// import { NbAuthService } from '@nebular/auth';
import { AppConfig } from 'src/app/model/appConfig';
import { ErrorDialogService } from '../error-dialog/error-dialog.service';
import { takeUntil } from 'rxjs/operators';
import { Subject } from 'rxjs';
@Component({
  selector: 'app-services',
  templateUrl: './services.component.html',
  styleUrls: ['./services.component.scss'],
})
export class ServicesComponent implements AfterViewInit, OnDestroy {
  config: AppConfig;
  private locale: string;

  selectedService: string;

  private serviceId: string;
  private serviceName: string;
  private serviceAccountId: string;
  private serviceAccountEmail: string;
  private serviceRole: RoleEnum = RoleEnum.Sink;

  public sdkUrl: string;
  public operatorId: string;
  public dashboardUrl: string;
  public cancelReturnUrl: string;
  public capeLinkStatus: SlStatusEnum;
  public capeConsentStatus: ConsentStatusEnum;
  public checkConsentAtOperator: boolean;
  public showConsentAdditionalOptions = false;
  consentRecord: ConsentRecordSigned;
  private unsubscribe: Subject<void> = new Subject();

  constructor(
    private configService: NgxConfigureService,
    private translateService: TranslateService,
    private capeService: CapeSdkAngularService,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private errorDialogService: ErrorDialogService,
    private capeDialogService: CapeSdkDialogService,
    private dialogService: NbDialogService
  ) {
    this.config = this.configService.config;
    this.locale = this.config.i18n.locale;
    this.operatorId = this.config.system.operatorId;
    this.dashboardUrl = this.config.system.dashboardUrl;
    this.sdkUrl = this.config.services.sdkUrl;
    this.cancelReturnUrl = this.config.system.cancelReturnUrl;
    this.checkConsentAtOperator = this.config.services.checkConsentAtOperator;
  }

  ngAfterViewInit(): void {
    // Get query params from which to extract serviceAccountId,
    const queryParams = this.activatedRoute.snapshot.queryParams;

    this.serviceId = queryParams.serviceId;
    this.serviceAccountId = localStorage.getItem('serviceAccountId');

    this.cancelReturnUrl = queryParams.cancelReturnUrl || this.cancelReturnUrl;

    if (this.serviceId == undefined) this.errorDialogService.openErrorDialog(new Error(this.translateService.instant('general.missingRequiredInputParam')));
    else {
      this.locale = queryParams.locale || this.config.i18n.locale; // TODO default value taken from User language preferences;
      this.showConsentAdditionalOptions = queryParams.showConsentAdditionalOptions || this.showConsentAdditionalOptions;
      this.translateService.setDefaultLang('en');
      this.translateService.use(this.locale);
      sessionStorage.setItem('currentLocale', this.locale);

      this.checkAndGo(this.serviceId, this.serviceName, this.serviceRole, queryParams.purposeId);
    }

    // this.capeService.consentRecordStatus$.pipe(takeUntil(this.unsubscribe)).subscribe(async (event) => {
    //   event = event as ConsentRecordEvent;
    //   if (event?.serviceId === this.serviceId) this.capeConsentStatus = event.status.consent_status;
    // });
  }

  checkAndGo = async (serviceId: string, serviceName: string, serviceRole: RoleEnum, purposeId: string) => {
    try {
      const serviceDescription = await this.capeService.getServiceDescription(this.sdkUrl, serviceId);
      const returnUrl = serviceDescription.serviceInstance.serviceUrls.linkingRedirectUri;
      var checkLinking = await this.onCheckLinking(serviceId, serviceName || serviceDescription.name, returnUrl);
      console.log(checkLinking);

      if (checkLinking) {
        var checkConsent = await this.onCheckConsent(serviceId, purposeId);
        console.log(checkConsent);

        if (!checkConsent) {
          this.openConsentForm(serviceId, serviceRole, purposeId || serviceDescription.processingBases[0].purposeId, returnUrl);
        } else {
          // Consent is ok, redirect to the description Return Url
          window.location.replace(returnUrl);
        }
      }
    } catch (error) {
      this.errorDialogService.openErrorDialog(error);
    }
  };

  onCheckLinking = async (serviceId: string, serviceName: string, returnUrl: string): Promise<boolean> => {
    let linkSurrogateId;

    try {
      console.log('checking service linking for service: ' + serviceId + ' and user: ' + this.serviceAccountId);
      linkSurrogateId = await this.capeService.getLinkSurrogateIdByUserIdAndServiceIdAndOperatorId(
        this.sdkUrl,
        this.serviceAccountId,
        serviceId,
        this.operatorId
      );

      console.log('checking surrogateId ...');

      if (linkSurrogateId) {
        console.log('checking SLR status');

        const serviceLinkRecord = await this.capeService.getServiceLinkRecordByUserIdAndServiceId(
          this.sdkUrl,
          this.serviceAccountId,
          serviceId,
          this.operatorId
        );

        // Auto activate existing SLR but in Removed status
        if (serviceLinkRecord.serviceLinkStatuses.pop().payload.sl_status == SlStatusEnum.Removed) {
          // await this.capeService.enableServiceLink(
          //   this.sdkUrl,
          //   serviceLinkRecord.payload.link_id,
          //   serviceLinkRecord.payload.surrogate_id,
          //   serviceId,
          //   serviceName
          // );
          await this.capeDialogService.openCapeDialog(
            dialogType.enableServiceLink,
            this.capeService.enableServiceLink,
            [this.sdkUrl, serviceLinkRecord.payload.link_id, serviceLinkRecord.payload.surrogate_id, serviceId, serviceName],
            '',
            ''
          );
          return false;
        }
        return true;
      } else {
        linkSurrogateId = await this.capeService.automaticLinkFromService(
          this.sdkUrl,
          this.operatorId,
          serviceId,
          this.serviceAccountId,
          this.serviceAccountEmail,
          this.locale,
          returnUrl
        );
        if (linkSurrogateId) return true;
      }
    } catch (error) {
      if (error.error?.statusCode === '401' || error.status === 401) {
        this.errorDialogService.openErrorDialog(error);
      } else if (error.status === 404) {
        console.log('No ServiceLink for service: ' + serviceId + '...starting service linking');
        try {
          linkSurrogateId = await this.capeService.automaticLinkFromService(
            this.sdkUrl,
            this.operatorId,
            serviceId,
            this.serviceAccountId,
            this.serviceAccountEmail,
            this.locale,
            returnUrl
          );
          return linkSurrogateId ? true : false;
        } catch (error) {
          // if (error?.error.cause == 'it.eng.opsi.cape.exception.AccountNotFoundException') {
          //   // Call SDK API to Create Account using as accountId the accountId of the Service
          //   await this.capeService.createCapeAccount(this.sdkUrl, this.serviceAccountId, this.serviceAccountEmail, this.locale);
          //   this.capeAccountId = this.serviceAccountId;
          //   // Once the Cape Account has been created, retry automaticLinking
          //   linkSurrogateId = await this.capeService.automaticLinkFromService(this.sdkUrl, this.operatorId, serviceId, this.serviceAccountId, this.returnUrl);
          // } else
          this.errorDialogService.openErrorDialog(error);
        }
      } else throw error;
    }
  };

  onCheckConsent = async (serviceId: string, purposeId: string): Promise<boolean> => {
    try {
      console.log('checking consent for service: ' + serviceId);

      const consentRecords = await this.capeService.getConsentsByUserIdAndQuery(
        this.sdkUrl,
        this.checkConsentAtOperator,
        this.serviceAccountId,
        serviceId,
        undefined,
        undefined,
        undefined,
        purposeId
      );
      const consentStatus = consentRecords[0]?.consentStatusList.pop().payload.consent_status;

      console.log('checking Consent' + consentStatus);
      if (consentStatus === ConsentStatusEnum.Active) {
        this.consentRecord = consentRecords[0];
        return true;
      } else return false;
    } catch (error) {
      if (error.error?.statusCode === '401' || error.status === 401) {
        this.errorDialogService.openErrorDialog(error);
      } else if (error.status === 404) {
        console.log('No Consent Record for service: ' + serviceId + 'and User: ' + this.serviceAccountId);
        return false;
      } else throw error;
    }
  };

  async openConsentForm(serviceId: string, serviceRole: RoleEnum, purposeId: string, returnUrl: string) {
    this.translateService.use(sessionStorage.getItem('currentLocale'));
    try {
      this.dialogService
        .open(ConsentFormComponent2, {
          hasScroll: true,
          autoFocus: true,
          closeOnBackdropClick: true,
          context: {
            sdkUrl: this.sdkUrl,
            consentForm: await this.capeService.fetchConsentForm(this.sdkUrl, this.serviceAccountId, serviceId, this.operatorId, purposeId, serviceRole),
            locale: sessionStorage.getItem('currentLocale') as string,
            showAdditionalOptions: this.config.services.showAdditionalConsentFormOptions,
          },
        })
        .onClose.subscribe((accepted) => {
          window.location.href = accepted ? returnUrl : this.cancelReturnUrl;
        });
    } catch (error) {
      this.errorDialogService.openErrorDialog(error);
    }
  }

  ngOnDestroy() {
    this.unsubscribe.next();
    this.unsubscribe.complete();
  }
}
