import { Component, OnInit } from '@angular/core';
import { NgxConfigureService } from 'ngx-configure';
import { Router, ActivatedRoute } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-ge1',
  templateUrl: './ge1.component.html',
  styleUrls: ['./ge1.component.scss'],
})
export class Ge1Component implements OnInit {
  accountId: string;
  config: any;
  locale: string;

  selectedService: string;

  constructor(
    private configService: NgxConfigureService,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private translateService: TranslateService
  ) {
    this.config = configService.config;
  }

  ngOnInit() {
    this.accountId = localStorage.getItem('serviceAccountId');
    const queryParams = this.activatedRoute.snapshot.queryParams;
    this.locale = queryParams.locale || this.config.i18n.locale;
    this.translateService.setDefaultLang('en');
    this.translateService.use(this.locale);
  }

  onClickGo = () => {
    switch (this.selectedService) {
      case 'new':
        this.router.navigate(['new'], { relativeTo: this.activatedRoute, queryParamsHandling: 'preserve' });
        break;

      case 'list':
        this.router.navigate(['list'], { relativeTo: this.activatedRoute, queryParamsHandling: 'preserve' });
        break;
      default:
        break;
    }
  };
}
