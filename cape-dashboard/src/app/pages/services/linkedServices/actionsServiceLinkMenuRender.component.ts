import { Component, OnInit, Input, OnDestroy, ViewChild, TemplateRef } from '@angular/core';
import { LinkedServicesService } from './linkedServices.service';
import { TranslateService } from '@ngx-translate/core';
import { LinkedServiceRow } from './linkedServices.component';
import { NbMenuItem, NbMenuService, NbWindowService } from '@nebular/theme';
import { Subject } from 'rxjs';
import { filter, takeUntil } from 'rxjs/operators';
import { FormControl, FormGroup } from '@angular/forms';
import { FormBuilder } from '@angular/forms';

@Component({
  template: `
    <button nbButton outline status="basic" [nbContextMenu]="actions" nbContextMenuTag="service-context-menu-{{ value.service_id }}">
      <nb-icon icon="settings-2"></nb-icon>
    </button>
    <ng-template #sendRequestWindowTemplate let-data>
      <form [formGroup]="sendRequestForm" (ngSubmit)="onSubmitRequest(requestMessage)">
        <div class="row align-items-center justify-content-center mb-3 flex-column">
          <label class="text-center">{{ 'general.linked_services.request_message' | translate }}</label>
          <textarea nbInput type="text" id="textarea" name="textarea" rows="4" cols="50" formControlName="sendRequestText"></textarea>
        </div>
        <div class="row justify-content-center">
          <button nbButton type="submit" status="primary" size="medium">
            {{ 'general.submit_button' | translate }}
          </button>
        </div>
      </form>
    </ng-template>
  `,
})
export class ActionsServiceLinkMenuRenderComponent implements OnInit, OnDestroy {
  @Input() value: LinkedServiceRow;
  private unsubscribe: Subject<void> = new Subject();

  actions: NbMenuItem[];
  actionsLabel: string;

  sendRequestForm: FormGroup;
  sendRequestTitle: string;
  get requestMessage(): string {
    return this.sendRequestForm.get('sendRequestText').value as string;
  }
  @ViewChild('sendRequestWindowTemplate', { static: false }) sendRequestWindowTemplate: TemplateRef<unknown>;

  constructor(
    protected linkedServicesService: LinkedServicesService,
    private translate: TranslateService,
    private menuService: NbMenuService,
    private windowService: NbWindowService,
    private formBuilder: FormBuilder
  ) {}

  ngOnInit(): void {
    this.sendRequestForm = this.formBuilder.group({
      sendRequestText: [''],
    });
    this.sendRequestTitle = this.translate.instant('general.linked_services.send_request') as string;

    this.actions = this.translatedActionLabels();
    this.menuService
      .onItemClick()
      .pipe(takeUntil(this.unsubscribe))
      .pipe(filter(({ tag }) => tag === 'service-context-menu-' + this.value.service_id))
      .subscribe((event) => {
        switch (event.item.data) {
          case 'enable_notifications':
            break;
          case 'send_request':
            this.openSendRequest();
            break;

          default:
        }
      });
  }

  ngOnDestroy(): void {
    this.unsubscribe.next();
    this.unsubscribe.complete();
  }

  translatedActionLabels(): NbMenuItem[] {
    return [
      {
        title: this.translate.instant('general.linked_services.enable_notifications') as string,
        data: 'enable_notifications',
      },
      {
        title: this.translate.instant('general.linked_services.send_request') as string,
        data: 'send_request',
      },
    ];
  }

  openSendRequest = (): void => {
    this.windowService.open(this.sendRequestWindowTemplate, {
      title: this.sendRequestTitle,
      context: { text: '' },
    });
  };

  onSubmitRequest = (message: string): void => {
    console.log(`Submit some kind of request with message: ${message}`);
  };
}
