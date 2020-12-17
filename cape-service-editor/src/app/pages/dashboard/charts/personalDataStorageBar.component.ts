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
      [legend]="showLegend"
      [xAxisLabel]="xAxisLabel"
      [yAxisLabel]="yAxisLabel">
    </ngx-charts-bar-vertical-stacked>
  `,
})
export class personalDataStorageBar implements OnInit, OnDestroy {

  results = [
    {
      "name": "Countries in the European Economic Area",
      "series": [
        {
          "name": "Glucose",
          "value": 50
        },
        {
          "name": "Age",
          "value": 50
        }
      ]
    },
    {
      "name": "the European Union",
      "series": [
        {
          "name": "Blood measurement",
          "value": 50
        },
        {
          "name": "Glucose",
          "value": 30
        },
        {
          "name": "Age",
          "value": 20
        }
      ]
    },
    {
      "name": "The controller servers",
      "series": [
        {
          "name": "Blood measurement",
          "value": 20
        },
        {
          "name": "Glucose",
          "value": 48
        },
        {
          "name": "Age",
          "value": 32
        }
      ]
    }
    ,
    {
      "name": "third Countries",
      "series": [
        {
          "name": "Blood measurement",
          "value": 50
        },
        {
          "name": "Glucose",
          "value": 30
        },
        {
          "name": "Age",
          "value": 20
        }
      ]
    }

  ];
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

    this.barData = Object.entries(this.inputData).map((storageLocation) => {


      return {
        name: storageLocation[0],
        series: Object.entries(storageLocation[1]).map((entry: [string, AuditDataMapping]) => {
          return {
            name: entry[1].name,
            value: entry[1].count
          }
        })
      };
    });


  }
  ngOnDestroy(): void {
    this.themeSubscription.unsubscribe();
  }
}
