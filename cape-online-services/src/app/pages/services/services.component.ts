import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute, NavigationExtras } from '@angular/router';
import { NgxConfigureService } from 'ngx-configure';
import { NbDialogService } from '@nebular/theme';
import { TranslateService } from '@ngx-translate/core';

import { DialogPersonalAttributesComponent } from './personalattributes-dialogue/dialog-personalattributes.component';

import { DialogPrivacyNoticeComponent } from './privacynotice/dialog-privacynotice.component';
import { CapeSdkAngularService, RoleEnum, ConsentRecordSigned, SlStatusEnum, ConsentStatusEnum, CapeSdkDialogService, dialogType } from 'cape-sdk-angular';
import { NbAuthService } from '@nebular/auth';
import { AppConfig } from 'src/app/model/appConfig';
import { ErrorDialogService } from '../error-dialog/error-dialog.service';
@Component({
  selector: 'app-services',
  templateUrl: './services.component.html',
  styleUrls: ['./services.component.scss'],
})
export class ServicesComponent implements OnInit {
  private serviceAccountId: string;
  private serviceAccountEmail: string;

  config: AppConfig;
  private locale: string;

  selectedService: string;

  public sdkUrl: string;
  public operatorId: string;
  public dashboardUrl: string;
  public returnUrl: string;
  public capeLinkStatus: SlStatusEnum;
  public capeConsentStatus: ConsentStatusEnum;
  public checkConsentAtOperator: boolean;
  capeAccountId: string;
  consentRecord: ConsentRecordSigned;

  constructor(
    private configService: NgxConfigureService,
    private translateService: TranslateService,
    private capeService: CapeSdkAngularService,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private errorDialogService: ErrorDialogService,
    private capeDialogService: CapeSdkDialogService,
    private dialogService: NbDialogService,
    private authService: NbAuthService
  ) {
    this.config = this.configService.config;
    this.locale = this.config.i18n.locale;
    this.operatorId = this.config.system.operatorId;
    this.dashboardUrl = this.config.system.dashboardUrl;
    this.sdkUrl = this.config.services.sdkUrl;

    this.returnUrl = this.config.system.onlineServicesUrl;
    this.checkConsentAtOperator = this.config.services.checkConsentAtOperator;
  }

  ngOnInit() {
    this.serviceAccountId = localStorage.getItem('serviceAccountId');
    this.serviceAccountEmail = localStorage.getItem('serviceAccountEmail');
    this.capeAccountId = localStorage.getItem('capeAccountId');
    const queryParams = this.activatedRoute.snapshot.queryParams;
    this.locale = queryParams.locale || this.config.i18n.locale; // TODO default value taken from User language preferences;
    this.translateService.setDefaultLang('en');
    this.translateService.use(this.locale);
    sessionStorage.setItem('currentLocale', this.locale);
  }

  async onClickGo(serviceId: string, serviceName: string, serviceRole: string, purposeId: string) {
    if (!(await this.authService.isAuthenticatedOrRefresh().toPromise())) {
      this.router.navigate(['/login']);
    } else {
      this.checkAndGo(serviceId, serviceName, serviceRole == 'Sink' ? RoleEnum.Sink : RoleEnum.Source, purposeId);

      /* this.dialogService.open(DialogPersonalAttributesComponent)
         .onClose.subscribe(() => this.checkAndGo(service, purpose));*/
    }
  }

  checkAndGo = async (serviceId: string, serviceName: string, serviceRole: RoleEnum, purposeId: string) => {
    try {
      var checkLinking = await this.onCheckLinking(serviceId, serviceName);
      console.log(checkLinking);

      if (checkLinking) {
        var checkConsent = await this.onCheckConsent(serviceId, purposeId);
        console.log(checkConsent);

        if (!checkConsent) {
          this.dialogService.open(DialogPersonalAttributesComponent).onClose.subscribe(() => this.openInformativaForm(serviceId, serviceRole, purposeId));
        } else {
          console.log(checkConsent);
          // to enforce consentRules
          this.testPDProcessingByConsent(this.consentRecord);
          this.dialogService.open(DialogPersonalAttributesComponent).onClose.subscribe(() => {
            this.router.navigate([serviceId], { relativeTo: this.activatedRoute, queryParamsHandling: 'preserve' });
          });

          //this.router.navigate([service], { relativeTo: this.activatedRoute, queryParamsHandling: 'preserve' });
        }
      }
    } catch (error) {
      this.errorDialogService.openErrorDialog(error);
    }
  };

  onCheckLinking = async (serviceId: string, serviceName: string): Promise<boolean> => {
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
          this.returnUrl
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
            this.returnUrl
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

  async openInformativaForm(serviceId: string, serviceRole: RoleEnum, purposeId: string) {
    try {
      this.dialogService
        .open(DialogPrivacyNoticeComponent, {
          hasScroll: true,
          autoFocus: true,
          closeOnBackdropClick: true,
          context: {
            accountId: this.serviceAccountId,
            sdkUrl: this.sdkUrl,
            serviceId: serviceId,
            serviceRole: serviceRole,
            operatorId: this.operatorId,
            purposeId: purposeId,
          },
        })
        .onClose.subscribe((accepted) => {
          if (accepted) this.router.navigate([serviceId], { relativeTo: this.activatedRoute, queryParamsHandling: 'preserve' });
        });

      this.capeService.consentRecordStatus$.subscribe((event) => {
        console.log(event);
        if (event?.consentRecord) {
          // perform consent enforcement about personal data processing
          this.testPDProcessingByConsent(event.consentRecord);
        }
      });
    } catch (error) {
      this.errorDialogService.openErrorDialog(error);
    }
  }

  //fake data processing. It stores on localstorage the data according to consent
  testPDProcessingByConsent(consent: ConsentRecordSigned) {
    var dataset = consent.payload.common_part.rs_description.resource_set.datasets[0].dataMappings;
    console.log(dataset);

    var accountEmail = localStorage.getItem('accountEmail') === 'null' ? '' : localStorage.getItem('accountEmail');
    var accountAddress = localStorage.getItem('accountAddress') === 'null' ? '' : localStorage.getItem('accountAddress');
    var accountPhone = localStorage.getItem('accountPhone') === 'null' ? '' : localStorage.getItem('accountPhone');

    localStorage.removeItem('accountEmail');
    localStorage.removeItem('accountAddress');
    localStorage.removeItem('accountPhone');

    for (let pd of dataset) {
      {
        switch (pd.property) {
          case 'email': {
            localStorage.setItem('accountEmail', accountEmail);
            break;
          }
          case 'address': {
            localStorage.setItem('accountAddress', accountAddress);
            break;
          }
          case 'phone': {
            localStorage.setItem('accountPhone', accountPhone);
            break;
          }
        }
      }
    }
  }
}
