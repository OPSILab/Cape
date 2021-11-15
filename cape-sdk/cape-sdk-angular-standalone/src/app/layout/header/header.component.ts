import { ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { NbMediaBreakpointsService, NbMenuService, NbSidebarService, NbThemeService } from '@nebular/theme';
import { map, takeUntil } from 'rxjs/operators';
import { Subject } from 'rxjs';
import { TranslateService } from '@ngx-translate/core';
// import { OidcUserInformationService } from 'src/app/auth/services/oidc-user-information.service';
import { UserClaims } from 'src/app/auth/model/oidc';

@Component({
  selector: 'ngx-header',
  styleUrls: ['./header.component.scss'],
  templateUrl: './header.component.html',
})
export class HeaderComponent implements OnInit, OnDestroy {
  private destroy$: Subject<void> = new Subject<void>();
  userPictureOnly: boolean = false;
  user: UserClaims;

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

  currentTheme = 'default';

  loggedUserMenu = [{ title: 'Account', link: '/account' }];
  userMenu = [{ title: 'Log in', link: '/login' }];

  constructor(
    private sidebarService: NbSidebarService,
    private menuService: NbMenuService,
    private themeService: NbThemeService,
    private breakpointService: NbMediaBreakpointsService,
    private translateService: TranslateService,
    // private userService: OidcUserInformationService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.loggedUserMenu.push({ title: this.translateService.instant('login.logout_button') as string, link: '' });
    this.currentTheme = this.themeService.currentTheme;

    // this.userService.onUserChange().subscribe((user: UserClaims) => (this.user = user));

    const { xl } = this.breakpointService.getBreakpointsMap();
    this.themeService
      .onMediaQueryChange()
      .pipe(
        map(([, currentBreakpoint]) => currentBreakpoint.width < xl),
        takeUntil(this.destroy$)
      )
      .subscribe((isLessThanXl: boolean) => {
        this.userPictureOnly = isLessThanXl;
        this.cdr.detectChanges();
      });
    this.themeService
      .onThemeChange()
      .pipe(
        map(({ name }) => name),
        takeUntil(this.destroy$)
      )
      .subscribe((themeName) => (this.currentTheme = themeName));
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  changeTheme(themeName: string) {
    this.themeService.changeTheme(themeName);
  }

  toggleSidebar(): boolean {
    this.sidebarService.toggle(true, 'menu-sidebar');

    return false;
  }

  navigateHome() {
    this.menuService.navigateHome();
    return false;
  }
}
