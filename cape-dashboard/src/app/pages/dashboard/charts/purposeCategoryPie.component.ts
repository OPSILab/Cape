import { Component, OnDestroy, Input, OnInit } from '@angular/core';
import { NbJSThemeVariable, NbThemeService } from '@nebular/theme';
import { Subject } from 'rxjs';
import { map, takeUntil } from 'rxjs/operators';

@Component({
  selector: 'purpose-category-pie',
  template: ` <ngx-charts-pie-chart [scheme]="colorScheme" [results]="pieData" [legend]="showLegend" [labels]="showLabels"> </ngx-charts-pie-chart> `,
})
export class PurposeCategoryPieComponent implements OnInit, OnDestroy {
  @Input()
  inputData: Record<string, number>;

  showLegend = true;
  showLabels = true;
  colorScheme: { domain: (string | string[] | NbJSThemeVariable)[] };
  private unsubscribe: Subject<void> = new Subject();

  pieData: { name: string; value: number }[];

  constructor(private theme: NbThemeService) {}

  ngOnInit(): void {
    this.theme
      .getJsTheme()
      .pipe(
        takeUntil(this.unsubscribe),
        map((config) => config.variables)
      )
      .subscribe((colors) => {
        this.colorScheme = {
          domain: [colors.primaryLight, colors.infoLight, colors.successLight, colors.warningLight, colors.dangerLight, '#dddddd'],
        };
      });
    this.pieData = Object.entries(this.inputData).map((entry) => {
      return {
        name: entry[0],
        value: entry[1],
      };
    });
  }

  ngOnDestroy(): void {
    this.unsubscribe.next();
    this.unsubscribe.complete();
  }
}
