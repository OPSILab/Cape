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
import { Description } from './description';
import { DatasetSchema } from './datasetSchema';
import { Distribution } from './distribution';
import { DataMapping } from './dataMapping';


export interface IsDescribedAt { 
    datasetId?: string;
    description?: Array<Description>;
    datasetSchema?: DatasetSchema;
    dataStructureSpecification?: string;
    distribution?: Distribution;
    dataMapping?: Array<DataMapping>;
}

