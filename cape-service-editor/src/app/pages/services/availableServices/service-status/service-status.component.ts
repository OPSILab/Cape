import { Component, Input, OnInit } from '@angular/core';

@Component({
  template: `
    <div *ngIf="active; else notActive">
      <button nbButton ghost shape="rectangle" size="small" status="success">
        <i class="material-icons">check_circle</i>
      </button>
    </div>

    <ng-template #notActive>
      <button nbButton ghost nbTooltip="Under Development" shape="rectangle" size="small" status="warning">
        <i class="material-icons">check_circle</i>
      </button>
    </ng-template>
  `,
  styleUrls: ['../availableServices.component.scss'],
})
export class ServiceStatusComponent implements OnInit {
  active: boolean = false;

  @Input() value: string;

  ngOnInit() {
    this.active = this.value.toLowerCase() === 'completed';
  }
}
