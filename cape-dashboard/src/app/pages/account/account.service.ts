import { Injectable } from '@angular/core';
import { Account, AccountInfo } from '../../model/account/account.model';
import { NgxConfigureService } from 'ngx-configure';
import { HttpClient } from '@angular/common/http';
import { AccountExport } from '../../model/account/accountExport';
import { Observable } from 'rxjs';

@Injectable()
export class AccountService {

  private accountUrl; string;
  private config: any;

  constructor(configService: NgxConfigureService, private http: HttpClient) {

    this.config = configService.config;
    this.accountUrl = this.config.system.accountUrl;
  }


  getAccount(accountId: string): Promise<Account> {

    return this.http
      .get<Account>(`${this.accountUrl}/accounts/${accountId}`).toPromise();
  }


  updateAccountInfo(accountId: string, accountInfo: AccountInfo): Promise<AccountInfo> {

    return this.http
      .put<AccountInfo>(`${this.accountUrl}/accounts/${accountId}/info`, accountInfo).toPromise();
  }


  deleteAccount(accountId: string): Promise<Account> {

    return this.http
      .delete<Account>(`${this.accountUrl}/accounts/${accountId}`).toPromise();
  }

  savePassword(accountId: string, password: string) {

    // TODO replace with real http request
    console.log('Password:' + password);
    //
  }


  saveConfiguration(account: Account) {

    return this.http
      .put<Account>(`${this.accountUrl}/accounts/${account.username}`, account).toPromise();
  }


  saveReportProblem(accountId: string, reportProblemText: string) {

    // TODO replace with real http request
    console.log('Reported problem:' + reportProblemText);
    //
  }


  downloadAccountExport(accountId: string): Observable<Blob> {

    return this.http
      .get(`${this.accountUrl}/accounts/${accountId}/export/download`, { responseType: 'blob' });
  }


  regenerateKey(account: Account) {

    // TODO replace with real http request
    console.log('Key was correctly regenerated.');
    //
  }
}
