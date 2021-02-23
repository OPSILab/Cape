import { Component, OnInit, Input } from '@angular/core';
import { LinkedServicesService } from './linkedServices.service';
import { TranslateService } from '@ngx-translate/core';
import { LinkedServiceRow } from './linkedServices.component';

@Component({
  template: `
    <button nbButton outline status="basic" [nbContextMenu]="actions">
      <nb-icon icon="settings-2"></nb-icon>
    </button>
  `,
})
export class ActionsServiceLinkMenuRenderComponent implements OnInit {
  @Input() value: LinkedServiceRow;

  actions: { title: string }[];

  actionsLabel: string;

  constructor(protected service: LinkedServicesService, private translate: TranslateService) {}

  ngOnInit(): void {
    this.actions = this.translatedActionLabels();
  }

  translatedActionLabels(): { title: string }[] {
    return [
      {
        title: this.translate.instant('general.linked_services.notify') as string,
      },
      {
        title: this.translate.instant('general.linked_services.send_objection') as string,
      },
    ];
  }
}
