import { Component } from '@angular/core';
import { LocalDataSource } from 'ng2-smart-table';
import { ConsentInfoLinkRenderComponent } from './consentinfo-link-render.component';
import { ConsentsService } from './consents.service';
import { Router, ActivatedRoute } from '@angular/router';
import { ErrorDialogService } from '../../error-dialog/error-dialog.service';
import { TranslateService } from '@ngx-translate/core';
import { NgxConfigureService } from 'ngx-configure';



@Component({
  selector: 'consents-smart-table',
  templateUrl: './consents.component.html',
  styleUrls: [('./consents.component.scss')]
})
export class ConsentsComponent {

  private serviceLabel: string = 'Service';
  private sourceLabel: string = 'Source';
  private userLabel: string = 'User Id';
  private purposeLabel: string = 'Purpose';
  private issuedLabel: string = 'Issued';
  private statusLabel: string = 'Status';

  public settings: unknown;
  private locale: string;
  consents: any;
  


  source: LocalDataSource = new LocalDataSource();

  constructor(private service: ConsentsService, private router: Router, private route: ActivatedRoute, private translate: TranslateService, private configService: NgxConfigureService,
    private errorDialogService: ErrorDialogService,) {

      this.settings = this.loadTableSettings();   
   

    

  }

   async ngOnInit() {

    this.consents = await this.service.getConsents(); 
  
    
      this.locale = this.configService.config.i18n.locale;  // TODO change with user language preferences
      
      interface Sources {
        [index: number]: boolean;
    }

           

      this.source.load(this.consents.reduce(function (filtered,elem) {
       
        
        if (elem.payload.common_part.role==="Sink"){
               var date=new Date(elem.payload.common_part.iat).toLocaleString();
               filtered.push({ id: elem.payload.common_part.cr_id, serviceName: elem.payload.common_part.subject_name, serviceSource: elem.payload.common_part.source_name, userId: elem.payload.common_part.surrogate_id, purpose: elem.payload.role_specific_part.usage_rules.purposeName, issued: date, status: elem.payload.common_part.consent_status, viewInfo: elem, });
         } 
        return filtered
      },[]));
    
      
      // getTestConsents() for demo purposes
      /*this.consents= await this.service.getTestConsents();
      this.source.load(this.consents.map(function (elem) {
         return { id: elem.common_part.cr_id, serviceName: elem.servicename, serviceSource: elem.source, userId: elem.userId, purpose: elem.role_specific_part.usage_rules[0].purposeName, issued: elem.consentStatusList[0].iat, status: elem.consentStatusList[0].consent_status, viewInfo: elem, }
      }));*/
  }


  loadTableSettings() {

    this.serviceLabel = this.translate.instant('general.services.service');
    this.sourceLabel = this.translate.instant('general.consents.dataprovider');
    this.userLabel = this.translate.instant('general.consents.userid');
    this.purposeLabel = this.translate.instant('general.consents.purpose');
    this.issuedLabel = this.translate.instant('general.consents.issued');
    this.statusLabel = this.translate.instant('general.consents.status');


    return {
      mode: 'external',
      attr: {
        class: 'table table-bordered'
      },
      actions: {
        add: false,
        edit: false,
        delete: false
      },
      add: {
        addButtonContent: '<i class="nb-plus"></i>',
        createButtonContent: '<i class="nb-checkmark"></i>',
        cancelButtonContent: '<i class="nb-close"></i>',
      },
      edit: {
        editButtonContent: '<i class="nb-edit"></i>',
        saveButtonContent: '<i class="nb-checkmark"></i>',
        cancelButtonContent: '<i class="nb-close"></i>',
      },
      delete: {
        deleteButtonContent: '<i class="nb-trash"></i>',
        confirmDelete: true,
      },
      columns: {
  
        serviceName: {
          title: this.serviceLabel,
          type: 'text'
        },
        serviceSource: {
          title: this.sourceLabel,
          type: 'text'
        },
        userId: {
          title: this.userLabel,
          editor: {
            type: 'text',
          },
        },
        purpose: {
          title: this.purposeLabel,
          editor: {
            type: 'text',
          },
        },
        issued: {
          title: this.issuedLabel,
          editor: {
            type: 'text',
          },
        },
        status: {
          title: this.statusLabel,
          editor: {
            type: 'text',
          },
        },
        viewInfo: {
          filter: false,
          type: 'custom',
          valuePrepareFunction: (cell, row) => row,
          renderComponent: ConsentInfoLinkRenderComponent,
  
        },
  
      },
  
  
    };
  }


  
}
