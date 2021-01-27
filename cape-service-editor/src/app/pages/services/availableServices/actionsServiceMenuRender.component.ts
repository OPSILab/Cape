import { Component, Input, Output, OnInit, TemplateRef, ChangeDetectorRef, EventEmitter, OnDestroy, ViewChild } from '@angular/core';
import { ViewCell } from 'ng2-smart-table';

import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { NgxConfigureService } from 'ngx-configure';
import { map, filter, takeUntil } from 'rxjs/operators';
import { combineLatest, Observable, Subscription, Subject } from 'rxjs';
import { NbMenuService, NbToastrService, NbDialogService, NbDialogRef, NbGlobalLogicalPosition, NbComponentStatus, NbGlobalPhysicalPosition } from '@nebular/theme';
import { ErrorDialogService } from '../../error-dialog/error-dialog.service';
import { AvailableServicesService } from './availableServices.service';
import { DialogRegisterPromptComponent } from './dialog-register-prompt/dialog-register-prompt.component';
import { DialogDeRegisterPromptComponent } from './dialog-deregister-prompt/dialog-deregister-prompt.component';

interface Row {
  serviceId: string;
  serviceName: string;
  serviceDescription: any;
}


@Component({
  template: `
<button nbButton outline status="basic" [nbContextMenu]="actions$ | async" nbContextMenuTag="service-context-menu{{renderValue.serviceId}}" ><nb-icon icon="settings-2"></nb-icon></button>
  <!-- Delete Service modal ng-template -->
<ng-template #confirmDeleteDialog let-data let-ref="dialogRef">
  <nb-card status="primary">
    <nb-card-body class="p-5">{{ 'general.editor.confirm_delete'|translate: {name:data.name} }}</nb-card-body>
    <nb-card-footer class="d-flex justify-content-center">
      <button nbButton status="primary" size="small" (click)="data.callback()">
        {{'general.editor.delete'|translate}}
      </button>
      <button nbButton class="ml-2" ghost shape="rectangle" status="primary" (click)="ref.close()">
        {{'general.editor.cancel' | translate}}
      </button>
    </nb-card-footer>
  </nb-card>
</ng-template>
  `
})
export class ActionsServiceMenuRenderComponent implements OnInit, OnDestroy {

  public renderValue;
  private unsubscribe: Subject<void> = new Subject();

  @Input() value: Row;
  @Output() updateResult = new EventEmitter<any>();

  actions$: Observable<{ title: string }[]>

  actionsLabel: string;

  test: Subscription;

  registered: boolean = true;
  @ViewChild('confirmDeleteDialog', { static: false }) confirmDeleteDialogTemplate: TemplateRef<any>;


  constructor(private availableServicesService: AvailableServicesService, private menuService: NbMenuService, private router: Router, private translate: TranslateService, private errorDialogService: ErrorDialogService,
    private toastrService: NbToastrService, private dialogService: NbDialogService,
    private configService: NgxConfigureService, private translateService: TranslateService) {
    this.renderValue = this.value;
  }

  ngOnInit() {

    this.renderValue = this.value;

    var cert = this.value.serviceDescription.serviceInstance.cert?.x5c;
    if (cert == undefined || cert == null || cert.length == 0) {
      this.registered = false;
    }

    this.actions$ = this.translatedActionLabels();
    this.menuService.onItemClick().pipe(takeUntil(this.unsubscribe))
      .pipe(
        filter(({ tag }) => tag === 'service-context-menu' + this.value.serviceId),
      )
      .subscribe((event) => {
        console.log(event)

        switch (event.item.data) {
          case 'edit':
            this.onEdit(this.value.serviceId);
            break;
          case 'delete':
            this.openDeleteFromRegistryDialog(this.value.serviceId, this.value.serviceName);
            break;
          case 'register':
            this.openRegisterDialog();
            break;
          case 'consents':
            this.viewConsents(this.value.serviceId);
            break;
          case 'deregister':
            this.openDeRegisterDialog();
            break;
          default:
        }

      }
      );
  }

  ngOnDestroy(): void {
    this.unsubscribe.next();
    this.unsubscribe.complete();
  }

  translatedActionLabels() {
    if (this.registered) {

      return combineLatest([

        this.translate.get('general.services.deregister'),
        this.translate.get('general.services.delete'),
        this.translate.get('general.services.consents'),
      ]).pipe(map(([deregister, delete_service, consents]) => {
        const actions = [{
          title: deregister, data: "deregister"
        },
        {
          title: delete_service, data: "delete"
        },
        {
          title: consents, data: "consents"
        }
        ];
        return actions;
      }));

    }
    else {
      return combineLatest([
        this.translate.get('general.services.edit'),
        this.translate.get('general.services.register'),
        this.translate.get('general.services.delete'),

      ]).pipe(map(([edit, register, delete_service]) => {
        const actions = [{
          title: edit, data: "edit"
        },
        {
          title: register, data: "register"
        },
        {
          title: delete_service, data: "delete"
        }
        ];
        return actions;
      }));

    }


  }


  onEdit(serviceId): void {
    this.router.navigate(['/pages/services/service-editor', { "serviceId": serviceId }]);
  }

  viewConsents(serviceId): void {
    this.router.navigate(['/pages/consents/register', { "serviceId": serviceId }]);
  }


  openRegisterDialog() {
    this.dialogService.open(DialogRegisterPromptComponent, {
      hasScroll: false,
      context: {
        serviceId: this.value.serviceId
      },
    }).onClose.subscribe(confirm => { if (confirm) this.onRegisterService() });


  }

  openDeRegisterDialog() {
    this.dialogService.open(DialogDeRegisterPromptComponent, {
      hasScroll: false,
      context: {
        serviceId: this.value.serviceId
      },
    }).onClose.subscribe(confirm => { if (confirm) this.onDeRegisterService() });


  }


  onRegisterService = async () => {

    try {
      this.value.serviceDescription = await this.availableServicesService.registerService(this.value.serviceDescription.serviceId);

      this.toastrService.primary('', this.translate.instant('general.services.service_registered_message', { serviceName: this.value.serviceDescription.serviceName }),
        { position: NbGlobalLogicalPosition.BOTTOM_END, duration: 2500 });
      this.updateResult.emit(this.value.serviceDescription);

    } catch (error) {
      if (error.error?.statusCode === '401') {
        this.router.navigate(['/login']);
      } else
        this.errorDialogService.openErrorDialog(error);
    }
  }


  onDeRegisterService = async () => {

    try {
      await this.availableServicesService.deregisterService(this.value.serviceDescription.serviceId);

      this.toastrService.primary('', this.translate.instant('general.services.service_deregistered_message', { serviceName: this.value.serviceDescription.serviceName }),
        { position: NbGlobalLogicalPosition.BOTTOM_END, duration: 2500 });
      this.value.serviceDescription.serviceInstance.cert = null;
      this.updateResult.emit(this.value.serviceDescription);

    } catch (error) {
      if (error.error?.statusCode === '401') {
        this.router.navigate(['/login']);
      } else
        this.errorDialogService.openErrorDialog(error);
    }
  }


  openDeleteFromRegistryDialog(serviceId: string, serviceName: string) {

    const ref = this.dialogService.open(this.confirmDeleteDialogTemplate,
      {
        context: {
          name: serviceName,
          callback: async () => {

            try {
              await this.availableServicesService.deleteService(serviceId);
              this.showToast('success', "", this.translateService.instant('general.editor.delete_success',
                { name: serviceName }));
              ref.close();
              this.updateResult.emit(this.value.serviceId);
            } catch (error) {
              this.errorDialogService.openErrorDialog(error);
            }
          }
        }
      });
  }


  private showToast(type: NbComponentStatus, title: string, body: string) {

    const config = {
      status: type,
      destroyByClick: true,
      duration: 2000,
      hasIcon: true,
      position: NbGlobalPhysicalPosition.TOP_RIGHT,
      preventDuplicates: true,
    };
    const titleContent = title;

    this.toastrService.show(
      body,
      `${titleContent}`);
  }


}