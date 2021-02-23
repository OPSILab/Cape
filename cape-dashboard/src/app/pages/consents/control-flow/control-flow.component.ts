import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'ngx-control-flow',
  templateUrl: './control-flow.component.html',
  styleUrls: ['./control-flow.component.scss'],
})
export class ControlFlowComponent implements OnInit {
  data: unknown;

  constructor(private http: HttpClient) {}

  async ngOnInit(): Promise<void> {
    this.data = await this.http.get(`assets/data/cape_dataflow.json`).toPromise();
  }
}
