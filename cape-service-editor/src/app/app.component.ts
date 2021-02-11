/**
 * @license
 * Copyright Akveo. All Rights Reserved.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */
import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { NbMenuService } from '@nebular/theme';
import { LoginService } from './login/login.service';
import { NbIconLibraries } from '@nebular/theme';
import { TranslateService } from '@ngx-translate/core';
import { NgxConfigureService } from 'ngx-configure';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'ngx-app',
  template: '<router-outlet></router-outlet>'
})
export class AppComponent implements OnInit, OnDestroy {

  private unsubscribe: Subject<void> = new Subject();

  constructor(private iconLibraries: NbIconLibraries, private menuService: NbMenuService, private loginService: LoginService,
    private translateService: TranslateService, private configService: NgxConfigureService, private cdr: ChangeDetectorRef) {

    this.iconLibraries.registerFontPack('md-icon', { packClass: 'material-icons', iconClassPrefix: '' });
    // this.iconLibraries.setDefaultPack('md-icon');
    this.translateService.use(localStorage.getItem('currentLocale') || this.configService.config.i18n.locale);
  }

  onContecxtItemSelection(title) {

    if (title === this.translateService.instant('general.login.logout_button')) {
      this.loginService.logout();
    }
  }

  ngOnInit() {

    this.menuService.onItemClick().pipe(takeUntil(this.unsubscribe))
      .subscribe(event => {
        this.onContecxtItemSelection(event.item.title);
      });

  }

  ngOnDestroy() {

    console.log('ngOnDestroy');
    this.unsubscribe.next();
    this.unsubscribe.complete();
  }
}
