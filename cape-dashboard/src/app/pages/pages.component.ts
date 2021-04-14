import { ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { ChildrenOutletContexts } from '@angular/router';
import { NbMenuItem } from '@nebular/theme';
import { TranslateService } from '@ngx-translate/core';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

import { MENU_ITEMS } from './pages-menu';

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
export class PagesComponent implements OnInit, OnDestroy {
  menu: NbMenuItem[];
  private unsubscribe: Subject<void> = new Subject();

  constructor(private translateService: TranslateService, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    // if put on constructor it will doing twice when refresh a page.
    this.menu = this.translate(MENU_ITEMS);
    this.translateService.onLangChange.pipe(takeUntil(this.unsubscribe)).subscribe(() => {
      this.menu = this.translate(MENU_ITEMS);
      this.cdr.detectChanges();
    });
  }

  ngOnDestroy(): void {
    console.log('ngOnDestroy');
    this.unsubscribe.next();
    this.unsubscribe.complete();
  }

  translate(menuItems: NbMenuItem[]): NbMenuItem[] {
    return menuItems.map((item) => {
      return {
        ...item,
        title: this.translateService.instant(item.title) as string,
        // children: item.children.map((child) => {
        //   return { ...child, title: this.translateService.instant(child.title) as string };
        // }),
      } as NbMenuItem;
    });
  }
}
