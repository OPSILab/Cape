// Auth functionality
// The AAC server has to be installed
// https://github.com/SIMPATICOProject/aac

var authManager = (function () {
  var instance; // Singleton Instance of the UI component
  function Singleton () {
    var featureEnabled = true;
    // Component-related variables
    var userdataElementID = 'simp-usr-data'
    var ifeClientID = '33c10bee-136b-463c-8b9d-ad21d82182db'
    var endpoint = 'http://localhost:8080/aac'
    var authority = 'google';
    var redirect = null;

    function initComponent(parameters) {
      endpoint = parameters.endpoint;
      ifeClientID = parameters.clientID;
      authority = parameters.authority;
      // support null setting where authority is selected by user
      if (!authority) {
        authority = "";
      } else {
        authority = "/"+authority;
      }
      // allow for custom redirect
      redirect = parameters.redirect;
      if (!redirect) {
        var base = window.location.href;
        var arr = base.split("/");
        redirect = arr[0] + '//' + arr[2] + '/IFE/login.html';
      }
    }
      

    // Component-related methods and behaviour
    function handleAuthClick() {
      if (featureEnabled) return;
      var base = window.location.href;
      var arr = base.split("/");
	 
      var url = endpoint + '/eauth/authorize' + authority + '?' +
                    'response_type=token' +
                    '&redirect_uri=' + redirect + // login window URL
                    '&client_id=' + ifeClientID; //Client id from the AAC console
	   console.log(url);				

      var win = window.open(url, 'AuthPopup', 'width=1024,height=768,resizable=true,scrollbars=true,status=true');

      window.addEventListener('message', function (event) {
        jQuery.ajax({
          url: endpoint + '/basicprofile/me',
          type: 'GET',
          dataType: 'json',
          success: function(data) {
            localStorage.userData = JSON.stringify(data);
            updateUserData();
          },
          error: function(err) {
            console.log(err);
          },
          beforeSend: function(xhr) {
            xhr.setRequestHeader('Authorization', 'Bearer ' + event.data.access_token);
          }
        });
        localStorage.aacTokenData = JSON.stringify(event.data);
      }, false);
    }

    // attach login flow to the sign-in button
    function handleSignoutClick(event) {
      if (!featureEnabled) return;
      localStorage.userData = '';
	  localStorage.aacTokenData = '';
	  updateUserData();
    }

    // It checks if the corresponding user is previously loged in and updates the view accordingly 
    function updateUserData () {
      console.log(">>> updateUserData()");
        var data = JSON.parse(localStorage.userData || 'null');
        if (!!data) {
          userData = data;
          document.getElementById(userdataElementID).innerHTML = 'Hello, ' + data.name;
          document.getElementById(userdataElementID).style = "display:block";
          enablePrivateFeatures();
          featureEnabled = true;
        } else {
          document.getElementById(userdataElementID).innerHTML = "";
          disablePrivateFeatures();
          featureEnabled = false;
        }
      console.log("<<< updateUserData()");
    }

    return {
      // Public definitions
      init: initComponent,          // Called only one time
      enable: handleAuthClick,      // When the Component button is enabled
      disable: handleSignoutClick,  // When the CB. is disabled or another one enabled
      isEnabled: function() { return featureEnabled;}, // Returns if the feature is enabled
      // More component related public methods
      updateUserData: updateUserData
    };
  }
  
  return {
    getInstance: function() {
      if(!instance) instance = Singleton();
      return instance;
    }
  };
})();
