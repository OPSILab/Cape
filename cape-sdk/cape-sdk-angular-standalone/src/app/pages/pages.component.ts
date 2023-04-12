import { Component } from '@angular/core';

import { MENU_ITEMS } from './pages-menu';

@Component({
  selector: 'ngx-pages',
  styleUrls: ['pages.component.scss'],
  template: `
    <nb-layout >
      

      <!-- <nb-sidebar class="menu-sidebar" tag="menu-sidebar" responsive start>
        <nb-menu [items]="menu"></nb-menu>
      </nb-sidebar> -->

      <nb-layout-column>
        <router-outlet></router-outlet>
      </nb-layout-column>

      
    </nb-layout>
  `,
})
export class PagesComponent {
  // menu = MENU_ITEMS;
}
