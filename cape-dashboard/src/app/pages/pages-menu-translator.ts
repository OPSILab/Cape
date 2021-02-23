import { NbMenuItem } from '@nebular/theme';
import { TranslateService } from '@ngx-translate/core';
import { Injectable } from '@angular/core';

@Injectable()
export class PagesMenuTranslator {
  constructor(private translateService: TranslateService) {}

  translate(menuItems: NbMenuItem[]): NbMenuItem[] {
    // this.translateService.setDefaultLang('en');
    // this.translateService.use(this.configService.config.i18n.locale);

    for (const item of menuItems) {
      item.title = this.translateService.instant(item.title) as string;

      if (item.children) {
        for (const child of item.children) {
          child.title = this.translateService.instant(child.title) as string;
        }
      }
    }

    return menuItems;
  }
}
