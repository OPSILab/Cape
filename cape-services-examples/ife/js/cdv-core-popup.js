// Citizen Data Vault Core Client (cdv-core-popup.js)
//-----------------------------------------------------------------------------
// This JavaScript contains the client side of the CDV component
// related to form fill features. The main functionality is to
// create the calls to the server side of the CV instance
// - Used by cdv-ui-popup.js
// - The CDV server side code is available in:
//              https://github.com/SIMPATICOProject/CDV
//-----------------------------------------------------------------------------

var cdvCORE = (function () {
	var instance;
	function Singleton() {

		var endpoint = "http://localhost:8080";
		var serviceID = 2;
		var serviceName = "2";
		var serviceURL = "http://localhost:8080/service2";
		var dataFields = [];
		var serviceLink = '';
		var serviceLinkToken='';
		var username = '';
        var cdvDashUrl='#' 
		var datasetId="h727";
		var purposeId='';
		var cr_id = '';
		var cr_Token = '';
		/**
		 * INIT THE ENGINE CONFIG. PARAMETERS:
		 * - endpoint: URL OF THE CDV API
		 */
		function initComponent(parameters) {

			if (parameters.endpoint) {
				endpoint = parameters.endpoint;
			}
			if (parameters.serviceID) {
				serviceID = parameters.serviceID;
			}
			if (parameters.serviceName) {
				serviceName = parameters.serviceName;
			}
			if (parameters.serviceURL) {
				serviceURL = parameters.serviceURL;
			}
			if (parameters.dataFields) {
				dataFields = parameters.dataFields;
			}
            if (parameters.cdvDashUrl) {
				cdvDashUrl = parameters.cdvDashUrl;
			}
			if (parameters.datasetId) {
				datasetId = parameters.datasetId;
			}
			if (parameters.purposeId) {
				purposeId = parameters.purposeId;
			}

		}

		this.cdv_getdata = function (updatePDataFields, errorCallback) {
			var properties = {};
			console.log("SERVICEID:" + serviceID);
			var userData = JSON.parse(localStorage.userData || 'null');
			console.log("USERDATA:" + JSON.parse(localStorage.userData || 'null'));

			var url = endpoint + "/pdata-manager/api/v1/getPData";
			var data = JSON.parse(localStorage.userData || 'null');
			var tokenData = localStorage.tokenData || 'null';
			console.log(tokenData);
			var pdata = new PData(data.userId, serviceLink, serviceLinkToken);
			$.ajax({
				url: url,
				type: 'POST',
				data: pdata.toJsonString(),
				contentType: "application/json; charset=utf-8",
				dataType: 'json',
				success: (function (json) {
					console.log(JSON.stringify(json));
					updatePDataFields(json);

				}),
				error: function (jqxhr, textStatus, err) {
					console.log(textStatus + ", " + err);
					errorCallback("Errore nella comunicazione col server");
				},
				beforeSend: function (xhr) {
					xhr.setRequestHeader('Authorization', 'Bearer ' + tokenData);

				}

			});
		}
		
		this.cdv_getdataByConsent = function (updatePDataFields, errorCallback) {
			var properties = {};
			console.log("SERVICEID:" + serviceID);
			var userData = JSON.parse(localStorage.userData || 'null');
			console.log("USERDATA:" + JSON.parse(localStorage.userData || 'null'));

			var url = endpoint + "/pdata-manager/api/v1/getPDataByConsent";
			var data = JSON.parse(localStorage.userData || 'null');
			var tokenData = localStorage.tokenData || 'null';
			var pdata = new PDataConsent(data.userId, serviceLink, serviceLinkToken,cr_id,cr_Token);
			$.ajax({
				url: url,
				type: 'POST',
				data: pdata.toJsonString(),
				contentType: "application/json; charset=utf-8",
				dataType: 'json',
				success: (function (json) {
					console.log(JSON.stringify(json));
					updatePDataFields(json);

				}),
				error: function (jqxhr, textStatus, err) {
					console.log(textStatus + ", " + err);
					errorCallback("Errore nella comunicazione col server");
				},
				beforeSend: function (xhr) {
					xhr.setRequestHeader('Authorization', 'Bearer ' + tokenData);

				}

			});
		}

		this.cdv_postdata = function (callback) {

			var data = JSON.parse(localStorage.userData || 'null');
			var url = endpoint + "/pdata-manager/api/v1/postPData?mode=append";
			var tokenData = localStorage.tokenData || 'null';
			console.log(tokenData);
			var pdata = formFieldsToJSON(serviceLink,serviceLinkToken, data.userId, dataFields, "", "");

			$.ajax({
				url: url,
				type: 'POST',
				data: pdata,
				contentType: "application/json; charset=utf-8",
				success: function (resp) {
					console.log("pdata saved!");
					callback(true);

				},
				error: function (jqxhr, textStatus, err) {
					console.log(textStatus + ", " + err);
					callback(false);
				},
				beforeSend: function (xhr) {
					xhr.setRequestHeader('Authorization', 'Bearer ' + tokenData);

				}

			});

		}
		
		
		this.cdv_postdataByConsent = function (callback) {

			var data = JSON.parse(localStorage.userData || 'null');
			var url = endpoint + "/pdata-manager/api/v1/postPDataByConsent?mode=append";
			var tokenData = localStorage.tokenData || 'null';
			console.log(tokenData);
			var pdata = formFieldsToJSON(serviceLink,serviceLinkToken, data.userId, dataFields, cr_id, cr_Token);

			$.ajax({
				url: url,
				type: 'POST',
				data: pdata,
				contentType: "application/json; charset=utf-8",
				success: function (resp) {
					console.log("pdata saved!");
					callback(true);

				},
				error: function (jqxhr, textStatus, err) {
					console.log(textStatus + ", " + err);
					callback(false);
				},
				beforeSend: function (xhr) {
					xhr.setRequestHeader('Authorization', 'Bearer ' + tokenData);

				}

			});

		}

		this.cdv_getSLink = function (callback) {

			var data = JSON.parse(localStorage.userData || 'null');
			var url = endpoint + "/account-manager/api/v1/users/" + data.userId + "/services/" + serviceID + "/serviceLink";
			var tokenData = localStorage.tokenData || 'null';
			console.log(">>> getting serviceLink "+tokenData);
			

			$.ajax({
				url: url,
				type: 'GET',
				contentType: "application/json; charset=utf-8",
				dataType: 'json',
				success: function (json) {
					console.log(json._id);
					serviceLink = json._id;
					serviceLinkToken = json.slrToken;
					callback(true, true);

				},
				error: function (jqxhr, textStatus, err) {
					console.log(textStatus + ", " + err);
					callback(true, false);

				},
				beforeSend: function (xhr) {
					xhr.setRequestHeader('Authorization', 'Bearer ' + tokenData);

				}

			});

		}
		
		
			
		

		this.cdv_getAccount = function (callback) {

			var data = JSON.parse(localStorage.userData || 'null');
			var url = endpoint + "/account-manager/api/v1/users/" + data.userId + "/serviceLink";
			var tokenData = localStorage.tokenData || 'null';
			console.log(">>> getting account info "+tokenData);
			

			$.ajax({
				url: url,
				type: 'GET',
				contentType: "application/json; charset=utf-8",
				dataType: 'json',
				success: function (json) {
					console.log(json.username);
					username = json.username;
					localStorage.accountData=username;
					callback(true);

				},
				error: function (jqxhr, textStatus, err) {
					console.log(textStatus + ", " + err);
					callback(false);

				},
				beforeSend: function (xhr) {
					xhr.setRequestHeader('Authorization', 'Bearer ' + tokenData);

				}

			});

		}

		this.cdv_createAccount = function (callback) {

			var data = JSON.parse(localStorage.userData || 'null');
			var url = endpoint + "/account-manager/api/v1/accounts";
			var tokenData = localStorage.tokenData || 'null';
			console.log(tokenData);
			var account = accountToJSON(data.userId, data.name, data.surname);

			$.ajax({
				url: url,
				type: 'POST',
				data: account,
				contentType: "application/json; charset=utf-8",
				success: function (resp) {
					console.log("account created");
					username = resp.username;
					localStorage.accountData=username;
					callback(true);

				},
				error: function (jqxhr, textStatus, err) {
					console.log(textStatus + ", " + err);
					callback(false);
				},
				beforeSend: function (xhr) {
					xhr.setRequestHeader('Authorization', 'Bearer ' + tokenData);

				}

			});

		}

		this.cdv_createSLR = function (consent, callback) {

			var data = JSON.parse(localStorage.userData || 'null');
			var url = endpoint + "/account-manager/api/v1/accounts/" + username + "/serviceLinks";
			var tokenData = localStorage.tokenData || 'null';
			console.log(tokenData);
			var slr = slrToJSON(data.userId, serviceID, serviceURL, serviceName);

			$.ajax({
				url: url,
				type: 'POST',
				data: slr,
				contentType: "application/json; charset=utf-8",
				success: function (resp) {
					console.log("slr saved!");
					serviceLink = resp._id;
					serviceLinkToken = resp.slrToken;
					if (consent) {
						$.ajax({
							type: "POST",
							url: endpoint + "/consent-manager/api/v1/giveConsent/" + username,
							contentType: "application/json; charset=utf-8",
							data: JSON.stringify(consent),
							beforeSend: function (xhr) {
								xhr.setRequestHeader('Authorization', 'Bearer ' + localStorage.tokenData);
							},
							success: function (datax) {
								console.log(datax);
								var cr_commonpart=datax.sink_consent_record.common_part;
					            cr_id = cr_commonpart.cr_id;
					            cr_Token = datax.sink_consent_record.consentSignedToken;
					            console.log("cr_id "+cr_id);
					            var datamapping= cr_commonpart.rs_description.resource_set.datasets[cr_commonpart.rs_description.resource_set.datasets.length-1].dataMappings;
					            updateFields(datamapping);
																							
								callback(true,true,true,datamapping);
								
								//callback(true, true);
							},
							error: function (e) {
								console.log(e);
							}
						});
					}
					else{
						callback(true, true,true,[]);
					}
					

				},
				error: function (jqxhr, textStatus, err) {
					console.log(textStatus + ", " + err);
					callback(false, false);
				},
				beforeSend: function (xhr) {
					xhr.setRequestHeader('Authorization', 'Bearer ' + tokenData);

				}

			});

		}
		
		function updateFields(datamapping){
			
			
				dataFields=[];
				for (var i = 0; i < datamapping.length; i++) {
					
					dataFields.push(datamapping[i].property);
				}							
				
			}

		this.cdv_exportData = function () {

			var dataUser = JSON.parse(localStorage.userData || 'null');
			var url = endpoint + "/pdata-manager/api/v1/pData/download?fileFormat=CSV";
			var tokenData = localStorage.tokenData || 'null';
			console.log(tokenData);

			$.ajax({
				url: url,
				type: 'GET',
				contentType: "application/json; charset=utf-8",
				success: function (json) {
					console.log(json);
					var data = encodeURIComponent(json);


					$("<a />", {
						"download": "data.csv",
						"href": "data:application/json;charset=utf-8," + data
					}).appendTo("body")
					.click(function () {
						$(this).remove()
					})[0].click()
					
				},
				error: function (jqxhr, textStatus, err) {
					console.log(textStatus + ",	" + err);
					
				},
				beforeSend: function (xhr) {
					xhr.setRequestHeader('Authorization', 'Bearer ' + tokenData);
					xhr.setRequestHeader('accountId', username);

				}

			});

		}
		
		
		
		this.cdv_removeCDV = function () {

			var dataUser = JSON.parse(localStorage.userData || 'null');
			var url = endpoint + "/account-manager/api/v1/accounts/"+username;
			var tokenData = localStorage.tokenData || 'null';
			console.log(tokenData);

			$.ajax({
				url: url,
				type: 'DELETE',
				contentType: "application/json; charset=utf-8",
				success: function (json) {
					console.log(json);
										
				},
				error: function (jqxhr, textStatus, err) {
					console.log(textStatus + ",	" + err);
					
				},
				beforeSend: function (xhr) {
					xhr.setRequestHeader('Authorization', 'Bearer ' + tokenData);
					
				}

			});

		}

		
		
		this.cdv_getConsentForm = function ( callback) {

			var url = endpoint + "/consent-manager/api/v1/fetchConsentForm/";
			var accountData = (localStorage.accountData || 'null');
			$.ajax({
				type: "GET",
				url: url+accountData+"/"+serviceID+"/"+serviceID+"/"+purposeId+"/"+datasetId,
				contentType: "application/json; charset=utf-8",
				beforeSend: function (xhr) {
					xhr.setRequestHeader('Authorization', 'Bearer ' + localStorage.tokenData);
				},
				success: function (data) {
					callback(data);
					
				},
				error: function (xhr, status, error) {
					console.log(xhr.responseText);
				}
			});

		}
		
		//Get Consent
		
		this.cdv_getConsent = function (callback) {

			var data = JSON.parse(localStorage.userData || 'null');
			var url = endpoint + "/consent-manager/api/v1/consents/active/" + username + "/" + serviceLink +"/" + serviceID;
			var tokenData = localStorage.tokenData || 'null';
			console.log(">>> getting serviceLink "+tokenData);
			
			$.ajax({
				url: url,
				type: 'GET',
				contentType: "application/json; charset=utf-8",
				dataType: 'json',
				success: function (json) {
					
					if (jQuery.isEmptyObject(json)){
						
						callback(true,true,false,[]);
						
					} else{
						var cr_commonpart=json.sink.common_part;
						cr_id = cr_commonpart.cr_id;
						cr_Token = json.sink.consentSignedToken;
						console.log("cr_id "+cr_id);
					
						var datamapping= cr_commonpart.rs_description.resource_set.datasets[cr_commonpart.rs_description.resource_set.datasets.length-1].dataMappings;
						updateFields(datamapping);
						callback(true,true,true,datamapping);
						
					}
					

				},
				error: function (jqxhr, textStatus, err) {
					console.log(textStatus + ", " + err);
					callback(true, true, false,[]);

				},
				beforeSend: function (xhr) {
					xhr.setRequestHeader('Authorization', 'Bearer ' + tokenData);

				}

			});

		}
		
		
		
		//store consent
		
		function storeConsent(mydata) {

			$.ajax({
				type: "POST",
				url: endpointx + "/consent-manager/api/v1/giveConsent/" + username,
				contentType: "application/json; charset=utf-8",
				data: JSON.stringify(mydata),
				beforeSend: function (xhr) {
					xhr.setRequestHeader('Authorization', 'Bearer ' + localStorage.tokenData);
				},
				success: function (datax) {
					console.log(datax);
				},
				error: function (e) {
					console.log(e);
				}
			});
		}
		
		
		
		// pdata
		function PData(userId, slrId,slrToken) {
			this.user_id = userId;
			this.slr_id = slrId;
			this.slr_token = slrToken;
			this.properties = [];
			this.toJsonString = function () {
				return JSON.stringify(this);
			};
		};
		
		
		// pdataConsent
		function PDataConsent(userId, slrId,slrToken,crId,crToken) {
			this.user_id = userId;
			this.slr_id = slrId;
			this.slr_token = slrToken;
			this.cr_id = crId;
			this.cr_token = crToken;
			this.properties = [];
			this.toJsonString = function () {
				return JSON.stringify(this);
			};
		};

		// Helper function to serialize all the form fields into a JSON string
		function formFieldsToJSON(slrId,slrToken, userId, fields, cr_id,cr_token) {
			
			console.log(fields.length);
			var properties = [];
			var jsonStr = JSON.stringify({
					"slr_id": slrId,
					"slr_token": slrToken,
					"user_id": userId,
					"cr_id": cr_id,
					"cr_token": cr_token,
					"properties": []
				});
			var obj = JSON.parse(jsonStr);
			var n = fields.length;
			for (var i = 0; i < n; i++) {
				
				var propertyField= fields[i];
					propertyField=propertyField.replace( /(:|\.|\[|\]|,|=|@)/g, "\\$1" );
				
				
				console.log(propertyField+"-"+$('#'+propertyField).val());
				
				if ($('#' + propertyField).val())
					obj['properties'].push({
						"key": fields[i],
						"values": [$('#' + propertyField).val()]
					});
			}
			jsonStr = JSON.stringify(obj);
			return jsonStr;
		}

		// Helper function to serialize all account fields into a JSON string
		function accountToJSON(userId, firstname, lastname) {
			var properties = [];
			var jsonStr = JSON.stringify({
					"username": firstname + "." + lastname + userId + serviceID,
					"userId": userId
					
				});
			var partStr = JSON.stringify({
					"firstname": firstname,
					"lastname": lastname
				});
			var obj = JSON.parse(jsonStr);
			var part = JSON.parse(partStr);
			obj['particular'] = part;

			jsonStr = JSON.stringify(obj);
			return jsonStr;
		}

		// Helper function to serialize all slr fields into a JSON string
		function slrToJSON(userId, serviceId, serviceURL,serviceName) {
			var properties = [];
			var jsonStr = JSON.stringify({
					"serviceId": serviceId,
					"serviceUri": serviceURL,
					"userId": userId,
					"serviceName":serviceName
				});
			return jsonStr;
		}

		return {
			init: initComponent,
			cdv_getdata: cdv_getdata,
			cdv_getdataByConsent: cdv_getdataByConsent,
			cdv_postdata: cdv_postdata,
			cdv_postdataByConsent: cdv_postdataByConsent,
			initializeSLR: cdv_getSLink,
			initializeAccount: cdv_getAccount,
			createSLR: cdv_createSLR,
			createAccount: cdv_createAccount,
			exportData: cdv_exportData,
			removeCDV: cdv_removeCDV,
			initializeConsent: cdv_getConsentForm,
			cdv_getconsent: cdv_getConsent
		};

	}

	return {
		getInstance: function () {
			if (!instance)
				instance = Singleton();
			return instance;
		}
	};

})();

function setFieldValue(target, value) {
	$('#' + target).val(value).focus();
	$('#' + target).css({
		'border-width': '2px'
	});
}

function openCDV(cdvDashUrl) {
	window.open(cdvDashUrl, "_blank");
}
