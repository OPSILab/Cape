import { Component, OnInit } from '@angular/core';
import { NgxConfigureService } from 'ngx-configure';
import { Router, ActivatedRoute } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-mymeasurements',
  templateUrl: './mymeasurements.component.html',
  styleUrls: ['./mymeasurements.component.scss'],
})
export class MyMeasurementsComponent implements OnInit {
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
    this.accountId = localStorage.getItem('accountId');
    const queryParams = this.activatedRoute.snapshot.queryParams;
    this.locale = queryParams.locale || this.config.i18n.locale; // TODO default value taken from User language preferences;
    this.translateService.setDefaultLang('en');
    this.translateService.use(this.locale);
  }

  onClickGo = () => {
    switch (this.selectedService) {
      case 'cholesterol':
        this.router.navigate(['cholesterol'], { relativeTo: this.activatedRoute, queryParamsHandling: 'preserve' });
        break;

      case 'weight':
        this.router.navigate(['weight'], { relativeTo: this.activatedRoute, queryParamsHandling: 'preserve' });
        break;
      default:
        break;
    }
  };
}
