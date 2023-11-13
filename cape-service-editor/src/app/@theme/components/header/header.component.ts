import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { NbMediaBreakpointsService, NbMenuService, NbSidebarService, NbThemeService } from '@nebular/theme';

import { map, takeUntil } from 'rxjs/operators';
import { Subject } from 'rxjs';
import { TranslateService } from '@ngx-translate/core';
import { OidcUserInformationService } from '../../../auth/services/oidc-user-information.service';
import { UserClaims } from '../../../auth/model/oidc';
import { NgxConfigureService } from 'ngx-configure';
import { AppConfig } from '../../../model/appConfig';

@Component({
  selector: 'ngx-header',
  styleUrls: ['./header.component.scss'],
  templateUrl: './header.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HeaderComponent implements OnInit, OnDestroy {
  private destroy$: Subject<void> = new Subject<void>();
  userPictureOnly = false;
  user: UserClaims;
  private config: AppConfig;

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

  loggedUserMenu = [{ title: 'Account', link: 'pages/account' }];
  userMenu = [{ title: 'Log in', link: '/login' }];

  public languages=[];
  public userLanguage : String;

  constructor(
    private sidebarService: NbSidebarService,
    private menuService: NbMenuService,
    private themeService: NbThemeService,
    private breakpointService: NbMediaBreakpointsService,
    private translateService: TranslateService,
    private userService: OidcUserInformationService,
    private cdr: ChangeDetectorRef,
    private configService: NgxConfigureService
  ) {
    this.config = this.configService.config as AppConfig;
  }

  ngOnInit(): void {
    let lan = this.config.i18n.languages;

    this.userLanguage=this.config.i18n.locale

    lan.forEach(x=>{
        let f=x;
         //this.languages.push({lan:x,flag: `flag-icon flag-icon-${f} flag-icon-squared` })
       this.languages.push({lan:x,flag: f,picture:`assets/flags/${f}.svg` })
      })

    this.loggedUserMenu.push({ title: this.translateService.instant('login.logout_button') as string, link: '' });
    this.currentTheme = this.themeService.currentTheme;
    this.userService.onUserChange().subscribe((user: UserClaims) => (this.user = user));

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
        map(({ name }) => name as string),
        takeUntil(this.destroy$)
      )
      .subscribe((themeName) => (this.currentTheme = themeName));
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  getDefLang(){
    return this.translateService.getDefaultLang();
  }

  changeLang(event){
    this.translateService.use(event);
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
