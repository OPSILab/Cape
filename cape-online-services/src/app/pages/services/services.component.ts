import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute, NavigationExtras } from '@angular/router';
import { NgxConfigureService } from 'ngx-configure';
import { NbDialogService } from '@nebular/theme';
import { CapeSdkAngularService, UserSurrogateIdLink } from '../../cape-sdk-angular/cape-sdk-angular.service';
import { TranslateService } from '@ngx-translate/core';
import { SlStatusEnum } from '../../cape-sdk-angular/model/service-link/serviceLinkStatusRecordPayload';
import { ConsentStatusEnum } from '../../cape-sdk-angular/model/consent/consentStatusRecordPayload';

import { DialogPersonalAttributesComponent } from './personalattributes-dialogue/dialog-personalattributes.component';

import { DialogPrivacyNoticeComponent } from './privacynotice/dialog-privacynotice.component';
import { ConsentRecordSigned } from 'src/app/cape-sdk-angular/model/consent/consentRecordSigned';
import { LoginService } from 'src/app/login/login.service';
import { ErrorDialogService } from '../error-dialog/error-dialog.service';
@Component({
  selector: 'app-services',
  templateUrl: './services.component.html',
  styleUrls: ['./services.component.scss'],
})
export class ServicesComponent implements OnInit {
  private serviceAccountId: string;
  private serviceAccountEmail: string;

  config: any;
  private locale: string;

  selectedService: string;

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
    private dialogService: NbDialogService,
    private loginService: LoginService
  ) {
    this.config = configService.config;
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
  }

  onClickGo(serviceId: string, purposeId: string) {
    if (!this.checkAuth()) {
      this.router.navigate(['/login']);
    } else {
      this.checkAndGo(serviceId, purposeId);

      /* this.dialogService.open(DialogPersonalAttributesComponent)
         .onClose.subscribe(() => this.checkAndGo(service, purpose));*/
    }
  }

  // to update with actual auth session if it is valid yet
  checkAuth() {
    this.serviceAccountId = localStorage.getItem('serviceAccountId');
    if (this.serviceAccountId) {
      return true;
    } else {
      return false;
    }
  }

  checkAndGo = async (serviceId: string, purposeId: string) => {
    try {
      var checkLinking = await this.onCheckLinking(serviceId);
      console.log(checkLinking);

      if (checkLinking) {
        var checkConsent = await this.onCheckConsent(serviceId, purposeId);
        console.log(checkConsent);

        if (!checkConsent) {
          this.dialogService.open(DialogPersonalAttributesComponent).onClose.subscribe(() => this.openInformativaForm(serviceId, purposeId));
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

  onCheckLinking = async (serviceId: string): Promise<boolean> => {
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

        var serviceLinkStatus = await this.capeService.getServiceLinkStatus(this.sdkUrl, this.serviceAccountId, serviceId, this.operatorId);
        console.log('checking SLR status...OK' + serviceLinkStatus);

        return serviceLinkStatus === SlStatusEnum.Active;
      } else {
        linkSurrogateId = await this.capeService.automaticLinkFromService(this.sdkUrl, this.operatorId, serviceId, this.serviceAccountId, this.returnUrl);
        if (linkSurrogateId) return true;
      }
    } catch (error) {
      if (error.error?.statusCode === '401' || error.status === 401) {
        this.errorDialogService.openErrorDialog(error);
      } else if (error.status === 404) {
        console.log('No ServiceLink for service: ' + serviceId + '...starting service linking');
        try {
          linkSurrogateId = await this.capeService.automaticLinkFromService(this.sdkUrl, this.operatorId, serviceId, this.serviceAccountId, this.returnUrl);
          return linkSurrogateId ? true : false;
        } catch (error) {
          if (error?.error.cause == 'it.eng.opsi.cape.exception.AccountNotFoundException') {
            // Call SDK API to Create Account using as accountId the accountId of the Service
            await this.capeService.createCapeAccount(this.sdkUrl, this.serviceAccountId, this.serviceAccountEmail, this.locale);
            this.capeAccountId = this.serviceAccountId;
            // Once the Cape Account has been created, retry automaticLinking
            linkSurrogateId = await this.capeService.automaticLinkFromService(this.sdkUrl, this.operatorId, serviceId, this.serviceAccountId, this.returnUrl);
          } else this.errorDialogService.openErrorDialog(error);
        }
      } else this.errorDialogService.openErrorDialog(error);
    }
  };

  onCheckConsent = async (serviceId: string, purpose: string): Promise<boolean> => {
    try {
      console.log('checking consent for service: ' + serviceId);

      // var consentStatus = await this.capeService.getConsentStatus(this.sdkUrl, this.accountId, serviceId, purpose, this.operatorId, this.checkConsentAtOperator);
      const consentRecords = await this.capeService.getConsentsByUserIdAndServiceIdAndPurposeId(
        this.sdkUrl,
        this.serviceAccountId,
        serviceId,
        purpose,
        this.operatorId,
        this.checkConsentAtOperator
      );
      const consentStatus = consentRecords[0]?.consentStatusList.pop().payload.consent_status;

      console.log('checking Consent' + consentStatus);
      if (consentStatus === ConsentStatusEnum.Active) {
        this.consentRecord = consentRecords[0];
        return true;
      } else return false;
    } catch (error) {
      this.errorDialogService.openErrorDialog(error);
    }
  };

  async openInformativaForm(serviceId: string, purposeId: string) {
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

  //fake data processing. It store on localstorage the data according to consent
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
