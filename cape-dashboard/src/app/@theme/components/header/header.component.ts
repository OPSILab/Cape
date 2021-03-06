import { ChangeDetectionStrategy, Component, OnDestroy, OnInit } from '@angular/core';
import { NbMediaBreakpointsService, NbMenuService, NbSidebarService, NbThemeService } from '@nebular/theme';

import { map, takeUntil } from 'rxjs/operators';
import { Subject } from 'rxjs';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'ngx-header',
  styleUrls: ['./header.component.scss'],
  templateUrl: './header.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HeaderComponent implements OnInit, OnDestroy {
  private destroy$: Subject<void> = new Subject<void>();
  userPictureOnly = false;
  user: { name: string; picture: string };

  themes = [
    {
      value: 'default',
      name: 'Light',
    },
    {
      value: 'cape',
      name: 'Cape',
    },
    {
      value: 'dark',
      name: 'Dark',
    },
    {
      value: 'corporate',
      name: 'Corporate',
    },
  ];

  currentTheme = 'cape';

  loggedUserMenu = [{ title: 'Account', link: '/pages/account' }];
  userMenu = [{ title: 'Log in', link: '/login' }];

  constructor(
    private sidebarService: NbSidebarService,
    private menuService: NbMenuService,
    private themeService: NbThemeService,
    private breakpointService: NbMediaBreakpointsService,
    private translateService: TranslateService
  ) {}

  ngOnInit(): void {
    this.loggedUserMenu.push({
      title: this.translateService.instant('general.login.logout_button') as string,
      link: '',
    });
    this.currentTheme = this.themeService.currentTheme;

    // this.userService.getUsers()
    //  .pipe(takeUntil(this.destroy$))
    //  .subscribe((users: any) => this.user = users.nick);

    const loggedUser = localStorage.accountId as string;
    if (loggedUser) this.user = { name: loggedUser, picture: '' };

    const { xl } = this.breakpointService.getBreakpointsMap();
    this.themeService
      .onMediaQueryChange()
      .pipe(
        map(([, currentBreakpoint]) => currentBreakpoint.width < xl),
        takeUntil(this.destroy$)
      )
      .subscribe((isLessThanXl: boolean) => (this.userPictureOnly = isLessThanXl));

    this.themeService
      .onThemeChange()
      .pipe(
        map(({ name }) => name as string),
        takeUntil(this.destroy$)
      )
      .subscribe((themeName) => (this.currentTheme = themeName));
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  changeTheme(themeName: string): void {
    this.themeService.changeTheme(themeName);
  }

  toggleSidebar(): boolean {
    this.sidebarService.toggle(true, 'menu-sidebar');
    return false;
  }

  navigateHome(): boolean {
    this.menuService.navigateHome();
    return false;
  }
}
