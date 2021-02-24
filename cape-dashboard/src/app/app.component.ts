import { Component, OnInit, OnDestroy } from '@angular/core';
import { NbMenuService } from '@nebular/theme';
import { LoginService } from './login/login.service';
import { NbIconLibraries } from '@nebular/theme';
import { TranslateService } from '@ngx-translate/core';
import { NgxConfigureService } from 'ngx-configure';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { AppConfig } from './model/appConfig';
import { ErrorDialogService } from './pages/error-dialog/error-dialog.service';

@Component({
  selector: 'ngx-app',
  template: '<router-outlet></router-outlet>',
})
export class AppComponent implements OnInit, OnDestroy {
  private unsubscribe: Subject<void> = new Subject();

  constructor(
    private iconLibraries: NbIconLibraries,
    private menuService: NbMenuService,
    private loginService: LoginService,
    private translateService: TranslateService,
    private configService: NgxConfigureService,
    private errorDialogService: ErrorDialogService
  ) {
    this.iconLibraries.registerFontPack('md-icon', {
      packClass: 'material-icons',
    });

    this.iconLibraries.registerFontPack('fa', {
      packClass: 'fa',
      iconClassPrefix: 'fa',
    });
    // this.iconLibraries.setDefaultPack('md-icon');
    this.translateService.use(localStorage.getItem('currentLocale') || (this.configService.config as AppConfig).i18n.locale);
  }

  onContecxtItemSelection(title: string): void {
    if (title === this.translateService.instant('general.login.logout_button')) {
      this.loginService.logout().catch((error) => this.errorDialogService.openErrorDialog(error));
    }
  }

  ngOnInit(): void {
    this.menuService
      .onItemClick()
      .pipe(takeUntil(this.unsubscribe))
      .subscribe((event) => {
        this.onContecxtItemSelection(event.item.title);
      });
  }

  ngOnDestroy(): void {
    console.log('ngOnDestroy');
    this.unsubscribe.next();
    this.unsubscribe.complete();
  }
}
