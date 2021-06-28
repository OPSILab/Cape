# Installing CaPe SDK - Angular Frontend Plugin

Cape SDK comes with the Angular Frontend Plugin, the Angular library enabling to easily integrate existing Angular projects with Cape funcionalities, in particular hiding the interaction with Cape SDK APIs.

The `cape-sdk-angular` library can be found on **npmJs** [here](https://www.npmjs.com/package/cape-sdk-angular). It provides:

  - **Component**: (cape-sdk-angular.component) Provides a "CaPe button" with contextual menu providing options for the Linking e Consenting functionalities. 
                  The component selector `lib-cape-sdk-angular` will be embedded in the template of the existing service pages.		  
  &nbsp; 

  - **Angular Injectable service**: (cape-sdk-angular.service) Injectable service providing methods to make HTTP calls to Cape SDK APIs and Observables to which subscribe to get Events regarding Service Linking and Consenting. It must be injected into Typescript part of existing Angular project (Host component).
 
                                    
The target is to provide a tool and a development workflow to easily integrate CaPe SDK functionalities into an existing Angular project, namely the Service to be extended with Cape capabilities. 

In order to include the library package in your Angular project, perform following steps:

 
 1) Place in project root folder and run following command:
   
    npm install --save cape-sdk-angular

 2) Integrate the library with your code as described in the section below.

---
# Integrating SDK Angular Plugin into your existing code

 ## Add library assets and configure translation (ngx-translate)

In order to correctly integrate the library module in your application, followinng steps must be performed:

  - Add library assets (images, i18n translations, etc) to your project `angular.json` file, in the `architect.build.assets` array, the following object:

             {
                "glob": "**/*",
                "input": "./node_modules/cape-sdk-angular/assets",
                "output": "/assets/cape"
              }

  - As the library uses `ngx-translate` library to implement internationalization:
      * if you are already using ngx-translate library in your application, use the MultiTranslateHttpLoader (from `ngx-translate-multi-http-loader` package) and add following entry in the constructor array:
		
		    export function HttpLoaderFactory(http: HttpClient) {
            return new MultiTranslateHttpLoader(http, [
             { prefix: './assets/i18n/', suffix: '.json' },
             { prefix: './assets/cape/i18n/', suffix: '.json' },
            ]);}

		  * If not using already ngx-translate, set it up in your application with TranslateHttpLoader as following:

				export function createTranslateLoader(http: HttpClient) {
				  return new TranslateHttpLoader(http, './assets/cape/i18n/', '.json');
				}

 ## Inject library service and component in your page (host component)

The SDK Angular Plugin uses the [RxJs Subject](https://rxjs-dev.firebaseapp.com/guide/subject#subject) - Subscription mechanism.
On one hand the host component will subscribe to Subject exposed by the plugin, in order to catch Event coming from interaction with Cape SDK (and then Cape Server).
Events will be the results of starting Service Linking and Consenting operations through the embedded "Cape Button".
This workflow was adopted to loosly couple the host component (existing Service) and the SDK plugin code, running as separated module and only exposing information needed to the Service (namely the Events). 

On the other hand, the Component part of SDK Plugin will need in input (with Angular input [] notation) the configurations depending on the particular Service instance, its description and how has been installed related Cape SDK Client instance.
  
  - Add **CapeSDKAngularModule** to the imports of feature module that will use its functionalities.    
  
  - Add the component selector `<lib-cape-sdk-angular>` to the page template where the button will be placed (host component).
    It requires following inputs (which can be retrieved from a Service specific configuration file for instance)
  
    - **`[locale]`** : project Locale ( "en", "it" supported) 
    - **`[operatorId]`**: Operator Id identifying Cape Server instance (see [Service Manager APIs]()).
    - **`[sdkUrl]`**: Endpoint (`host`: `port`/api/v2) where the installed Cape SDK Client instance is listening (e.g. http://localhost:8085/cape-service-sdk/api/v2).
    - **`[userId]`**: The identifier of the User at the Service (depends on service IdM and the way in which the User Id is retrieved in the Application frontend).
    - **`[serviceId]`**: ServiceId matching the value in the corresponding "serviceId" field of Service description registered by Service Provider (either via Data Controller Dashboard o directly via SDK APIs).
    - **`[serviceName]`**: Service Name matching the value in the corresponding "name" filed of Service description registered by Service Provider. 
    - **`[returnUrl]`**: Url to witch return after Service Linking (e.g. the Application page in which the cape-sdk-angular component button is embedded). 
    - **`[purposeId]`**: Purpose Id matching one of the ones defined in the Service description registered by Service Provider (field "processingBases" -> "purposeId").
	
		    
**Note**. For further information about Service description fields that must match with above inputs, see the [Data Controller Dashboard - Configuring Service Instance]() section.
			
  - The Typescript part of host component must import and inject the **`cape-sdk-angular.service`**.
			
    ```
       constructor( private capeService: CapeSdkAngularService){}
    ```

  - It must subscribe to the Subjects (**`serviceLinkStatus$`** and **`consentRecordStatus$`**) exposed by **`cape-sdk-angular.service`**. These subjects will emit several events about the status of ServiceLinking and Consenting respectively.
  
  Example:
```
this.capeService.serviceLinkStatus$.pipe(takeUntil(this.unsubscribe)).subscribe(async event => {

      event = event as ServiceLinkEvent;

      if (event?.serviceId === this.serviceId)
        this.capeLinkStatus = event.status;

      this.cdr.detectChanges();
    });

    this.capeService.consentRecordStatus$.pipe(takeUntil(this.unsubscribe)).subscribe(async event => {

      event = event as ConsentRecordEvent;
      if (event?.serviceId === this.serviceId)
        this.capeConsentStatus = event.status.consent_status;
    });

    this.cdr.detectChanges();
  }	
	
```

  - `capeLinkStatus` and `capeConsentStatus` are variables defined in the host component that will be updated according to Events emitted by Cape Sdk Angular Subjects.

These are the Typescript interfaces about Cape Events exposed by cape-sdk-angular.service:

```
export interface ServiceLinkEvent {
  serviceId: string;
  slrId?: string;
  surrogateId?: string;
  status: SlStatusEnum;
}

export interface ConsentRecordEvent {
  serviceId: string;
  crId: string;
  status: ConsentStatusRecordPayload;
  consentRecord?: ConsentRecordSigned;
}
```

See `cape-sdk-angular/model` folder for further information on `ConsentStatusRecordPayload`, `ConsentRecordSigned`, `SlStatusEnum` classes.
These mirror the models defined in the [Cape SDK APIs]().




**Note**. A complete example of integration of Cape SDK Angular plugin and an existing service can be found in the Cape Playground folder of Cape repository ([here](https://github.com/OPSILab/Cape/tree/main/cape-playground)).



