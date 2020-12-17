import { Component, OnDestroy, Input, OnInit } from '@angular/core';
import { NbThemeService } from '@nebular/theme';


interface AuditDataMapping {
  name: string;
  count: string;
}

@Component({
  selector: 'personal-data-processing-bar',
  template: `
    <ngx-charts-bar-horizontal-stacked
      [scheme]="colorScheme"
      [results]="barData"
      [xAxis]="showXAxis"
      [yAxis]="showYAxis"
      [legend]="showLegend"
      [xAxisLabel]="xAxisLabel"
      [yAxisLabel]="yAxisLabel">
    </ngx-charts-bar-horizontal-stacked>
  `
})
export class PersonalDataProcessingBar implements OnInit, OnDestroy {

  showLegend = true;
  showXAxis = true;
  showYAxis = true;
  xAxisLabel = 'Country';
  yAxisLabel = 'Population';
  colorScheme: any;
  themeSubscription: any;

  @Input()
  inputData: Object;

  barData: any[];

  constructor(private theme: NbThemeService) {
    this.themeSubscription = this.theme.getJsTheme().subscribe(config => {
      const colors: any = config.variables;
      this.colorScheme = {
        domain: [colors.primaryLight, colors.infoLight, colors.successLight, colors.warningLight, colors.dangerLight],
      };
    });
  }


  ngOnInit(): void {

    this.barData = Object.entries(this.inputData).map((processingCategory) => {

      return {
        name: processingCategory[0],
        series: Object.entries(processingCategory[1]).map((entry: [string, AuditDataMapping]) => {
          return {
            name: entry[1].name,
            value: entry[1].count
          };
        })
      };
    });
  }

  ngOnDestroy(): void {

    this.themeSubscription.unsubscribe();
  }
}
