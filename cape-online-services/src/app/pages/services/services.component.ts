import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute, NavigationExtras } from '@angular/router';
import { NgxConfigureService } from 'ngx-configure';
import { NbDialogService } from '@nebular/theme';
import { CapeSdkAngularService, UserSurrogateIdLink } from '../../cape-sdk-angular/cape-sdk-angular.service';
import { ConsentFormComponent } from '../../cape-sdk-angular/consent-form/consent-form.component';
import { TranslateService } from '@ngx-translate/core';
import { SlStatusEnum } from '../../cape-sdk-angular/model/service-link/serviceLinkStatusRecordPayload';
import { ConsentStatusEnum } from '../../cape-sdk-angular/model/consent/consentStatusRecordPayload';
import { ErrorDialogService } from '../error-dialog/error-dialog.service';

import { DialogPersonalAttributesComponent } from './personalattributes-dialogue/dialog-personalattributes.component';

import { DialogPrivacyNoticeComponent } from './privacynotice/dialog-privacynotice.component';
import { ConsentRecordSigned } from 'src/app/cape-sdk-angular/model/consent/consentRecordSigned';
@Component({
  selector: 'app-services',
  templateUrl: './services.component.html',
  styleUrls: ['./services.component.scss']
})
export class ServicesComponent implements OnInit {

  accountId: string;
  config: any;
  locale: string;

  selectedService: string;

  public sdkUrl: string;
  public operatorId: string;
  public dashboardUrl: string;
  public serviceId: string;
  public serviceName: string
  public serviceUrl: string
  public returnUrl: string;
  public purposeId: string;
  public sourceDatasetId: string;
  public sourceServiceId: string;
  public capeLinkStatus: SlStatusEnum;
  public capeConsentStatus: ConsentStatusEnum;
  public checkConsentAtOperator: boolean;
  capeAccountId: string;
  consentRecord: ConsentRecordSigned;




  constructor(private configService: NgxConfigureService, private translateService: TranslateService,
    private capeService: CapeSdkAngularService, private router: Router, private activatedRoute: ActivatedRoute, private errorDialogService: ErrorDialogService,
    private dialogService: NbDialogService,) {

    this.config = configService.config;
    this.locale = this.config.i18n.locale;
    this.operatorId = this.config.system.operatorId;
    this.dashboardUrl = this.config.system.dashboardUrl;
    this.sdkUrl = this.config.services.sdkUrl;
    this.purposeId = this.config.services.mywellness.purposes.cholesterol.purposeId;
    
    this.sourceDatasetId = this.config.services.mywellness.purposes.cholesterol.sourceDatasetId;
    this.sourceServiceId = this.config.services.mywellness.purposes.cholesterol.sourceServiceId;
    this.returnUrl = "http://localhost:4400";
    this.checkConsentAtOperator = this.config.services.checkConsentAtOperator;

  }



  ngOnInit() {

    this.accountId = localStorage.getItem('accountId');
    this.capeAccountId = localStorage.getItem('accountId');
    const queryParams = this.activatedRoute.snapshot.queryParams;
    this.locale = queryParams.locale || this.config.i18n.locale; // TODO default value taken from User language preferences;
    this.translateService.setDefaultLang('en');
    this.translateService.use(this.locale);
  }


  onClickGo(service: string, purpose: string) {

    if (!this.checkAuth()) {
      this.router.navigate(['/login']);
    } else {
      this.checkAndGo(service, purpose);


     /* this.dialogService.open(DialogPersonalAttributesComponent)
        .onClose.subscribe(() => this.checkAndGo(service, purpose));*/
      
    }
  }

  // to update with actual auth session if it is valid yet
  checkAuth() {
    this.accountId = localStorage.getItem('accountId');
    if (this.accountId) {
      return true;
    } else {
      return false;
    }
  }

  checkAndGo = async (service: string, purpose: string) => {

    var checkLinking = await this.onCheckLinking(service);
    console.log(checkLinking);

    if (checkLinking) {

      var check = await this.onCheckConsent(service, purpose);
      console.log(check);
      if (!check){        
        this.dialogService.open(DialogPersonalAttributesComponent)
        .onClose.subscribe(() => this.openInformativaForm(service, purpose));        
        
      }
      
      else { 
        console.log(check); 
        // to enforce consentRules 
        this.testPDProcessingByConsent(this.consentRecord);
        this.dialogService.open(DialogPersonalAttributesComponent)
        .onClose.subscribe(() => {this.router.navigate([service], { relativeTo: this.activatedRoute, queryParamsHandling: 'preserve' });} );
        
        //this.router.navigate([service], { relativeTo: this.activatedRoute, queryParamsHandling: 'preserve' });
      }
      
    }
  };


  

  onCheckLinking = async (serviceId: string) => {
    try {
      console.log("checking service linking for service: " + serviceId + " and user: " + this.accountId);
      var linkSurrogateId = await this.capeService.getLinkSurrogateIdByUserIdAndServiceIdAndOperatorId(this.sdkUrl, this.accountId, serviceId, this.operatorId);

      console.log("checking surrogateId ...");
      console.log(linkSurrogateId);
      if (linkSurrogateId) {
        console.log("checking SLR status");

        var serviceLinkStatus = await this.capeService.getServiceLinkStatus(this.sdkUrl, this.accountId, serviceId, this.operatorId);
        console.log("checking SLR status...OK" + serviceLinkStatus);

        if (serviceLinkStatus === "Active")
          return true
        else
          return false

      } else {

        linkSurrogateId = await this.capeService.automaticLinkFromService(this.sdkUrl, this.operatorId, serviceId, this.serviceName, this.accountId, this.capeAccountId, this.returnUrl)
        if (linkSurrogateId)
          return true
      }


    } catch (error) {
      console.log(error)
      if (error.error?.statusCode === '401') {
        this.router.navigate(['/login']);
      }
      if (error.status === 404) {
        console.log("no service link for service " + serviceId);
        linkSurrogateId = await this.capeService.automaticLinkFromService(this.sdkUrl, this.operatorId, serviceId, this.serviceName, this.accountId, this.capeAccountId, this.returnUrl)
        if (linkSurrogateId)
          return true
      }

    }

  }

  onCheckConsent = async (serviceId: string, purpose: string) => {
    try {
      console.log("checking consent for service: " + serviceId);

     // var consentStatus = await this.capeService.getConsentStatus(this.sdkUrl, this.accountId, serviceId, purpose, this.operatorId, this.checkConsentAtOperator);
      const consentRecords = await this.capeService.getConsentsByUserIdAndServiceIdAndPurposeId(this.sdkUrl, this.accountId, serviceId, purpose, this.operatorId, this.checkConsentAtOperator);
      var consentStatus= consentRecords[0].consentStatusList.pop().payload.consent_status;
      
      console.log("checking Consent" + consentStatus);
      if (consentStatus === "Active") {
        this.consentRecord= consentRecords[0];
        return true
      }
        
      else
        return false

    } catch (error) {
      if (error.error?.statusCode === '401') {
        this.router.navigate(['/login']);
      } else
        console.log("ERROR:" + error)
    }

  }

  async openInformativaForm(serviceId: string, purposeId: string) {

    try {
      this.dialogService.open(DialogPrivacyNoticeComponent,
        {
          hasScroll: true,
          autoFocus: true,
          closeOnBackdropClick: true,
          context: {
            accountId: this.accountId,
            sdkUrl: this.sdkUrl,
            serviceId: serviceId,
            operatorId: this.operatorId,
            purposeId: purposeId,
            sourceServiceId: serviceId,
            sourceDatasetId: "General_01"

          }
        }).onClose.subscribe(accepted => { if (accepted) this.router.navigate([serviceId], { relativeTo: this.activatedRoute, queryParamsHandling: 'preserve' }); });

        

      this.capeService.consentRecordStatus$.subscribe(event => {
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

    var dataset = consent.payload.common_part.rs_description.resource_set.datasets[0].dataMappings
    console.log(dataset);

    var accountEmail = localStorage.getItem('accountEmail') === "null" ? "" : localStorage.getItem('accountEmail');
    var accountAddress = localStorage.getItem('accountAddress') === "null" ? "" : localStorage.getItem('accountAddress');
    var accountPhone = localStorage.getItem('accountPhone') === "null" ? "" : localStorage.getItem('accountPhone');

    localStorage.removeItem('accountEmail')
    localStorage.removeItem('accountAddress')
    localStorage.removeItem('accountPhone')

    for (let pd of dataset) {
      {
        switch (pd.property) {
          case 'email': {
            localStorage.setItem('accountEmail', accountEmail)
            break;
          }
          case 'address': {
            localStorage.setItem('accountAddress', accountAddress)
            break;
          }
          case 'phone': {
            localStorage.setItem('accountPhone', accountPhone)
            break;
          }

        }
      }
    }
  }


}