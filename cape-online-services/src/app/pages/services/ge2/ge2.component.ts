import { Component, OnInit } from '@angular/core';
import { NgxConfigureService } from 'ngx-configure';
import { Router, ActivatedRoute } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-ge2',
  templateUrl: './ge2.component.html',
  styleUrls: ['./ge2.component.scss']
})
export class Ge2Component implements OnInit {

  accountId: string;
  config: any;
  locale: string;

  selectedService: string;

  constructor(private configService: NgxConfigureService, private router: Router, private activatedRoute: ActivatedRoute,
    private translateService: TranslateService) {

    this.config = configService.config;
  }

  ngOnInit() {


    this.accountId = localStorage.getItem('accountId');
    const queryParams = this.activatedRoute.snapshot.queryParams;
    this.locale = queryParams.locale || this.config.i18n.locale; // TODO default value taken from User language preferences;
    this.translateService.setDefaultLang('en');
    this.translateService.use(this.locale);
  }

  onClickGo = () => {

    switch (this.selectedService) {

      case 'x':
        this.router.navigate(['x'], { relativeTo: this.activatedRoute, queryParamsHandling: 'preserve' });
        break;

      case 'y':
        this.router.navigate(['y'], { relativeTo: this.activatedRoute, queryParamsHandling: 'preserve' });
        break;
      default:
        break;

    }
  };

}
