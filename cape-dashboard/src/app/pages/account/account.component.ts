import { Component, OnInit, TemplateRef, ViewChild, ChangeDetectorRef } from '@angular/core';
import { AccountService } from './account.service';
import { NbDialogService, NbWindowService, NbDialogRef, NbToastrModule, NbToastrService, NbGlobalLogicalPosition } from '@nebular/theme';
import { Validators, FormControl, ValidatorFn, FormGroup, ValidationErrors, FormArray } from '@angular/forms';
import { FormBuilder } from '@angular/forms';
import { TranslateService } from '@ngx-translate/core';
import { NgxConfigureService } from 'ngx-configure';
import { Account, AccountInfo, AccountNotificationEnum } from '../../model/account/account.model';
import { LoginService } from '../../login/login.service';
import { ErrorDialogService } from '../error-dialog/error-dialog.service';
import { Router } from '@angular/router';
import { saveAs as importedSaveAs } from "file-saver";

@Component({
  selector: 'account-management',
  templateUrl: './account.component.html',
  styleUrls: ['./account.component.scss']
})
export class AccountComponent implements OnInit {

  accountId: string;
  account: Account;
  reportTitle: string;

  languages = [
    { key: 'IT', value: 'Italian' },
    { key: 'EN', value: 'English' }
  ];

  private accountNotificationsEnum = AccountNotificationEnum;
  public accountNotificationsKeys: string[];

  generalInformationForm = this.fb.group({
    firstName: new FormControl('', [
      Validators.required,
      Validators.minLength(2)]),
    lastName: new FormControl('', [
      Validators.required,
      Validators.minLength(3)]),
    email: new FormControl('', [
      Validators.required,
      Validators.pattern('^[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,4}$')]),
    phone: new FormControl('', [
      Validators.required,
      Validators.pattern('^[(]{0,1}[0-9]{3}[)]{0,1}[-\s\.]{0,1}[0-9]{3}[-\s\.]{0,1}[0-9]{4}$')])
  });

  changePasswordValidator: ValidatorFn = (control: FormGroup): ValidationErrors | null => {
    const newPassword = control.get('newPassword');
    const confirmPassword = control.get('confirmPassword');

    return newPassword.value !== confirmPassword.value ? { 'passwordMismatch': true } : null;
  }

  passwordForm = this.fb.group({
    newPassword: new FormControl('', [
      Validators.required,
      Validators.pattern('^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])[a-zA-Z0-9]+$'),
      Validators.minLength(6)]),
    confirmPassword: new FormControl('', [
      Validators.required
    ])
  }, {
    validators: this.changePasswordValidator
  });

  configurationForm = this.fb.group({
    language: new FormControl(''),
    notification: this.fb.array(Object.entries(this.accountNotificationsEnum).map(e => new FormControl(false)))
  });

  reportProblemForm = this.fb.group({
    reportProblemText: ['']
  });

  @ViewChild('reportProblemWindowTemplate', { static: false }) reportProblemWindowTemplate: TemplateRef<any>;

  get firstName() { return this.generalInformationForm.get('firstName'); }
  get lastName() { return this.generalInformationForm.get('lastName'); }
  get email() { return this.generalInformationForm.get('email'); }
  get phone() { return this.generalInformationForm.get('phone'); }
  get newPassword() { return this.passwordForm.get('newPassword'); }
  get confirmPassword() { return this.passwordForm.get('confirmPassword'); }
  get notificationFormArray() { return this.configurationForm.controls.notification as FormArray; }

  constructor(private accountService: AccountService, private dialogService: NbDialogService,
    private fb: FormBuilder, private windowService: NbWindowService, private translateService: TranslateService, private router: Router,
    private configService: NgxConfigureService, private toastrService: NbToastrService,
    private loginService: LoginService, private errorDialogService: ErrorDialogService, private cdr: ChangeDetectorRef) {

    this.accountNotificationsKeys = Object.keys(this.accountNotificationsEnum);
    this.reportTitle = this.translateService.instant('general.account.report_the_problem');


  }

  async ngOnInit() {

    this.accountId = localStorage.getItem('accountId');
    this.account = await this.accountService.getAccount(this.accountId);

    this.generalInformationForm.patchValue({
      firstName: this.account.account_info.firstname,
      lastName: this.account.account_info.lastname,
      email: this.account.account_info.email,
      phone: this.account.account_info.phone
    });

    this.configurationForm.patchValue({
      language: this.languages.find(lang => lang.key === this.account.language.toUpperCase())?.value,
      notification: this.accountNotificationsKeys.map(key =>
        this.account.notification.filter(value => value.toString() === key).length > 0)
    });

    this.generalInformationForm.markAllAsTouched();

  }

  openReportProblem = () => {

    this.windowService.open(
      this.reportProblemWindowTemplate,
      { title: this.reportTitle, context: { text: 'some text to pass into template' } },
    );
  }


  onSubmitReportProblem = () => {

    console.log(this.reportProblemForm.value);
    this.accountService.saveReportProblem(this.accountId, this.reportProblemForm.value.reportProblemText);
  }


  openChangePassword = (changePasswordTemplate) => {

    this.dialogService.open(changePasswordTemplate, {
      hasScroll: false
    });
  }


  onSubmitPassword = (dialogRef: NbDialogRef<unknown>) => {

    this.accountService.savePassword(this.accountId, this.passwordForm.value.newPassword);
    dialogRef.close();
  }


  openDeleteAccount = (accountDeleteTemplate: TemplateRef<unknown>) => {

    this.dialogService.open(accountDeleteTemplate, {
      hasScroll: false
    });
  }


  onDeleteAccount = async (dialogRef: NbDialogRef<unknown>) => {

    try {
      await this.accountService.deleteAccount(this.accountId);
      dialogRef.close();
      this.loginService.logout();
      this.router.navigate(['/login']);
    } catch (error) {
      if (error.error?.statusCode === '401') {
        this.loginService.logout();
        this.router.navigate(['/login']);
      } else
        this.errorDialogService.openErrorDialog(error);
    }
  }


  openDownloadAccountExport = () => {

    this.accountService.downloadAccountExport(this.accountId).subscribe(
      res => {
        importedSaveAs(res, 'CapeAccountExport.json');
      },
      error => {
        if (error.error?.statusCode === '401') {
          this.loginService.logout();
          this.router.navigate(['/login']);
        } else
          this.errorDialogService.openErrorDialog(error);
      });
  }


  openRegenerateKey = (regenerateKeyTemplate: TemplateRef<unknown>) => {

    this.dialogService.open(regenerateKeyTemplate, {
      hasScroll: false
    });
  }


  onRegenerateKey = (dialogRef: NbDialogRef<unknown>) => {

    this.accountService.regenerateKey(this.account);
    dialogRef.close();
  }


  onSubmitGeneralInformation = async () => {

    try {
      await this.accountService.updateAccountInfo(this.accountId, {
        firstname: this.generalInformationForm.value.firstName,
        lastname: this.generalInformationForm.value.lastName,
        email: this.generalInformationForm.value.email,
        phone: this.generalInformationForm.value.phone
      });
      this.toastrService.primary('', this.translateService.instant('general.account.account_updated_message'),
        { position: NbGlobalLogicalPosition.BOTTOM_END, duration: 3500 });

      this.generalInformationForm.markAsPristine();

    } catch (error) {
      if (error.error?.statusCode === '401') {
        this.loginService.logout();
        this.router.navigate(['/login']);
      } else
        this.errorDialogService.openErrorDialog(error);
    }

  }


  onSubmitConfiguration = async () => {

    try {
      const selectedOrderIds = this.configurationForm.get('notification').value
        .map((checked, i) => checked ? this.accountNotificationsKeys[i] : null)
        .filter(v => v !== null);
      this.account.notification = selectedOrderIds;
      this.account.language = this.languages.filter(language => language.value === this.configurationForm.get('language').value)[0]?.key;
      localStorage.setItem('currentLocale', this.account.language?.toLowerCase());
      this.translateService.use(this.account.language?.toLowerCase());
      this.cdr.detectChanges();

      await this.accountService.saveConfiguration(this.account);

      this.toastrService.primary('', this.translateService.instant('general.account.account_updated_message'),
        { position: NbGlobalLogicalPosition.BOTTOM_END, duration: 3500 });
      this.configurationForm.markAsPristine();

    } catch (error) {
      if (error.error?.statusCode === '401') {
        this.loginService.logout();
        this.router.navigate(['/login']);
      } else
        this.errorDialogService.openErrorDialog(error);
    }
  }


}
