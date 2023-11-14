/* eslint-disable @typescript-eslint/no-unsafe-call */
/* eslint-disable @typescript-eslint/unbound-method */
import { Component, OnInit, TemplateRef, ViewChild, ChangeDetectorRef } from '@angular/core';
import { AccountService } from './account.service';
import {
  NbDialogService,
  NbWindowService,
  NbDialogRef,
  NbToastrService,
  NbComponentStatus,
  NbToastrConfig,
  NbGlobalPhysicalPosition,
  NbWindowRef,
} from '@nebular/theme';
import { Validators, FormControl, FormGroup, ValidationErrors, FormArray, AbstractControl } from '@angular/forms';
import { FormBuilder } from '@angular/forms';
import { TranslateService } from '@ngx-translate/core';
import { Account, AccountNotificationEnum } from '../../model/account/account.model';
import { LoginService } from '../../auth/login/login.service';
import { OidcUserInformationService } from '../../auth/services/oidc-user-information.service';
import { UserClaims } from '../../auth/model/oidc';
import { NgxConfigureService } from 'ngx-configure';
import { AppConfig } from '../../model/appConfig';


@Component({
  selector: 'account-management',
  templateUrl: './account.component.html',
  styleUrls: ['./account.component.scss'],
})
export class AccountComponent implements OnInit {
  accountId: string;
  account: Account;
  reportTitle: string;
  userPictureOnly = false;
  user: UserClaims;
  private config: AppConfig;
  windowRef: NbWindowRef;
  docsUrl: string;
  

  @ViewChild('reportProblemWindowTemplate', { static: false })
  reportProblemWindowTemplate: TemplateRef<unknown>;
  languages = [
    { key: 'IT', value: 'Italiano' },
    { key: 'EN', value: 'English' },
  ];

  private accountNotificationsEnum = AccountNotificationEnum;
  public accountNotificationsKeys: string[];
  

  constructor(
    private accountService: AccountService,
    private dialogService: NbDialogService,
    private formBuilder: FormBuilder,
    private windowService: NbWindowService,
    private translateService: TranslateService,
    private toastrService: NbToastrService,
    private userService: OidcUserInformationService,
    private configService: NgxConfigureService,
  ) {
    this.accountNotificationsKeys = Object.keys(this.accountNotificationsEnum);
    this.reportTitle = this.translateService.instant('general.account.report_the_problem') as string;
    this.config = this.configService.config as AppConfig;
    
    this.docsUrl=this.config.system.docsUrl;
  }

  
  passwordForm = this.formBuilder.group(
    {
      newPassword: new FormControl('', [
        Validators.required,
        Validators.pattern('^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])[a-zA-Z0-9]+$'),
        Validators.minLength(6),
      ]),
      confirmPassword: new FormControl('', [Validators.required]),
    },
    {
      validators: (control: FormGroup): ValidationErrors | null => {
        const newPassword = control.get('newPassword');
        const confirmPassword = control.get('confirmPassword');

        return newPassword.value !== confirmPassword.value ? { passwordMismatch: true } : null;
      },
    }
  );

  configurationForm = this.formBuilder.group({
    language: new FormControl(''),
    notification: this.formBuilder.array(Object.entries(this.accountNotificationsEnum).map(() => new FormControl(false))),
  });

  reportProblemForm = this.formBuilder.group({
    reportProblemText: [''],
    reportTopicText:['']
  });

  
  get newPassword(): AbstractControl {
    return this.passwordForm.get('newPassword');
  }
  get confirmPassword(): AbstractControl {
    return this.passwordForm.get('confirmPassword');
  }
  get notificationFormArray(): AbstractControl {
    return this.configurationForm.controls.notification as FormArray;
  }

  async ngOnInit(): Promise<void> {
    
    this.user=this.userService.user;

    
  }

  openReportProblem = (): void => {
    this.windowRef =this.windowService.open(this.reportProblemWindowTemplate, {
      title: this.reportTitle,
      context: { text: 'some text to pass into template' },
    });

   
  };

  onSubmitReportProblem = (): void => {
          
    var mailText = "mailto:"+this.config.system.mailTo+"?subject=[Service Catalogue - Report problem]"+this.reportProblemForm.get('reportTopicText').value+"&body="+this.reportProblemForm.get('reportProblemText').value; 
    console.log(mailText);
    this.windowRef.close();
    window.location.href = mailText;
   
  };

  openChangePassword = (changePasswordTemplate: TemplateRef<unknown>): void => {
    this.dialogService.open(changePasswordTemplate, {
      hasScroll: false,
    });
  };

  onSubmitPassword = (dialogRef: NbDialogRef<unknown>): void => {
    this.accountService.savePassword(this.accountId, this.passwordForm.get('newPassword').value);
    dialogRef.close();
  };

    



  

  

 

  private showToast(type: NbComponentStatus, title: string, body: string) {
    const config = {
      status: type,
      destroyByClick: true,
      duration: 2500,
      hasIcon: true,
      position: NbGlobalPhysicalPosition.BOTTOM_RIGHT,
      preventDuplicates: false,
    } as Partial<NbToastrConfig>;

    this.toastrService.show(body, title, config);
  }
}
