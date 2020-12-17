/**
 * SDK Service API
 * SDK Service API for integration with cape
 *
 * The version of the OpenAPI document: 2.0
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
import { ServiceUrls } from './serviceUrls';
import { DataController } from './dataController';
import { ServiceProvider } from './serviceProvider';
import { Cert } from './cert';


export interface ServiceInstance { 
    serviceProvider?: ServiceProvider;
    cert?: Cert;
    serviceUrls?: ServiceUrls;
    dataController?: DataController;
}

