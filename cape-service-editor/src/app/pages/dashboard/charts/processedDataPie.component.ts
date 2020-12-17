import { Component, OnDestroy, Input, AfterViewChecked, OnInit, ViewEncapsulation } from '@angular/core';
import { NbThemeService } from '@nebular/theme';
import { takeUntil } from 'rxjs/operators';
import { Subject } from 'rxjs';

interface AuditDataMapping {
  name: string,
  count: string
};

@Component({
  selector: 'processed-data-pie',
  template: `
    <ngx-charts-advanced-pie-chart
      [scheme]="colorScheme"
      [results]="pieData"
  >
    </ngx-charts-advanced-pie-chart>
  `
})
export class processedDataPie implements OnInit, OnDestroy {

  @Input()
  inputData: Object;

  pieData: any[];
  colorScheme: any;
  themeSubscription: any;
  private unsubscribe: Subject<void> = new Subject();

  constructor(private theme: NbThemeService) {
    this.themeSubscription = this.theme.getJsTheme().pipe(takeUntil(this.unsubscribe)).subscribe(config => {
      const colors: any = config.variables;
      this.colorScheme = {
        domain: [colors.primaryLight, colors.infoLight, colors.successLight, colors.warningLight, colors.dangerLight],
      };
    });
  }

  ngOnInit(): void {

    this.pieData = Object.entries(this.inputData).map((entry: [string, AuditDataMapping]) => {
        return {
          name: entry[1].name,
          value: entry[1].count
        };
      });


  }

  ngOnDestroy(): void {
    this.themeSubscription.unsubscribe();
  }

}
