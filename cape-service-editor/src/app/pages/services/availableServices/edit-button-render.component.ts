import { Component, Input, OnInit } from '@angular/core';
import { ViewCell } from 'ng2-smart-table';
import { Router } from '@angular/router';


@Component({
 template: `
    
	<button type="button" class="btn btn-primary btn-with-icon" (click)="EditService()"><i class="ion-link"></i>Edit</button>
  `,
})
export class EditButtonRenderComponent implements OnInit {

  public renderValue;

  @Input() value;

  constructor(private router: Router) {  }

  ngOnInit() {
    this.renderValue = this.value;
  }

  EditService() {
    console.log(this.renderValue.viewInfo);

    
         
    this.router.navigate(['/pages/tables/editor', {"serviceId":this.renderValue.viewInfo.serviceId}]);  
  }	
	

 


}