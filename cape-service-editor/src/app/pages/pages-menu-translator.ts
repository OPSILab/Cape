import { NbMenuItem } from '@nebular/theme';
import { TranslateService } from '@ngx-translate/core';
import { Injectable, OnDestroy } from '@angular/core';
import { Subscription } from "rxjs/Subscription";
import { NgxConfigureService } from 'ngx-configure';

@Injectable()
export class PagesMenuTranslator implements OnDestroy {
  private menuItem$ = new Subscription();

  constructor(private translateService: TranslateService, private configService: NgxConfigureService) {
  }

  translate(menuItems: NbMenuItem[]) : NbMenuItem[] {
    // deep copy array (without reference)
    const translationMenu = JSON.parse(JSON.stringify(menuItems));
    this.translateService.setDefaultLang('en');
    this.translateService.use(this.configService.config.i18n.locale);
    for (let mainIndex = 0; mainIndex < menuItems.length; mainIndex++) {
      const mainSubscription = this.translateService.stream(menuItems[mainIndex].title).subscribe(res => {
        const mainMenu = translationMenu[mainIndex];
        mainMenu.title = res;
      });

      this.menuItem$.add(mainSubscription);

      if (menuItems[mainIndex].children) {
        for (let childIndex = 0; childIndex < menuItems[mainIndex].children.length; childIndex++) {
          const childSubscription = this.translateService.stream(menuItems[mainIndex].children[childIndex].title).subscribe(res => {            
            const subMenu =  translationMenu[mainIndex].children[childIndex];            
            subMenu.title = res;
          });

          this.menuItem$.add(childSubscription);          
        }
      }
    }

    return translationMenu;
  }

  ngOnDestroy() {
    this.menuItem$.unsubscribe();
  }

}