import { Component, OnDestroy, Input, OnInit } from '@angular/core';
import { NbJSThemeVariable, NbThemeService } from '@nebular/theme';
import { map, takeUntil } from 'rxjs/operators';
import { Subject } from 'rxjs';
import { AuditDataMapping } from '../../../model/auditlogs/auditlogs.model';

@Component({
  selector: 'processed-data-pie',
  template: ` <ngx-charts-advanced-pie-chart [scheme]="colorScheme" [results]="pieData"> </ngx-charts-advanced-pie-chart> `,
})
export class ProcessedDataPieComponent implements OnInit, OnDestroy {
  @Input()
  private inputData: Record<string, AuditDataMapping>;

  pieData: { name: string; value: number }[];
  colorScheme: { domain: (string | string[] | NbJSThemeVariable)[] };
  private unsubscribe: Subject<void> = new Subject();

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

    this.pieData = Object.values(this.inputData).map((dataMapping) => {
      return {
        name: dataMapping.name,
        value: dataMapping.count,
      };
    });
  }

  ngOnDestroy(): void {
    this.unsubscribe.next();
    this.unsubscribe.complete();
  }
}
