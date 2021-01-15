import { NgxConfigureOptions } from 'ngx-configure';
import { Injectable } from '@angular/core';

@Injectable()
export class AppOptions extends NgxConfigureOptions {
  ConfigurationURL = 'assets/config.json';
  AppVersion = '0.0.0';
  BustCache = false;
}
