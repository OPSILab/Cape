import { Component, OnDestroy, OnInit, Input } from '@angular/core';
import { NbJSThemeVariable, NbThemeService } from '@nebular/theme';
import { Subject } from 'rxjs';
import { map, takeUntil } from 'rxjs/operators';
import { AuditDataMapping } from '../../../model/auditlogs/auditlogs.model';

@Component({
  selector: 'personal-data-storage-bar',
  template: `
    <ngx-charts-bar-vertical-stacked [scheme]="colorScheme" [results]="barData" [xAxis]="showXAxis" [yAxis]="showYAxis" [legend]="showLegend">
    </ngx-charts-bar-vertical-stacked>
  `,
})
export class PersonalDataStorageBarComponent implements OnInit, OnDestroy {
  @Input()
  private inputData: Record<string, Record<string, AuditDataMapping>>;

  showLegend = true;
  showXAxis = true;
  showYAxis = true;
  colorScheme: { domain: (string | string[] | NbJSThemeVariable)[] };
  private unsubscribe: Subject<void> = new Subject();

  barData: { name: string; series: { name: string; value: number }[] }[];

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

    this.barData = Object.entries(this.inputData).map((storageLocation) => {
      return {
        name: storageLocation[0],
        series: Object.entries(storageLocation[1]).map((entry) => {
          return {
            name: entry[1].name,
            value: entry[1].count,
          };
        }),
      };
    });
  }

  ngOnDestroy(): void {
    this.unsubscribe.next();
    this.unsubscribe.complete();
  }
}
