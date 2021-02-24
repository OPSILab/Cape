import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute, NavigationExtras } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { NgxConfigureService } from 'ngx-configure';

@Component({
  selector: 'app-services',
  templateUrl: './services.component.html',
  styleUrls: ['./services.component.scss']
})
export class ServicesComponent implements OnInit {

  accountId: string;
  config: any;
  locale: string;

  selectedService: string;

  constructor (private configService: NgxConfigureService, private router: Router, private activatedRoute: ActivatedRoute,
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

    case 'MyWellness':
      this.router.navigate(['my-wellness'], { relativeTo: this.activatedRoute, queryParamsHandling: 'preserve' });
      break;

    case 'MyMeasurements':
      this.router.navigate(['my-measurements'], { relativeTo: this.activatedRoute, queryParamsHandling: 'preserve' });
      break;
    default:
      break;

  }
};


}
