import { Component, OnInit } from '@angular/core';
import { NbIconLibraries, NbMenuService } from '@nebular/theme';
import { LoginService } from './login/login.service';
import { TranslateService } from '@ngx-translate/core';
import { NgxConfigureService } from 'ngx-configure';

@Component({
  selector: 'app-root',
  template: '<router-outlet></router-outlet>'
})
export class AppComponent implements OnInit {

  constructor(private configService: NgxConfigureService, private iconLibraries: NbIconLibraries, private menuService: NbMenuService, private loginService: LoginService, private translate: TranslateService) {

    this.menuService.onItemClick()
      .subscribe((event) => {
        this.onContecxtItemSelection(event.item.title);
      });
    this.iconLibraries.registerFontPack('md-icon', { packClass: 'material-icons', iconClassPrefix: 'material-icons' });
    //this.iconLibraries.setDefaultPack('md-icon');

    this.translate.setDefaultLang('en');
    this.translate.use(this.configService.config.i18n.locale);
  }

  onContecxtItemSelection(title) {
    if (title === 'Log out') {
      this.loginService.logout();
      location.reload();
    }
  }

  ngOnInit() {

  }
}

