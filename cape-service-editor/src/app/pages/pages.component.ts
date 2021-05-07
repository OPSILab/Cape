import { Component, OnInit } from '@angular/core';
import { NbAccessChecker } from '@nebular/security';
import { NbMenuItem } from '@nebular/theme';
import { take } from 'rxjs/operators';

import { PagesMenuTranslator } from './pages-menu-translator';

@Component({
  selector: 'ngx-pages',
  styleUrls: ['pages.component.scss'],
  template: `
    <ngx-one-column-layout>
      <nb-menu [items]="menu"></nb-menu>
      <router-outlet></router-outlet>
    </ngx-one-column-layout>
  `,
})
export class PagesComponent implements OnInit {
  public menu: NbMenuItem[];
  private hideServicesMenu: boolean;
  private hideConsentsMenu: boolean;

  constructor(private translator: PagesMenuTranslator, private accessChecker: NbAccessChecker) {}

  ngOnInit() {
    this.accessChecker
      .isGranted('view', 'services')
      .pipe(take(1))
      .subscribe((granted: boolean) => {
        this.hideServicesMenu = !granted;
      });

    this.accessChecker
      .isGranted('view', 'consents')
      .pipe(take(1))
      .subscribe((granted: boolean) => {
        this.hideConsentsMenu = !granted;
      });

    const MENU_ITEMS: NbMenuItem[] = [
      {
        title: 'general.menu.dashboard',
        icon: 'pie-chart-outline',
        link: '/pages/dashboard',
      },
      {
        title: 'general.menu.consents_logs_group',
        group: true,
        hidden: this.hideConsentsMenu,
      },
      {
        title: 'general.menu.consents',
        icon: 'toggle-left-outline',
        link: '/pages/consents/register',
        hidden: this.hideConsentsMenu,
      },
      {
        title: 'general.menu.services',
        group: true,
        hidden: this.hideServicesMenu,
      },
      {
        title: 'general.menu.availableservices',
        icon: 'grid-outline',
        link: '/pages/services/availableServices',
        home: true,
        hidden: this.hideServicesMenu,
      },
      {
        title: 'general.menu.editor',
        icon: 'edit-outline',
        link: '/pages/services/service-editor',
        hidden: this.hideServicesMenu,
      },
    ];

    // if put on constructor it will doing twice when refresh a page.
    this.menu = this.translator.translate(MENU_ITEMS);
  }
}
