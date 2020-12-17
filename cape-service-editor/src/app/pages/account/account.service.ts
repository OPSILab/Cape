import { Injectable } from '@angular/core';
import { Account, AccountInfo } from './account.model';

@Injectable()
export class AccountService {

    constructor() {
    }

    account: Account = {
        account_info: {
            firstname: "John",
            lastname: "Doe",
            email: "doe@gmail.com",
            phone: 2222222222
        },
        language: "English",
        notification: ["dashboard", "email"]
    };

    saveGeneralInformation(firstName: string, lastName: string, email: string, phone: number) {
        console.log("Name: " + firstName + ", " + "Last name: " + lastName + ", " + "Email: " + email + ", " + "Phone: " + phone);
        this.account.account_info.firstname = firstName;
        this.account.account_info.lastname = lastName;
        this.account.account_info.email = email;
        this.account.account_info.phone = phone;
        console.log(this.account);
    }

    savePassword(password: string) {
        console.log("Password:" + password);
    }

    saveConfiguration(language: string, notification: string[]) {
        console.log("Language:" + language + ", " + "Notification:" + notification);
        this.account.language = language;
        this.account.notification = notification;

        console.log(this.account);
    }

    saveReportProblem(reportProblemText: string) {
        console.log("Reported problem:" + reportProblemText);
    }

    deleteAccount() {
        console.log("Service was successfully deleted.");
    }
    downloadInformation() {
        console.log("Information was successfully downloaded.");
    }

    getAccount() {
        var accountObject : Account;
        accountObject = this.account;
        return accountObject;
    }
}
