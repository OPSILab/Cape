/* eslint-disable @typescript-eslint/no-misused-promises */
import { Component, OnInit, Input, ChangeDetectorRef, ViewChild, AfterViewInit, TemplateRef, OnDestroy } from '@angular/core';
import { NbMenuService, NbMenuItem, NbToastrService, NbDialogService, NbDialogRef } from '@nebular/theme';
import { filter, map, takeUntil } from 'rxjs/operators';
import { ActivatedRoute, Router } from '@angular/router';
import { CapeSdkDialogService } from './cape-sdk-dialog/cape-sdk-dialog.service';
import { LinkingFromEnum, ConsentRecordEvent, ServiceLinkEvent } from './cape-sdk-angular.service';
import { CapeSdkAngularService } from './cape-sdk-angular.service';
import { TranslateService } from '@ngx-translate/core';
import { SlStatusEnum } from './model/service-link/serviceLinkStatusRecordPayload';
import { ServiceLinkRecordDoubleSigned } from './model/service-link/serviceLinkRecordDoubleSigned';
import { Subject } from 'rxjs';
import { ConsentFormComponent } from './consent-form/consent-form.component';
import { ConsentRecordSigned } from './model/consent/consentRecordSigned';
import { ConsentStatusEnum } from './model/consent/consentStatusRecordPayload';
import { RoleEnum } from './model/service-link/serviceEntry';

@Component({
  selector: 'lib-cape-sdk-angular',
  templateUrl: './cape-sdk-angular.component.html',
  styleUrls: ['./cape-sdk-angular.component.scss'],
})
export class CapeSdkAngularComponent implements OnInit, AfterViewInit, OnDestroy {
  @Input()
  private locale: string;

  @Input()
  private operatorId: string;

  @Input()
  private dashboardUrl: string;

  @Input()
  private sdkUrl: string;

  @Input()
  private serviceRole: RoleEnum;

  @Input()
  private sinkServiceId: string;

  @Input()
  public serviceName: string;

  @Input()
  private returnUrl: string;

  @Input()
  private purposeId: string;

  @Input()
  private sourceDatasetId: string;

  @Input()
  private sourceServiceId: string;

  @Input()
  private checkConsentAtOperator: boolean;

  @Input()
  private userId: string;

  private serviceId: string;
  public isServiceRegistered = false;
  private serviceLinkRecord: ServiceLinkRecordDoubleSigned;
  private consentRecord: ConsentRecordSigned;
  private linkingFrom: LinkingFromEnum;

  private initialLocale: string;
  private sessionCode: string;
  public menuItems: NbMenuItem[];

  @ViewChild('linkingDialog', { static: false })
  linkingDialog: TemplateRef<unknown>;
  @ViewChild('linkedDialog', { static: true })
  linkedDialog: TemplateRef<unknown>;
  @ViewChild('consentUpdateConflict')
  private consentUpdateConflict: TemplateRef<unknown>;

  public openedDialog: NbDialogRef<unknown> = undefined;
  private unsubscribe: Subject<void> = new Subject();

  get consentStatus(): ConsentStatusEnum {
    return this.consentRecord?.consentStatusList[this.consentRecord?.consentStatusList.length - 1]?.payload.consent_status;
  }

  get serviceLinkStatus(): SlStatusEnum {
    return this.serviceLinkRecord?.serviceLinkStatuses[this.serviceLinkRecord.serviceLinkStatuses.length - 1]?.payload.sl_status;
  }

  constructor(
    private nbMenuService: NbMenuService,
    private capeService: CapeSdkAngularService,
    private toastrService: NbToastrService,
    private cdr: ChangeDetectorRef,
    private route: ActivatedRoute,
    private router: Router,
    private errorDialogService: CapeSdkDialogService,
    private dialogService: NbDialogService,
    private translateService: TranslateService
  ) {
    this.menuItems = [{ title: this.translateService.instant('general.goToDashboard') as string }];
    this.translateService.setDefaultLang('en');
    this.translateService.use(this.locale);
  }

  async ngOnInit(): Promise<void> {
    /*
    Set as serviceId either the input sinkServiceId or sourceServiceId, depending on input Role
    */
    this.serviceId = this.serviceRole === RoleEnum.Sink ? this.sinkServiceId : this.sourceServiceId;
    /*


     * Register Menu Actions
     */
    this.nbMenuService
      .onItemClick()
      .pipe(takeUntil(this.unsubscribe))
      .pipe(
        filter(({ tag }) => tag === 'cape-context-menu'),
        map(({ item: { target } }) => target)
      )
      .subscribe(async (target) => {
        try {
          switch (target) {
            case 'Link Service':
              this.capeService.serviceLinkStatus$.pipe(takeUntil(this.unsubscribe)).subscribe(async (event) => {
                if (event?.serviceId === this.serviceId)
                  this.menuItems = [
                    {
                      title: this.translateService.instant('general.services.disableServiceLinkLabel') as string,
                      target: 'Disable Service Link',
                    },
                    {
                      title: this.translateService.instant('general.goToDashboard') as string,
                    },
                  ];

                this.menuItems.push({
                  title: this.translateService.instant('general.consent.giveConsentLabel') as string,
                  target: 'Give Consent',
                });

                this.serviceLinkRecord = await this.capeService.getServiceLinkRecordByUserIdAndServiceId(
                  this.sdkUrl,
                  this.userId,
                  this.serviceId,
                  this.operatorId
                );

                this.cdr.detectChanges();
              });

              await this.capeService.linkWithOperator(
                this.sdkUrl,
                this.locale,
                this.operatorId,
                this.serviceId,
                this.serviceName,
                this.userId,
                this.returnUrl,
                this.cdr
              );
              break;

            case 'Enable Service Link':
              await this.enableServiceLink();
              break;
            case 'Disable Service Link':
              await this.disableServiceLink();
              break;
            case 'Give Consent':
              await this.openConsentForm(this.dashboardUrl);
              break;
            case 'Disable Consent':
              await this.disableConsent();
              break;
            case 'Enable Consent':
              await this.enableConsent();
              break;
            case 'Withdraw Consent':
              await this.withdrawConsent();
              break;
            default:
              // Go to Dashboard
              window.open(this.dashboardUrl);
              break;
          }
        } catch (error) {
          this.errorDialogService.openErrorDialog(error);
        }
      });

    /*
     * Try to get service as registered service, otherwise return
     * */
    try {
      const serviceDescription = await this.capeService.getRegisteredService(this.sdkUrl, this.serviceId);

      if (serviceDescription) this.isServiceRegistered = this.capeService.emitIsRegisteredValue(true);
    } catch (error) {
      // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
      if (error.status !== 404) this.errorDialogService.openErrorDialog(error);
      else {
        this.isServiceRegistered = this.capeService.emitIsRegisteredValue(false);
        return;
      }
    }

    /*
     * Check Service Link
     */
    try {
      this.serviceLinkRecord = await this.capeService.getServiceLinkRecordByUserIdAndServiceId(
        this.sdkUrl,
        this.userId,
        this.serviceId,
        this.operatorId
      );
      this.capeService.emitServiceLinkEvent({
        serviceId: this.serviceId,
        status: this.serviceLinkStatus,
      } as ServiceLinkEvent);
    } catch (error) {
      // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
      if (error.status !== 404) this.errorDialogService.openErrorDialog(error);
    }

    if (!this.serviceLinkStatus)
      this.menuItems.push({
        title: this.translateService.instant('general.services.linkServiceLabel') as string,
        target: 'Link Service',
      });
    else if (this.serviceLinkStatus === SlStatusEnum.Removed)
      this.menuItems.push({
        title: this.translateService.instant('general.services.enableServiceLinkLabel') as string,
        target: 'Enable Service Link',
      });
    else {
      /*
       * Service Linked and Link active. Check Consent Record
       */
      this.menuItems.push({
        title: this.translateService.instant('general.services.disableServiceLinkLabel') as string,
        target: 'Disable Service Link',
      });

      try {
        // Get the first retrieved Consent Record (results are order by descending "iat" field)
        this.consentRecord = (
          await this.capeService.getConsentsByUserIdAndQuery(
            this.sdkUrl,
            this.checkConsentAtOperator,
            this.userId,
            this.sinkServiceId,
            this.sourceServiceId,
            undefined,
            undefined,
            this.purposeId
          )
        )[0];

        this.capeService.emitConsentRecordEvent({
          serviceId: this.consentRecord?.payload.common_part.subject_id,
          crId: this.consentRecord?.payload.common_part.cr_id,
          status: this.consentRecord?.consentStatusList[this.consentRecord?.consentStatusList.length - 1]?.payload,
        } as ConsentRecordEvent);
      } catch (error) {
        // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
        if (error.status !== 404) this.errorDialogService.openErrorDialog(error);
      }

      if (this.consentStatus === ConsentStatusEnum.Active)
        this.menuItems.push(
          {
            title: this.translateService.instant('general.consent.disableConsentLabel') as string,
            target: 'Disable Consent',
          },
          {
            title: this.translateService.instant('general.consent.withdrawConsentLabel') as string,
            target: 'Withdraw Consent',
          }
        );
      else if (this.consentStatus === ConsentStatusEnum.Disabled)
        this.menuItems.push(
          {
            title: this.translateService.instant('general.consent.enableConsentLabel') as string,
            target: 'Enable Consent',
          },
          {
            title: this.translateService.instant('general.consent.withdrawConsentLabel') as string,
            target: 'Withdraw Consent',
          }
        );
      else if (this.sourceServiceId)
        this.menuItems.push({
          title: this.translateService.instant('general.consent.giveConsentLabel') as string,
          target: 'Give Consent',
        });
    }

    this.cdr.detectChanges();
  }

  ngAfterViewInit(): void {
    const queryParams = this.route.snapshot.queryParams;
    /*
     * If it comes from a linking started from Cape, go to the linking dialog
     */
    if (queryParams.linkingFrom === LinkingFromEnum.Operator) {
      if (queryParams.sessionCode === undefined || '' || queryParams.returnUrl === undefined || '') {
        this.errorDialogService.openErrorDialog(new Error('One or more mandatory linking parameters are missing!'));
        return;
      }

      this.openedDialog = this.dialogService.open(this.linkingDialog, {
        context: {
          sessionCode: queryParams.sessionCode as string,
          returnUrl: queryParams.returnUrl as string,
        },
        hasScroll: true,
      });

      this.sessionCode = queryParams.sessionCode as string;
      this.locale = (queryParams.locale as string) || 'en';
      this.initialLocale = this.translateService.currentLang;
      this.translateService.use(this.locale);
      this.returnUrl = queryParams.returnUrl as string;
      this.linkingFrom = queryParams.linkingFrom as LinkingFromEnum;
      //this.cdr.detectChanges();
    }
  }

  ngOnDestroy(): void {
    this.locale = this.initialLocale;
    this.translateService.use(this.locale);
    this.unsubscribe.next();
    this.unsubscribe.complete();
  }

  async startLinkingAfterServiceLogin(): Promise<void> {
    try {
      await this.capeService.linkFromOperator(this.sdkUrl, this.sessionCode, this.operatorId, this.serviceId, this.serviceName, this.userId);

      const successMessage: string = this.translateService.instant('general.services.linkingSuccessfulMessage', {
        serviceId: this.serviceId,
        serviceName: this.serviceName,
      }) as string;

      this.openedDialog = this.dialogService.open(this.linkedDialog, {
        context: {
          message: successMessage,
        },
        hasScroll: false,
      });
    } catch (error) {
      this.errorDialogService.openErrorDialog(error);
    }
  }

  closeLinkedDialogAndReturnToOperator(message: string): void {
    window.onunload = () => {
      (window.opener as Window).postMessage(
        {
          result: 'SUCCESS',
          message: message,
          serviceId: this.serviceId,
          returnUrl: this.returnUrl,
        },
        this.returnUrl
      );
    };
    window.close();
  }

  closeLinkedDialog(message: string): void {
    (window.opener as Window).postMessage(
      {
        result: 'SUCCESS',
        message: message,
        serviceId: this.serviceId,
        returnUrl: this.returnUrl,
      },
      this.returnUrl
    );
    this.openedDialog.close();
    location.replace(location.href.split('?')[0]);
  }

  cancel(): void {
    (window.opener as Window).postMessage(
      {
        result: 'CANCELLED',
        message: this.translateService.instant('general.services.linkingCancelledMessage') as string,
        serviceId: this.serviceId,
        returnUrl: this.returnUrl,
      },
      this.returnUrl
    );

    this.openedDialog.close();
    //location.replace(location.href.split('?')[0]);
    window.close();
  }

  async openConsentForm(dashboardUrl: string): Promise<void> {
    try {
      this.dialogService.open(ConsentFormComponent, {
        hasScroll: false,
        autoFocus: true,
        closeOnBackdropClick: true,
        context: {
          sdkUrl: this.sdkUrl,
          consentForm: await this.capeService.fetchConsentForm(
            this.sdkUrl,
            this.userId,
            this.sinkServiceId,
            this.operatorId,
            this.purposeId,
            this.serviceRole,
            this.sourceServiceId,
            this.sourceDatasetId
          ),
        },
      });

      this.capeService.consentRecordStatus$.pipe(takeUntil(this.unsubscribe)).subscribe((event) => {
        if (event?.consentRecord) {
          this.consentRecord = event.consentRecord;
          this.menuItems = [
            { title: this.translateService.instant('general.goToDashboard') as string },
            {
              title: this.translateService.instant('general.services.disableServiceLinkLabel') as string,
              target: 'Disable Service Link',
            },
            {
              title: this.translateService.instant('general.consent.disableConsentLabel') as string,
              target: 'Disable Consent',
            },
            {
              title: this.translateService.instant('general.consent.withdrawConsentLabel') as string,
              target: 'Withdraw Consent',
            },
          ];
        }
      });
    } catch (error) {
      this.errorDialogService.openErrorDialog(error, dashboardUrl);
    }
  }

  async enableServiceLink(): Promise<void> {
    try {
      this.serviceLinkRecord.serviceLinkStatuses.push(
        await this.capeService.enableServiceLink(
          this.sdkUrl,
          this.serviceLinkRecord.payload.link_id,
          this.serviceLinkRecord.payload.surrogate_id,
          this.serviceId,
          this.serviceName
        )
      );
      this.menuItems = [
        { title: this.translateService.instant('general.goToDashboard') as string },
        {
          title: this.translateService.instant('general.services.disableServiceLinkLabel') as string,
          target: 'Disable Service Link',
        },
      ];

      switch (this.consentStatus) {
        case ConsentStatusEnum.Active:
          this.menuItems.push(
            {
              title: this.translateService.instant('general.consent.disableConsentLabel') as string,
              target: 'Disable Consent',
            },
            {
              title: this.translateService.instant('general.consent.withdrawConsentLabel') as string,
              target: 'Withdraw Consent',
            }
          );
          break;
        case ConsentStatusEnum.Disabled:
          this.menuItems.push(
            {
              title: this.translateService.instant('general.consent.enableConsentLabel') as string,
              target: 'Enable Consent',
            },
            {
              title: this.translateService.instant('general.consent.withdrawConsentLabel') as string,
              target: 'Withdraw Consent',
            }
          );
          break;
        default:
          if (this.sourceServiceId)
            this.menuItems.push({
              title: this.translateService.instant('general.consent.giveConsentLabel') as string,
              target: 'Give Consent',
            });
      }

      this.cdr.detectChanges();
    } catch (error) {
      this.errorDialogService.openErrorDialog(error);
    }
  }

  async disableServiceLink(): Promise<void> {
    try {
      this.serviceLinkRecord.serviceLinkStatuses.push(
        await this.capeService.disableServiceLink(
          this.sdkUrl,
          this.serviceLinkRecord.payload.link_id,
          this.serviceLinkRecord.payload.surrogate_id,
          this.serviceId,
          this.serviceName
        )
      );
      this.menuItems = [
        { title: this.translateService.instant('general.goToDashboard') as string },
        {
          title: this.translateService.instant('general.services.enableServiceLinkLabel') as string,
          target: 'Enable Service Link',
        },
      ];

      //switch (this.consentStatus) {
      //  case ConsentStatusEnum.Disabled:
      //    this.menuItems.push({ title: this.translateService.instant('general.consent.enableConsentLabel'), target: 'Enable Consent' },
      //      { title: this.translateService.instant('general.consent.withdrawConsentLabel'), target: 'Withdraw Consent' });
      //    break;
      //  default:
      //    if (this.sourceServiceId)
      //      this.menuItems.push({ title: this.translateService.instant('general.consent.giveConsentLabel'), target: 'Give Consent' });
      //}

      this.cdr.detectChanges();
    } catch (error) {
      this.errorDialogService.openErrorDialog(error);
    }
  }

  async disableConsent(): Promise<void> {
    try {
      this.consentRecord.consentStatusList.push(await this.capeService.disableConsent(this.sdkUrl, this.consentRecord));
      this.menuItems = [
        { title: this.translateService.instant('general.goToDashboard') as string },
        {
          title: this.translateService.instant('general.consent.enableConsentLabel') as string,
          target: 'Enable Consent',
        },
      ];

      this.menuItems.push(
        this.serviceLinkStatus === SlStatusEnum.Active
          ? {
              title: this.translateService.instant('general.services.disableServiceLinkLabel') as string,
              target: 'Disable Service Link',
            }
          : {
              title: this.translateService.instant('general.services.enableServiceLinkLabel') as string,
              target: 'Enable Service Link',
            }
      );

      this.cdr.detectChanges();
    } catch (error) {
      this.errorDialogService.openErrorDialog(error);
    }
  }

  async enableConsent(): Promise<void> {
    try {
      this.consentRecord.consentStatusList.push(await this.capeService.enableConsent(this.sdkUrl, this.consentRecord));
      this.menuItems = [
        { title: this.translateService.instant('general.goToDashboard') as string },
        {
          title: this.translateService.instant('general.consent.disableConsentLabel') as string,
          target: 'Disable Consent',
        },
      ];

      this.menuItems.push(
        this.serviceLinkStatus === SlStatusEnum.Active
          ? {
              title: this.translateService.instant('general.services.disableServiceLinkLabel') as string,
              target: 'Disable Service Link',
            }
          : {
              title: this.translateService.instant('general.services.enableServiceLinkLabel') as string,
              target: 'Enable Service Link',
            }
      );

      this.cdr.detectChanges();
    } catch (error) {
      // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
      if (error.status === 409) {
        this.openConsentUpdateConflictDialog(
          error,
          this.consentUpdateConflict,
          this.consentRecord.payload.common_part.subject_id,
          this.consentRecord.payload.common_part.slr_id
        );
      } else this.errorDialogService.openErrorDialog(error);
    }
  }

  openConsentUpdateConflictDialog(error: unknown, consentUpdateConflict: TemplateRef<unknown>, serviceId: string, slrId: string): void {
    this.dialogService.open(consentUpdateConflict, {
      context: {
        error: error,
        serviceId: serviceId,
        slrId: slrId,
      },
      hasScroll: false,
      closeOnBackdropClick: false,
      closeOnEsc: false,
    });
  }

  async withdrawConsent(): Promise<void> {
    try {
      this.consentRecord.consentStatusList.push(await this.capeService.withdrawConsent(this.sdkUrl, this.consentRecord));
      this.menuItems = [{ title: this.translateService.instant('general.goToDashboard') as string }];
      if (this.sourceServiceId)
        this.menuItems.push({
          title: this.translateService.instant('general.consent.giveConsentLabel') as string,
          target: 'Give Consent',
        });

      this.menuItems.push(
        this.serviceLinkStatus === SlStatusEnum.Active
          ? {
              title: this.translateService.instant('general.services.disableServiceLinkLabel') as string,
              target: 'Disable Service Link',
            }
          : {
              title: this.translateService.instant('general.services.enableServiceLinkLabel') as string,
              target: 'Enable Service Link',
            }
      );

      this.cdr.detectChanges();
    } catch (error) {
      this.errorDialogService.openErrorDialog(error);
    }
  }

  async refreshStatus(): Promise<void> {
    try {
      this.serviceLinkRecord = await this.capeService.getServiceLinkRecordByUserIdAndServiceId(
        this.sdkUrl,
        this.userId,
        this.serviceId,
        this.operatorId
      );
      this.capeService.emitServiceLinkEvent({
        serviceId: this.serviceId,
        status: this.serviceLinkRecord?.serviceLinkStatuses[this.serviceLinkRecord.serviceLinkStatuses.length - 1]?.payload.sl_status,
      } as ServiceLinkEvent);
    } catch (error) {
      // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
      if (error.status !== 404) this.errorDialogService.openErrorDialog(error);
    }

    this.menuItems = [{ title: this.translateService.instant('general.goToDashboard') as string }];

    if (!this.serviceLinkStatus)
      this.menuItems.push({
        title: this.translateService.instant('general.services.linkServiceLabel') as string,
        target: 'Link Service',
      });
    else if (this.serviceLinkStatus === SlStatusEnum.Removed)
      this.menuItems.push({
        title: this.translateService.instant('general.services.enableServiceLinkLabel') as string,
        target: 'Enable Service Link',
      });
    else {
      // Service Linked. Consent?
      this.menuItems.push({
        title: this.translateService.instant('general.services.disableServiceLinkLabel') as string,
        target: 'Disable Service Link',
      });

      try {
        // Get the first retrieved Consent Record (results are order by descending "iat" field)
        this.consentRecord = (
          await this.capeService.getConsentsByUserIdAndQuery(
            this.sdkUrl,
            this.checkConsentAtOperator,
            this.userId,
            this.serviceId,
            undefined,
            undefined,
            undefined,
            this.purposeId
          )
        )[0];

        this.capeService.emitConsentRecordEvent({
          serviceId: this.consentRecord?.payload.common_part.subject_id,
          crId: this.consentRecord?.payload.common_part.cr_id,
          status: this.consentRecord?.consentStatusList[this.consentRecord?.consentStatusList.length - 1]?.payload,
        } as ConsentRecordEvent);
      } catch (error) {
        // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
        if (error.status !== 404) this.errorDialogService.openErrorDialog(error);
      }

      if (this.consentStatus === ConsentStatusEnum.Active)
        this.menuItems.push(
          {
            title: this.translateService.instant('general.consent.disableConsentLabel') as string,
            target: 'Disable Consent',
          },
          {
            title: this.translateService.instant('general.consent.withdrawConsentLabel') as string,
            target: 'Withdraw Consent',
          }
        );
      else if (this.consentStatus === ConsentStatusEnum.Disabled)
        this.menuItems.push(
          {
            title: this.translateService.instant('general.consent.enableConsentLabel') as string,
            target: 'Enable Consent',
          },
          {
            title: this.translateService.instant('general.consent.withdrawConsentLabel') as string,
            target: 'Withdraw Consent',
          }
        );
      else
        this.menuItems.push({
          title: this.translateService.instant('general.consent.giveConsentLabel') as string,
          target: 'Give Consent',
        });
    }
  }
}
