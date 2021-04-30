import { Injectable } from '@angular/core';
import { Account, AccountInfo } from '../../model/account/account.model';
import { NgxConfigureService } from 'ngx-configure';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AppConfig } from '../../model/appConfig';

@Injectable()
export class AccountService {
  private accountUrl: string;
  private config: AppConfig;

  constructor(configService: NgxConfigureService, private http: HttpClient) {
    this.config = configService.config as AppConfig;
    this.accountUrl = this.config.system.accountUrl;
  }

  createAccount(account: Account): Promise<Account> {
    return this.http.post<Account>(`${this.accountUrl}/api/v2/accounts`, account).toPromise();
  }

  getAccount(accountId: string): Promise<Account> {
    return this.http.get<Account>(`${this.accountUrl}/api/v2/accounts/${accountId}`).toPromise();
  }

  updateAccountInfo(accountId: string, accountInfo: AccountInfo): Promise<AccountInfo> {
    return this.http.put<AccountInfo>(`${this.accountUrl}/api/v2/accounts/${accountId}/info`, accountInfo).toPromise();
  }

  deleteAccount(accountId: string): Promise<Account> {
    return this.http.delete<Account>(`${this.accountUrl}/api/v2/accounts/${accountId}`).toPromise();
  }

  savePassword(accountId: string, password: string): void {
    // TODO replace with real http request
    console.log('Password:' + password);
    //
  }

  saveConfiguration(account: Account): Promise<Account> {
    return this.http.put<Account>(`${this.accountUrl}/api/v2/accounts/${account.username}`, account).toPromise();
  }

  saveReportProblem(accountId: string, reportProblemText: string): void {
    // TODO replace with real http request
    console.log('Reported problem:' + reportProblemText);
    //
  }

  downloadAccountExport(accountId: string): Observable<Blob> {
    return this.http.get(`${this.accountUrl}/api/v2/accounts/${accountId}/export/download`, { responseType: 'blob' });
  }

  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  regenerateKey(account: Account): void {
    // TODO replace with real http request
    console.log('Key was correctly regenerated.');
    //
  }
}
