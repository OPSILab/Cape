import { Component, OnDestroy, OnInit } from '@angular/core';
import { NbIconLibraries, NbMenuService } from '@nebular/theme';
import { LoginService } from './login/login.service';
import { TranslateService } from '@ngx-translate/core';
import { NgxConfigureService } from 'ngx-configure';
import { takeUntil } from 'rxjs/operators';
import { Subject } from 'rxjs';

@Component({
  selector: 'app-root',
  template: '<router-outlet></router-outlet>'
})
export class AppComponent implements OnInit, OnDestroy {

  private unsubscribe: Subject<void> = new Subject();

  constructor(private configService: NgxConfigureService, private iconLibraries: NbIconLibraries, 
    private menuService: NbMenuService, private loginService: LoginService, private translate: TranslateService) {

    this.iconLibraries.registerFontPack('md-icon', { packClass: 'material-icons', iconClassPrefix: 'material-icons' });
    //this.iconLibraries.setDefaultPack('md-icon');

    this.translate.setDefaultLang('en');
    this.translate.use(this.configService.config.i18n.locale);
  }

  onContecxtItemSelection(title) {

    if (title === this.translate.instant('general.login.logout_button')) {
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

