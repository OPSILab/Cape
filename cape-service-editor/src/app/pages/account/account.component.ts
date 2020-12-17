import { Component, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { AccountService } from './account.service';
import { NbDialogService, NbWindowService } from '@nebular/theme';
import { Validators, FormControl } from '@angular/forms';
import { FormBuilder } from '@angular/forms';
import { TranslateService } from '@ngx-translate/core';
import { NgxConfigureService } from 'ngx-configure';

@Component({
  selector: 'account',
  templateUrl: './account.component.html',
  styleUrls: ['./account.component.scss']
})
export class AccountComponent implements OnInit {

  constructor(private service: AccountService, private dialogService: NbDialogService,
    private fb: FormBuilder, private windowService: NbWindowService, private translate: TranslateService,
    private configService: NgxConfigureService) {
    this.translate.setDefaultLang('en');
    this.translate.use(this.configService.config.i18n.locale);
    this.translate.get('general.account.report_the_problem').subscribe((text: string) => this.reportTitle = text);
  }

  reportTitle: string;

  languages = [
    { key: 1, value: "Italian" },
    { key: 2, value: "English" },
    { key: 3, value: "Russian" }
  ];

  notifications = ["Dashboard", "Email", "Phone"];

  firstName = new FormControl('', [
    Validators.required,
    Validators.minLength(2)]);

  lastName = new FormControl('', [
    Validators.required,
    Validators.minLength(3)]);

  email = new FormControl('', [
    Validators.required,
    Validators.pattern("^[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,4}$")]);

  phone = new FormControl('', [
    Validators.required,
    Validators.pattern("^[(]{0,1}[0-9]{3}[)]{0,1}[-\s\.]{0,1}[0-9]{3}[-\s\.]{0,1}[0-9]{4}$")]);

  generalInformationForm = this.fb.group({
    firstName: this.firstName,
    lastName: this.lastName,
    email: this.email,
    phone: this.phone
  });

  newPassword = new FormControl('', [
    Validators.required,
    Validators.pattern('^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])[a-zA-Z0-9]+$'),
    Validators.minLength(6)]);

  confirmPassword = new FormControl('', [
    Validators.required,
  ]);

  passwordForm = this.fb.group({
    newPassword: this.newPassword,
    confirmPassword: this.confirmPassword
  });

  configurationForm = this.fb.group({
    language: ['', Validators.required],
    notification: this.fb.array(this.notifications.map(item => !1), Validators.required)
  });

  reportProblemForm = this.fb.group({
    reportProblemText: ['']
  });

  @ViewChild('contentTemplate', { static: false }) contentTemplate: TemplateRef<any>;

  openReportProblem() {
    this.windowService.open(
      this.contentTemplate,
      { title: this.reportTitle, context: { text: 'some text to pass into template' } },
    );
  }

  openDeleteAccount(accountDeleted) {
    this.dialogService.open(accountDeleted, {
      hasScroll: false
    });;

  }
  openDownloadInformation() {
    console.log("Information downloaded.");
    this.service.downloadInformation();
  }

  openRegenerateKey(regeneratedKey) {
    this.dialogService.open(regeneratedKey, {
      hasScroll: false
    });;
  }


  onSubmitGeneralInformation() {
    console.log(this.generalInformationForm.value);
    this.service.saveGeneralInformation(this.generalInformationForm.value.firstName, this.generalInformationForm.value.lastName, this.generalInformationForm.value.email, this.generalInformationForm.value.phone);
  }

  onSubmitPassword() {
    console.log(this.passwordForm.value);
    this.service.savePassword(this.passwordForm.value.newPassword);
  }

  onSubmitConfiguration() {
    const form = this.configurationForm.value;
    form.notification = this.notifications.filter((item, i) => form.notification[i]);
    console.log(this.configurationForm.value);
    this.service.saveConfiguration(form.language, form.notification);
  }

  onSubmitReportProblem() {
    console.log(this.reportProblemForm.value);
    this.service.saveReportProblem(this.reportProblemForm.value.reportProblemText);
  }

  passwordCheck(newPassword: FormControl, confirmPassword: FormControl) {
    if (newPassword.value === confirmPassword.value) {
      return true;
    }
    return false;
  }

  regenerateKey() {
    console.log("New key: 123");
  }

  deleteAccount() {
    this.service.deleteAccount();
  }

  ngOnInit() {

    var account = this.service.getAccount();

    this.generalInformationForm.patchValue({
      firstName: account.account_info.firstname,
      lastName: account.account_info.lastname,
      email: account.account_info.email,
      phone: account.account_info.phone
    }),

      this.configurationForm.patchValue({
        language: account.language,
        notification: account.notification
      })

  }

}
