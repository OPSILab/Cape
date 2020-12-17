/**
 * CaPe API - Consent Manager
 * CaPe APIs used to manage CaPe Consent Form, Consent Records and consenting operations
 *
 * The version of the OpenAPI document: 2.0
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
import { Dataset } from './dataset';


export interface ResourceSet { 
    rs_id: string;
    datasets: Array<Dataset>;
}

