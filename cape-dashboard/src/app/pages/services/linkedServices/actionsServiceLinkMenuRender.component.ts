import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { ViewCell } from 'ng2-smart-table';
import { LinkedServicesService } from './linkedServices.service';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { NgxConfigureService } from 'ngx-configure';
import { map } from 'rxjs/operators';
import { combineLatest, Observable } from 'rxjs';

@Component({
  template: `
<button nbButton outline status="basic" [nbContextMenu]="actions$ | async"><nb-icon icon="settings-2"></nb-icon></button>
  `
})
export class ActionsServiceLinkMenuRenderComponent implements OnInit {

  public renderValue;

  @Input() value;

  actions$: Observable<{ title: string }[]>

  actionsLabel: string;

  constructor(protected service: LinkedServicesService, private router: Router, private translate: TranslateService,
    private configService: NgxConfigureService) {

  }

  ngOnInit() {
    this.renderValue = this.value;
    this.actions$ = this.translatedActionLabels()
  }

  translatedActionLabels() {
    return combineLatest([
      this.translate.get('general.linked_services.notify'),
      this.translate.get('general.linked_services.send_objection'),
    ]).pipe(map(([notify, send_objection]) => {
      const actions = [{
        title: notify
      },
      {
        title: send_objection
      }
      ];
      return actions;
    }));
  }

}
