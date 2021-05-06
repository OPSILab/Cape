import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-weight',
  templateUrl: './weight.component.html',
  styleUrls: ['./weight.component.scss'],
})
export class WeightComponent implements OnInit {
  accountId: string;

  constructor() {}

  ngOnInit() {
    this.accountId = localStorage.getItem('accountId');
  }
}
