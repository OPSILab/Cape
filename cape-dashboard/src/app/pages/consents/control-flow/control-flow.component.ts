import { Component, OnInit } from '@angular/core';

import { DataService } from '../../../services/data.service';
import { NbIconLibraries } from '@nebular/theme';

@Component({
  selector: 'ngx-control-flow',
  templateUrl: './control-flow.component.html',
  styleUrls: ['./control-flow.component.scss']
})
export class ControlFlowComponent implements OnInit {
  data: any;

  constructor(
    private dataService: DataService,
    private iconsLibrary: NbIconLibraries
  ) {
    this.iconsLibrary.registerFontPack('fa', { packClass: 'fa', iconClassPrefix: 'fa' });
  }

  initData() {
    this.dataService.getGraphData()
      .subscribe(res => {
        this.data = res;
      }, err => {
        console.log(err);
      })
  }

 
  ngOnInit() {
    this.initData();
  }

}
