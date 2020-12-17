import { Component, OnDestroy, Input, OnInit } from '@angular/core';
import { NbThemeService } from '@nebular/theme';

@Component({
  selector: 'purpose-category-pie',
  template: `
    <ngx-charts-pie-chart
      [results]="pieData"
      [legend]="showLegend"
      [labels]="showLabels">
    </ngx-charts-pie-chart>
  `,
})
export class PurposeCategoryPie implements OnInit, OnDestroy {

  showLegend = true;
  showLabels = true;
  colorScheme: any;
  themeSubscription: any;

  @Input()
  inputData: Object;

  pieData: any[];

  constructor(private theme: NbThemeService) {
    this.themeSubscription = this.theme.getJsTheme().subscribe(config => {
      const colors: any = config.variables;
      this.colorScheme = {
        domain: [colors.primaryLight, colors.infoLight, colors.successLight, colors.warningLight, colors.dangerLight],
      };
    });
  }

  ngOnInit(): void {

    this.pieData = Object.entries(this.inputData).map((entry) => {
      return {
        name: entry[0],
        value: entry[1]
      };
    });
  }
  ngOnDestroy(): void {
    this.themeSubscription.unsubscribe();
  }
}
