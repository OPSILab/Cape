import { Component, OnDestroy, OnInit, Input } from '@angular/core';
import { NbThemeService } from '@nebular/theme';

interface AuditDataMapping {
  name: string,
  count: string
};

@Component({
  selector: 'personal-data-storage-bar',
  template: `
    <ngx-charts-bar-vertical-stacked
      [scheme]="colorScheme"
      [results]="barData"
      [xAxis]="showXAxis"
      [yAxis]="showYAxis"
      [legend]="showLegend">
    </ngx-charts-bar-vertical-stacked>
  `,
})
export class PersonalDataStorageBar implements OnInit, OnDestroy {

  showLegend = true;
  showXAxis = true;
  showYAxis = true;
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

    this.barData = Object.entries(this.inputData).map((storageLocation) => {


      return {
        name: storageLocation[0],
        series: Object.entries(storageLocation[1]).map((entry: [string, AuditDataMapping]) => {
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
