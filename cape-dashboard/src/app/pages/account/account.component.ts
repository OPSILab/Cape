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
} from '@nebular/theme';
import { Validators, FormControl, FormGroup, ValidationErrors, FormArray, AbstractControl } from '@angular/forms';
import { FormBuilder } from '@angular/forms';
import { TranslateService } from '@ngx-translate/core';
import { Account, AccountNotificationEnum } from '../../model/account/account.model';
import { LoginService } from '../../login/login.service';
import { ErrorDialogService } from '../error-dialog/error-dialog.service';
import { saveAs as importedSaveAs } from 'file-saver';

@Component({
  selector: 'account-management',
  templateUrl: './account.component.html',
  styleUrls: ['./account.component.scss'],
})
export class AccountComponent implements OnInit {
  accountId: string;
  account: Account;
  reportTitle: string;

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
    private loginService: LoginService,
    private errorDialogService: ErrorDialogService,
    private cdr: ChangeDetectorRef
  ) {
    this.accountNotificationsKeys = Object.keys(this.accountNotificationsEnum);
    this.reportTitle = this.translateService.instant('general.account.report_the_problem') as string;
  }

  generalInformationForm = this.formBuilder.group({
    firstName: new FormControl('', [Validators.minLength(2)]),
    lastName: new FormControl('', [Validators.minLength(3)]),
    email: new FormControl('', [Validators.required, Validators.pattern('^[a-z0-9._%+-]+@[a-z0-9.-]+.[a-z]{2,4}$')]),
    phone: new FormControl('', [Validators.pattern('^[(]{0,1}[0-9]{3}[)]{0,1}[-s.]{0,1}[0-9]{3}[-s.]{0,1}[0-9]{4}$')]),
  });

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
  });

  get firstName(): AbstractControl {
    return this.generalInformationForm.get('firstName');
  }
  get lastName(): AbstractControl {
    return this.generalInformationForm.get('lastName');
  }
  get email(): AbstractControl {
    return this.generalInformationForm.get('email');
  }
  get phone(): AbstractControl {
    return this.generalInformationForm.get('phone');
  }
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
    this.accountId = localStorage.getItem('accountId');
    this.account = await this.accountService.getAccount(this.accountId);

    this.generalInformationForm.patchValue({
      firstName: this.account.account_info.firstname,
      lastName: this.account.account_info.lastname,
      email: this.account.account_info.email,
      phone: this.account.account_info.phone,
    });

    this.configurationForm.patchValue({
      language: this.languages.find((lang) => lang.key === this.account.language.toUpperCase())?.value,
      notification: this.accountNotificationsKeys.map((key) => this.account.notification.filter((value) => value.toString() === key).length > 0),
    });

    this.generalInformationForm.markAllAsTouched();
  }

  openReportProblem = (): void => {
    this.windowService.open(this.reportProblemWindowTemplate, {
      title: this.reportTitle,
      context: { text: 'some text to pass into template' },
    });
  };

  onSubmitReportProblem = (): void => {
    console.log(this.reportProblemForm.value);
    this.accountService.saveReportProblem(this.accountId, this.reportProblemForm.get('reportProblemText').value as string);
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

  openDeleteAccount = (accountDeleteTemplate: TemplateRef<unknown>): void => {
    this.dialogService.open(accountDeleteTemplate, {
      hasScroll: false,
    });
  };

  onDeleteAccount = async (dialogRef: NbDialogRef<unknown>): Promise<void> => {
    try {
      await this.accountService.deleteAccount(this.accountId);
      dialogRef.close();
      void this.loginService.logout();
      // this.router.navigate(['/login']);
    } catch (error) {
      // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
      if (error.error?.statusCode === '401') {
        this.loginService.logout().catch((error) => this.errorDialogService.openErrorDialog(error));
        // this.router.navigate(['/login']);
      } else this.errorDialogService.openErrorDialog(error);
    }
  };

  openDownloadAccountExport = (): void => {
    this.accountService.downloadAccountExport(this.accountId).subscribe(
      (res) => {
        // eslint-disable-next-line @typescript-eslint/no-unsafe-call
        importedSaveAs(res, 'CapeAccountExport.json');
      },
      (error) => {
        // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
        if (error.error?.statusCode === '401') {
          this.loginService.logout().catch((error) => this.errorDialogService.openErrorDialog(error));
          // this.router.navigate(['/login']);
        } else this.errorDialogService.openErrorDialog(error);
      }
    );
  };

  openRegenerateKey = (regenerateKeyTemplate: TemplateRef<unknown>): void => {
    this.dialogService.open(regenerateKeyTemplate, {
      hasScroll: false,
    });
  };

  onRegenerateKey = (dialogRef: NbDialogRef<unknown>): void => {
    this.accountService.regenerateKey(this.account);
    dialogRef.close();
  };

  onSubmitGeneralInformation = async (): Promise<void> => {
    try {
      await this.accountService.updateAccountInfo(this.accountId, {
        firstname: this.generalInformationForm.get('firstName').value as string,
        lastname: this.generalInformationForm.get('lastName').value as string,
        email: this.generalInformationForm.get('email').value as string,
        phone: this.generalInformationForm.get('phone').value as number,
      });

      this.showToast('primary', this.translateService.instant('general.account.account_updated_message'), '');

      this.generalInformationForm.markAsPristine();
    } catch (error) {
      // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
      if (error.error?.statusCode === '401') {
        this.loginService.logout().catch((error) => this.errorDialogService.openErrorDialog(error));
        // this.router.navigate(['/login']);
      } else this.errorDialogService.openErrorDialog(error);
    }
  };

  onSubmitConfiguration = async (): Promise<void> => {
    try {
      // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
      this.account.notification = this.configurationForm
        .get('notification')
        .value.map((checked: boolean, i: number) => (checked ? this.accountNotificationsKeys[i] : null))
        .filter((v) => v !== null) as AccountNotificationEnum[];

      this.account.language = this.languages.filter((language) => language.value === this.configurationForm.get('language').value)[0]?.key;
      localStorage.setItem('currentLocale', this.account.language?.toLowerCase());
      this.translateService.use(this.account.language?.toLowerCase());
      this.cdr.detectChanges();

      await this.accountService.saveConfiguration(this.account);

      this.showToast('primary', this.translateService.instant('general.account.account_updated_message'), '');

      this.configurationForm.markAsPristine();
    } catch (error) {
      // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
      if (error.error?.statusCode === '401') {
        this.loginService.logout().catch((error) => this.errorDialogService.openErrorDialog(error));
        // this.router.navigate(['/login']);
      } else this.errorDialogService.openErrorDialog(error);
    }
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
