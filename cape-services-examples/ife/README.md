# Interactive Front-End
The Interactive Front-End component (IFE) is the main component which dialogs with SIMPATICO users meanwhile they are interacting with a Public Administration e-service. Several features of the SIMPATICO platform could be selected and used through this component.
Features:
 - *Text simplification* of the paragraphs and *terms definitions*
 - Automatic *filling of forms* and guide to navigate among the form sections
 - Ask and *get questions* related to the enhanced e-service
 - Get a *diagram describing* the corresponding e-service

IFE is a group of JavaScript libraries that runs in a web browser and enables to apply the simpatico enhancement features over the existing electronic services.


## Usage 
In order to use Interactive Front-End, JS Libraries should be loaded and configured in each enhanced webpage.
Depending of the features, different JS Libraries should be selected.

| File | Feature | Mandatory | Component | Description |
| :--- | :--- | :---: | :---: | :--- |
| *simpatico-ife.js* | **Main Toolbar** | Yes | - | The main toolbar which exposes the buttons to enable/disable the features |
|  *simpatico-auth.js* | **Authentication**  | Yes | AAC |The Authentication and Authorization Control Module client |
| *ctz-ui.js* and *ctz-core.js* | **Questions and Diagrams**  | No | Citizenpedia  | The Citizenpedia Component client which exposes questions related to the e-service, it enables users to ask new ones and search a diagram which represents the current e-service |
| *tae-ui.js* and *tae-core.js* | **Text Adaptation**  | No | TAE  | The Text Adaptation Engine Component client which exposes text simplifications and complex words definitions and synonyms to ease the e-service understanding  |


## Integration steps 
In order to successfully integrate the IFE with an e-service, 5 main steps should be followed:

### 1. Enhanced webpage set up
In order to enhance a webpage with the IFE, an element which will contain the toolbar should be defined, as well as global scope JavaScript variables which identifies the e-service to enhance.

#### Simpatico toolbar container definition
Inside the body of the webpage, a ```<div>``` element with an special id (by default is ```simpatico_top```) should be placed.

Example:
```html
  <div id="simpatico_top"></div>
```

#### Global variables

In order to identify the e-service which is going to be enhanced, two JavaScript variables should be initialized at global scope level:
* **simpaticoEservice**: It contains the unique id of the enhanced e-service. It is used by the Citizenpedia client.
* **simpaticoCategory**: It contains the general category of the enhanced e-service. It is used by the Citizenpedia client.

Example:
```html
  <script type="text/javascript">
    var simpaticoEservice = "BS607A"; //  the id corresponding to the e-service
    var simpaticoCategory = "Wellness"; // the general category of the e-service
  </script>
```

### 2. Injection of JS Libraries
The JavasScript Libraries which corresponds to the selected features should be injected inside the HTML code of the enhanced e-service and runned after the global variables set up. The *simpatico-ife.js* lib should be loaded after loading the rest of ones. 

Example:
```html
  <script src="js/ctz-ui.js"></script>
  <script src="js/ctz-core.js"></script>
  
  <script src="js/tae-ui.js"></script>
  <script src="js/tae-core.js"></script>
  
  <script src="js/simpatico-auth.js"></script>
  <script src="js/simpatico-ife.js"></script>
```

### 3. Init calls and buttons parameter set ut
Inside the *simpatico-ife.js*, the *initFeatures()* function will contain the calls to modify.  
For each feature (including the authentication), a call to the corresponding init method should be done.
Depending on the feature, the parameters for each call are different.

#### Authentication

Example of an init call:
```JavaScript
  authManager.getInstance().init({
    endpoint: 'https://the-aac-instance-endpoint.com', 
    clientID: 'A0A0A0A0-A0A0-A0A0-A0A0-A0A0A0A0A0A0',
    authority: "google"
  });
```
Parameters:
* **endpoint**: the main URL of the used AAC instance.
* **clientID**: the IFE Client ID registered in the AAC instance
* **authority**: the used authentication mechanism. Only 'google' is available.

#### Questions:

Example of an init call:
```JavaScript
  citizenpediaUI.getInstance().init({
    endpoint: 'https://the-citizenpedia-instance-endpoint.com'
    primaryColor: "#24BCDA",
    secondaryColor:"#D3F2F8",
    elementsToEnhanceClassName: "simp-text-paragraph",
    questionsBoxClassName: "simp-ctz-ui-qb",
    questionsBoxTitle: "RELATED QUESTIONS",
    addQuestionLabel: "+ Add new question",
    diagramNotificationImage: "./img/diagram.png",
    diagramNotificationClassName: "simp-ctz-ui-diagram",
    diagramNotificationText: "There is one diagram related to this e-service in Citizenpedia"
  });
```
Parameters:
* **endpoint**: the main URL of the used Citizenpedia instance
* **primaryColor**: color used to highlight the enhanced components
* **secondaryColor**: color used to paint the question boxes backgrounds
* **elementsToEnhanceClassName**: the CSS class used to define the enhanced elements
* **questionsBoxClassName**: the CSS class of the box which shows questions
* **questionsBoxTitle**: title of the box which shows questions
* **addQuestionLabel**: text exposed to show the action to create a question
* **diagramNotificationImage**: Image to show when a diagram is found
* **diagramNotificationClassName**: The CSS class of the img shown when a diagram is found
* **diagramNotificationText**: The text to notify that a diagram

#### Text Adaptation Engine:

Example of an init call:
```JavaScript
  taeUI.getInstance().init({
    endpoint: 'https://the-tae-instance-endpoint.com',
    language: 'it',
    primaryColor: "#DE453E",
    secondaryColor:"#F0ABA8",
    elementsToEnhanceClassName: "simp-text-paragraph",
    simplifyBoxClassName: "simp-tae-ui-sb",
    simplifyBoxTitle: "Simplified text",
    wordPropertiesClassName: "simp-tae-ui-word"
  });
```
Parameters:
* **endpoint**: the main URL of the used TAE instance
* **language**: the language of the text to be adapted by the TAE instance
* **primaryColor**: Color used to highlight the enhanced components
* **secondaryColor**: Color used to paint the simplification boxes backgrounds
* **elementsToEnhanceClassName**: The CSS class used to define the enhanced elements
* **simplifyBoxClassName**: The CSS class of the box which shows the simplifications
* **simplifyBoxTitle**: Title of the box which shows the simplifications
* **wordPropertiesClassName**: The CSS class of the word properties box

#### Text Adaptation Engine (With Free Text Popup):

Example of an init call:
```JavaScript
  taeUIPopup.getInstance().init({
      lang: 'it',
      endpoint: 'https://dev.smartcommunitylab.it/simp-engines/tae',
      dialogTitle: 'Arricchimento testo',
      tabDefinitionsTitle: 'Definizioni',
      tabSimplificationTitle: 'Semplificazione',
      tabWikipediaTitle: 'Wikipedia',
      entryMessage: 'Scegli il tipo di aiuto',
      notextMessage: 'Nessun testo selezionato'
  });
```
Parameters:
* **endpoint**: the main URL of the used TAE instance
* **lang**: the language of the text to be adapted by the TAE instance
* **dialogTitle**: popup title
* **tabDefinitionsTitle**: title of 'definitions' tab
* **tabSimplificationTitle**: title of 'simplifications' tab
* **tabWikipediaTitle**: title of 'wikipedia' tab
* **entryMessage**: label of 'enter text' hint
* **notextMessage**: label of 'no text selected' hint

#### Workflow Adaptation Engine (With Free Text Popup):

Example of an init call:
```JavaScript
  waeUI.getInstance().init({
		endpoint: 'https://dev.smartcommunitylab.it/simp-engines/wae',
		prevButtonLabel: 'Precedente',
		nextButtonLabel: 'Successivo',
		topBarHeight: 60,
		errorLabel: {
			'block1' : 'Manca il codice fiscale',
			'block4' : 'Manca selezione Part-time / Full-time'
		}
  });
```
Parameters:
* **endpoint**: the main URL of the used WAE instance
* **prevButtonLabel**: Label for 'previous step' button
* **nextButtonLabel**: Label for 'next step' button
* **topBarHeight**: height of the bar to control the scroll
* **errorLabel**: map with blockId - error message in case of block precondition fails

Please note that the module requires that the corresponding workflow has been uploaded to the WAE repository. The URI of the
workflow model is configured directly in the page as an **data-simpatico-workflow** attribute of the enclosing HTML tag, e.g.,
```HTML
<form data-simpatico-workflow="http://simpatico.eu/test" ...
```


### 4. Buttons configuration

In order to personalise the look and feel of each feature button, the parameters of each one should be defined.
* **id**: the unique element id used to get the button inside the DOM
* **imageSrcEnabled**: the URL of the image shown when the button is enabled
* **imageSrcDisabled**: the URL of the image shown when the button is disabled
* **alt**: the alternative text of the button
* **styleClassEnabled**: the CSS class applied to the button shown when it is enabled
* **styleClassDisabled**: the CSS class applied to the button shown when it is disnabled


Example of the buttons configuration:
```JavaScript
  buttons = [{
                  id: "simp-bar-sw-login",
                  // Ad-hoc images to define the enabled/disabled images
                  imageSrcEnabled: "./img/ic_on.png",
                  imageSrcDisabled: "./img/login.png",
                  alt: "Autheticate",
                  // Ad-hoc css classes to define the enabled/disabled styles
                  styleClassEnabled: "simp-none", 
                  styleClassDisabled: "simp-none",
                  
                  isEnabled: function() { return authManager.getInstance().isEnabled(); },
                  enable: function() { authManager.getInstance().enable(); },
                  disable: function() { authManager.getInstance().disable(); }
                },

                {
                  id: "simp-bar-sw-citizenpedia",
                  // Ad-hoc images to define the enabled/disabled images
                  imageSrcEnabled: "./img/citizenpedia.png",
                  imageSrcDisabled: "./img/citizenpedia.png",
                  alt: "Questions and answer",
                  // Ad-hoc css classes to define the enabled/disabled styles
                  styleClassEnabled: "simp-bar-btn-active",
                  styleClassDisabled: "simp-bar-btn-inactive",

                  isEnabled: function() { return citizenpediaUI.getInstance().isEnabled(); },
                  enable: function() { citizenpediaUI.getInstance().enable(); },
                  disable: function() { citizenpediaUI.getInstance().disable(); }
                },
                {
                  id: "simp-bar-sw-tae",
                  // Ad-hoc images to define the enabled/disabled images
                  imageSrcEnabled: "./img/simplify.png",
                  imageSrcDisabled: "./img/simplify.png",
                  alt: "Text simplification",
                  // Ad-hoc css classes to define the enabled/disabled styles
                  styleClassEnabled: "simp-bar-btn-active-tae",
                  styleClassDisabled: "simp-bar-btn-inactive-tae",

                  isEnabled: function() { return taeUI.getInstance().isEnabled(); },
                  enable: function() { taeUI.getInstance().enable(); },
                  disable: function() { taeUI.getInstance().disable(); }
                },
                {
                    id: "simp-bar-sw-tae-popup",
                    // Ad-hoc images to define the enabled/disabled images
                    imageSrcEnabled: "./img/enrich.png",
                    imageSrcDisabled: "./img/enrich.png",
                    alt: "Semplificazione del testo selezionato",
                    // Ad-hoc css classes to define the enabled/disabled styles
                    styleClassEnabled: "simp-bar-btn-active-tae",
                    styleClassDisabled: "simp-bar-btn-inactive-tae",

                    isEnabled: function() { return taeUIPopup.getInstance().isEnabled(); },
                    enable: function() { 
                    	taeUIPopup.getInstance().showDialog(); 
                    },
                    disable: function() { 
                    	taeUIPopup.getInstance().hideDialog(); 
                    }
                },
                { // workflow adaptation. Switch to the modality, where the form adaptation starts
                  id: 'workflow',
                  imageSrcEnabled: "./img/forms.png",
                  imageSrcDisabled: "./img/forms.png",
                  alt: "Semplifica processo",
                  // Ad-hoc css classes to define the enabled/disabled styles
                  styleClassEnabled: "simp-bar-btn-active-wae",
                  styleClassDisabled: "simp-bar-btn-inactive",

                  isEnabled: function() { return waeUI.getInstance().isEnabled(); },
                  enable: function() { var idProfile = null; waeUI.getInstance().enable(idProfile); },
                  disable: function() { waeUI.getInstance().disable(); }
                }
            ];
```

### 5. Style set upd
To be completed....

## Development of a new feature

In order to develope a new feature, two main JavaScrip Libraries should be created and implemented.
* **newfeature-ui.js**: JavaScript which contains the functionality related to the User Interface.
* **newfeature-core.js**: JavaScript which contains functions related to the main functionality (e.g. the server calls). It will be called by *newfeature-ui.js*

### 1. Implementation of newfeature-ui.js

1. Implement the initComponent(parameters) function. To be completed...
2. Implement the enableComponentFeatures() function. To be completed...
3. Implement the disableComponentFeatures() function. To be completed...
4. Implement the public functions. To be completed...
5. Declare the public functions. To be completed...

UI-template:
```JavaScript
var newfeatureUI = (function () {
  var instance; // Singleton Instance
  var featureEnabled = false; // If the feature is enabled
  function Singleton () {
    // Component-related variables
    var myOwnVariable = '';

    //  [STEP1] Component-related methods and behaviour
    function initComponent(parameters) {
      // Init the Component-related variables
      myOwnVariable = parameters.myOwnVariable
      // Also init the corresponding CORE component
      newFeatureCORE.getInstance().init({
          endpoint: parameters.endpoint
        });
    }
    //  [STEP2] 
    function enableComponentFeatures() {
      if (featureEnabled) return;
      featureEnabled = true;
      // ...
      // Code of the new feature enabling (e.g. add onClick events to elements to do stuff)
    }
    //  [STEP3] 
    function disableComponentFeatures() {
      if (!featureEnabled) return;
      featureEnabled = false;
      // ...
      // Code of the new feature disabling (e.g. remove the added onClick events)
    }

    // [STEP4]
    function doStuff(element) {
      //...
    }

    return {
      // Mandatory definitions
      init: initComponent, // Called only one time
      enable: enableComponentFeatures,  // Called when the Component button is enabled
      disable: disableComponentFeatures, // Called when the Component button is disabled or another one enabled
      isEnabled: function() { return featureEnabled;}, // Returns if the feature is enabled
      // [STEP5] The ad-hoc functions
      doStuff: doStuff // Special public function
    };
  }
  
  return {
    getInstance: function() {
      if(!instance) instance = Singleton();
      return instance;
    }
  };
})();
```

###2. Implementation of newfeature-core.js
The var declared in this file is only used (it should not be used by another objects) in by the one declared in *newFeatureUI.js*, concretely, by newfeatureUI.

Taking the *CORE-template* template as a basis, the functions used in *newFeatureUI.js* should be implemented below the comment tagged as *[STEP1]* and declared below the comment tagged as *[STEP2]*

CORE-template:
```JavaScript
var newFeatureCORE = (function () {
  var instance;
  function Singleton () {
    // Component-related variables
    var endpointA = '';
    var endpointB = '';
    
    //In inits the main used variables (e.g. The URLs used)
    function initComponent(parameters) {
      endpointA = parameters.endpoint + '/A';
      endpointB = parameters.endpoint + '/B';
    }
    
    // [STEP2] Implementation
    function coreFunctionA() { // Do core stuff }
    function coreFunctionB() { // Do core stuff }

    return {
        // [STEP1] Functions used in newFeatureUI 
        init: initComponent,
        coreFunctionA: coreFunctionA,
        coreFunctionB: coreFunctionB
      };
  }  
  return {
    getInstance: function() {
      if(!instance) instance = Singleton();
      return instance;
    }
  };
})();

```




