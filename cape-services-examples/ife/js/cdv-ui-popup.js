
// CaPe web page plugin (cdv-ui-popup.js)
//-----------------------------------------------------------------------------
// This JavaScript contains the functionality related to the User Interface
// which enriches the Interactive Front-End component with the features of
// the CaPe component.
// - It uses the methods implemented in cdv-core-popup.js
// 
//-----------------------------------------------------------------------------



var cdvUI = (function () {
	var instance; // Singleton Instance of the UI component
	var featureEnabled = false;
	var consent_given = false;
	var dialog_cdv = null;
	var informedConsentLink = "give_consent.html";
	var dataReload=true;

	function Singleton() {

		var colors = {
			//cdv: '#008000'
			cdv:'red'
		}

		var originalCSS = null;

		var labels = {
			dialogTitle: 'Citizen Data Vault',
			entryMessage: 'Welcome to SIMPATICO Citizen Data Vault!',
			statusMessage: 'Now you can select,store and update your personal data to fill form fields.',
			dialogSaveTitle: 'Data Saved',
			dialogSaveMessage: 'Data saved successfully into your Data Vault.',
			statusMessageNoAccount: "No CDV Account associated to you. Create?",
			statusMessageNoActive: "CDV is not active for this service. Activate?",
			confirmSaveDataMessage: "Update your Persona Data?",
			consentMessage1: "The service requires consent to use data for specific purposes",
			consentMessage2: "Check the ones you agree to grant consent for the declared purpose.",
			buttonSaveData: "Save your data",
			buttonManageData: "Manage your data",
			buttonActivate: "Activate",
			buttonCreate: "Create",
			consentButton: "Consent",
			statusMessageAnonymousSelection: "Please select your preference on anonymous processing"

		};

		var dataFields = [];
		var cdvDashUrl = "#";

		/**
		 * CURRENTLY SELECTED FIELD
		 */
		var selectedField = null;

		/**
		 * INITIALIZE UI COMPONENT.
		 * CONFIG PARAMETERS:

		 */
		function initComponent(parameters) {

			if (parameters.cdvDashUrl) {
				cdvDashUrl = parameters.cdvDashUrl;
			}

			if (parameters.dataFields) {
				dataFields = parameters.dataFields;
			}

			if (parameters.informedConsentLink) {
				this.informedConsentLink = parameters.informedConsentLink;
			}

			if (parameters.consentGiven) {
				consent_given = parameters.consentGiven;
			}

			cdvDashUrl: parameters.cdvDashUrl

			cdvCORE.getInstance().init({
				endpoint: parameters.endpoint,
				serviceID: parameters.serviceID,
				serviceName: parameters.serviceName,
				dataFields: parameters.dataFields,
				serviceURL: parameters.serviceURL,
				cdvDashUrl: parameters.cdvDashUrl,
				datasetId: parameters.datasetId,
				purposeId: parameters.purposeId
			});

			labels.dialogTitle = parameters.dialogTitle || labels.dialogTitle;
			labels.entryMessage = parameters.entryMessage || labels.entryMessage;
			labels.statusMessage = parameters.statusMessage || labels.statusMessage;
			labels.dialogSaveTitle = parameters.dialogSaveTitle || labels.dialogSaveTitle;
			labels.dialogSaveMessage = parameters.dialogSaveMessage || labels.dialogSaveMessage;
			labels.statusMessageNoAccount = parameters.statusMessageNoAccount || labels.statusMessageNoAccount;
			labels.statusMessageNoActive = parameters.statusMessageNoActive || labels.statusMessageNoActive;
			labels.confirmSaveDataMessage = parameters.confirmSaveDataMessage || labels.confirmSaveDataMessage;
			labels.buttonSaveData = parameters.buttonSaveData || labels.buttonSaveData;
			labels.buttonManageData = parameters.buttonManageData || labels.buttonManageData;
			labels.buttonActivate = parameters.buttonActivate || labels.buttonActivate;
			labels.buttonCreate = parameters.buttonCreate || labels.buttonCreate;
			labels.consentButton = parameters.consentButton || labels.consentButton;
            labels.statusMessageAnonymousSelection= parameters.statusMessageAnonymousSelection || labels.statusMessageAnonymousSelection;
			colors.cdv = parameters.cdvColor || colors.cdv;

		}

		/**
		 * OPEN CDV UI DIALOG
		 */
		function enableComponentFeatures() {
			if (featureEnabled)
				return;
			featureEnabled = true;
			console.log(">>>>initializing CDV Dialog Box");
			 $(document.body).append ('<div id="overlay" style="display: none;" ><div id="spinner"><img src="img/reload.gif" width="120px" height="120px"></div></div>');
            //Open Spinner
			$("#overlay").show();
			if (!dialog_cdv) {
				var inizialize = initializeServiceLink();
				cdvCORE.getInstance().initializeAccount(inizialize);
				console.log("<<<<initializing CDV Dialog Box");

			} else {

				dialog_cdv.dialog("open");

			}
			//highlightFields(dataFields, true);
			$(document.body).append('<div id="cdv_toolbar_buttons"><button class="btn btn-primary pull-right" title="Open CaPe Client" onClick="toggleDialog();" name="Open Cape Client">&#9776; ' + labels.dialogTitle + '<i class="icon-circle-arrow-down"></i></button></div>');
           
		}

		function disableComponentFeatures() {
			
			if (!featureEnabled)
				return;
			featureEnabled = false;
			selectedField = null;
			highlightFields(dataFields, false);
			$('#plist').remove();
			if (dialog_cdv) {
				$("#overlay").hide();
				dialog_cdv.dialog("destroy");
				$('#dialog-consent').dialog("destroy");
				dialog_cdv = null;
				$('#cdv_toolbar_buttons').remove();
			}

		}

		function setError(target) {
			var targetElement = document.getElementById(target);
			return function (text) {
				targetElement.innerHTML = '<p>' + text + '</p>';
			}
		}

		function confirmUpdateData() {
			return function (saved) {
				var errCb = setError("tab-pdata");
				$("#overlay").hide();
				if (saved) {
					var dialog_saved = $(
							'<div id="dialog-message" title="' + labels.dialogSaveTitle + '">' +
							'			<p>' + labels.dialogSaveMessage + '</p>' +
							'</div>').dialog({
							modal: true,
							buttons: {
								Ok: function () {
									//cdvCORE.getInstance().cdv_getdata(cdvUI.selectedField, "tab-pdata", errCb);
									$("#overlay").show();
									//var getPDataList = updatePDataFields(cdvUI.selectedField, "tab-pdata");
									//cdvCORE.getInstance().cdv_getdata(getPDataList, errCb);
									var getPDataList = updatePDataFields(cdvUI.selectedField, "tab-pdata");
                                    cdvCORE.getInstance().cdv_getdataByConsent(getPDataList, null);
									$(this).dialog("close");
									//dialog_saved.dialog("open");
								}
							}
						});
					dialog_saved.dialog("open");
				}
			}

		}

		function initializeServiceLink() {
			return function (account_exist) {
				console.log("Account " + account_exist);
				if (account_exist) {
					var initialize= initializeConsent();
					cdvCORE.getInstance().initializeSLR(initialize);
				} else {
					var initializeDlg = initializeDialog();
					initializeDlg(false, false, false,[]);
				}
			}
		}
		
		
		function initializeConsent() {
			return function (account_exist, servicelink_exist) {
				console.log("ServiceLink " + servicelink_exist);
				if (servicelink_exist) {
					var initializeDlg = initializeDialog();
                    cdvCORE.getInstance().cdv_getconsent(initializeDlg);
				} else {
					var initializeDlg = initializeDialog();
					initializeDlg(true, false, false, []);
				}
			}
		}
		

		function updatePDataFields(source, target) {
			return function (json) {
				var fieldSelect1 = "<fieldset><legend>Selected field </legend>";
				var fieldSelect2 = "<fieldset><legend>Other available fields</legend>";

				// code for the creation of datalist to anchor to each data input
				var p = $('#plist');
				if (p) {
					p.remove();
				}
				var datalisttemp = '<div id="plist">';
				for (var itemName in json.properties) {
					var property = json.properties[itemName];
					var propertyField = property.key;
					propertyField = propertyField.replace(/(:|\.|\[|\]|,|=|@)/g, "\\$1");

					datalisttemp += '<datalist id="datalist' + property.key + '">';
					datalisttemp += '<select id=' + property.key + ' style="display: none;">';
					for (var field in property.values) {
						datalisttemp += '<option>' + property.values[field] + '</option>';
					}
					datalisttemp += '</select></datalist>';
					$('#' + propertyField).attr("list", "datalist" + property.key);

					if (property.values.length > 0) {
						$('#' + propertyField).val(property.values[0]);
					}

					if (navigator.userAgent.indexOf('Safari') != -1 && navigator.userAgent.indexOf('Chrome') == -1) {
						$('#' + propertyField).autocomplete({
							source: property.values,
							minLength: 0,
						}).focus(function () {
							$(this).autocomplete("search");
						});
					}
				}
				$(document.body).append(datalisttemp + '</div>');
				//Close spinner
				$("#overlay").hide();

				
				

			}

		}

		

		function initializeDialog() {

			return function (account_exist, activated, consentGiven, datamapping) {
				var statusMessage = labels.statusMessage;
				var entryMessage = labels.entryMessage;

				console.log("Initialize dialog: " + account_exist + "-" + activated + "-"+ consentGiven);
				updateFields(datamapping);
						

				if (!account_exist) {
					statusMessage = '<img src="./img/alert.png" width="40" height="40" <span class="ui-icon ui-icon-alert">  ' + labels.statusMessageNoAccount;
					entryMessage = "";
				} else if (!activated) {
					statusMessage = '<img src="./img/alert.png" width="40" height="40" align="bottom">  ' + labels.statusMessageNoActive;
					entryMessage = "";
				}
				
				
				dialog_cdv = createDialogNoTabs(entryMessage, statusMessage);

				if (!account_exist) {
					dialog_cdv.dialog({
						buttons: [{
								text: labels.buttonCreate,
								click: function () {

									if (consent_given) {
										var confirm = activateSLR();
										cdvCORE.getInstance().createAccount(confirm);
										dialog_cdv.dialog("destroy");
									} else {
										showPrivacyPolicyForActivation();

									}

								}

							}
						]

					});

				} else if (!activated) {
					dialog_cdv.dialog({
						buttons: [{
								text: labels.buttonActivate,
								click: function () {
								if (consent_given) {	
									var activate = activateSLR();
									activate(true);
									dialog_cdv.dialog("destroy");
								} else {
								    showPrivacyPolicyForActivation();
								}

							}
							
						}	
						]

					});

				} else if (!consentGiven){
					
					showPrivacyPolicyForActivation(true);
					
					
				}else {
					
					dialog_cdv.dialog({
						buttons: [{
								text: labels.buttonSaveData,
								click: function () {
									$("#overlay").show();
									var confirm = confirmUpdateData();
									cdvCORE.getInstance().cdv_postdataByConsent(confirm);
								}
							}, {

								text: labels.buttonManageData,
								click: function () {
									openCDV(cdvDashUrl);

								}

							}
						]

					});

				}

				

				$("input[type!='submit']").on("click", function () {
					console.log("<<<selected field: " + $(this).attr("id"));
					/*if ($.inArray($(this).attr("id"), dataFields) != -1) {
					console.log("selected field: " + $(this).attr("id"));
					cdvUI.selectedField = $(this);
					var errCb = setError("tab-pdata");
					var getPDataList = updatePDataFields(cdvUI.selectedField, "tab-pdata");
					cdvCORE.getInstance().cdv_getdata(getPDataList, errCb);
					}*/

				});

				$("input[type='submit']").on("click", function () {
					console.log("selected submit: " + $(this).attr("id"));
					var r = confirm(labels.confirmSaveDataMessage);
					if (r == true) {
						//TODO: You can Define "callback" function to elaborate the result of update action
						var callback;
						cdvCORE.getInstance().cdv_postdata(callback);
					}

				});

				$("button[type='submit']").on("click", function () {
					console.log("selected submit: " + $(this).attr("id"));
					var r = confirm(labels.confirmSaveDataMessage);
					if (r == true) {
						//TODO: You can Define "callback" function to elaborate the result of update action
						var callback;
						cdvCORE.getInstance().cdv_postdata(callback);
					}

				});

				
				if (!account_exist || !activated) {
					dialog_cdv.dialog("open");
				} else {
					var getPDataList = updatePDataFields(null, null);
					cdvCORE.getInstance().cdv_getdataByConsent(getPDataList, null);
				}

			}

		}
		
		
		function updateFields(datamapping){
			
				console.log(datamapping);
				dataFields=[];
				for (var i = 0; i < datamapping.length; i++) {
					
					dataFields.push(datamapping[i].property);
				}							
				highlightFields(dataFields, true);
				
			}
		

		function activateSLR(consent) {
			return function (created) {

				if (created) {
					//dialog_cdv.dialog("destroy");

					var initializeDlg = initializeDialog();
					cdvCORE.getInstance().createSLR(consent,initializeDlg);

				} else {
					var initializeDlg = initializeDialog();
					initializeDlg(false, false);
				}
			}
		}

		function reloadDialog() {
			return function (created) {
				console.log("reload " + created);
				if (created) {
					dialog_cdv.dialog("destroy");
					dialog_cdv.dialog("open");
				}
			}
		}

		function confirmActivation(title, message) {

			var dialog_saved = $(
					'<div id="dialog-message" title="' + title + '">' +
					'			<p>' + message + '</p>' +
					'</div>').dialog({
					modal: true,
					buttons: {
						Ok: function () {
							$(this).dialog("close");
							dialog_cdv.dialog("open");
						}
					}
				});
			dialog_saved.dialog("open");

		}

		function highlightFields(fields, selected) {
			var n = fields.length;
			console.log("#" + n);
			var field = "";
			for (var i = 0; i < n; i++) {
				field = fields[i].replace(/(:|\.|\[|\]|,|=|@)/g, "\\$1");
				console.log("#" + field + $('#' + field).html());
				if (selected) {
					originalCSS = $('#' + field).css('border');

					$('#' + field).css({
						'border': '3px solid ' + colors.cdv
					});

				} else if (originalCSS) {

					$('#' + field).css({
						'border': originalCSS
					});

				}

			}

		}

		
		function createDialogNoTabs(entryMessage, statusMessage) {
			dialog_cdv = $(
					'<div id="dialog-cdv" title="' + labels.dialogTitle + '">' +
					'			<p>' + entryMessage + '</p>' +
					'			<p>' + statusMessage + '</p>' +
					'		</div>').dialog({

					dialogClass: "no-close",
					autoOpen: false,
					modal: false,
					closeOnEscape: true,
					resizable: false,
					draggable: false,
					height: "auto",
					position: {
						my: "right top",
						at: "right bottom",
						of: "#cdv_toolbar_buttons"
					},

					width: 310,
					show: {
						effect: "fold",
						duration: 200,
						direction: 'top'
					},
					hide: {
						effect: "fold",
						duration: 200,
						direction: 'right'
					},

					open: function () {
						if (dataReload){
							dataReload=false;
							var errCb = setError("tab-0");
							var getPDataList = updatePDataFields(null, null);
							cdvCORE.getInstance().cdv_getdataByConsent(getPDataList, null);
						}
						

					}

				});

			return dialog_cdv;

		}

		function showPrivacyPolicyForActivation_(url, title) {
			var $dialog = $('<div></div>')
				.load("informed_consent.html").dialog({
					autoOpen: false,
					title: "Privacy Policy",
					width: 700,
					height: 450,
					modal: true,
					buttons: [{
							text: labels.consentButton,
							click: function () {
								var confirm = activateSLR();
								cdvCORE.getInstance().createAccount(confirm);
								cdvUI.getInstance().consent_given = true;
								dialog_cdv.dialog("destroy");
								$(this).dialog("close");
							}
						}
					]
				});
			$dialog.dialog('open');
		}

		function showPrivacyPolicyForActivation(isAccount) {

			var initializeDlg = initializeConsentDlg(isAccount);
			cdvCORE.getInstance().initializeConsent(initializeDlg);
		}

		//########################################


		/*if notpresent ask consent*/

		function initializeConsentDlg(isAccount) {
			return function (consentData) {

				if (consentData) {
					console.log(JSON.stringify(consentData));
					var checkdataset = 0;
					var $dialog = $('<div id="dialog-consent"></div>');
					var mydata = consentData;
					$dialog.load(informedConsentLink, function () {

						//$(this).find('#mybody').append(labels.consentMessage1+':<br><b>-Required data:</b><br>');
						$(this).find('#mybody').append(labels.consentMessage1+'.');
						
						for (var i = 0; i < mydata.resourceSet.datasets.length; i++) {
							dataMappingstate = mydata.resourceSet.datasets[i].status;
							if (dataMappingstate == true) {
								dataMappinglength = mydata.resourceSet.datasets[i].dataMappings.length;
								checkdataset = i;
								for (var x = 0; x < dataMappinglength; x++) {
									var checkname = mydata.resourceSet.datasets[i].dataMappings[x].name;
									var checkrequired = mydata.resourceSet.datasets[i].dataMappings[x].required;
									if (checkrequired === 1) {
										$(this).find('#mybody').append('<input type="checkbox" checked disabled name=' + checkname + ' value=' + x + '>' + checkname + '</input><br>');
									}
								}
							}
						}

						//$(this).find('#mybody').append('<br><b>-Optional data:</b>'+labels.consentMessage2+'<br>')
						$(this).find('#mybody').append('<br>'+labels.consentMessage2+'<br>')
						for (var i = 0; i < mydata.resourceSet.datasets.length; i++) {
							dataMappingstate = mydata.resourceSet.datasets[i].status;
							if (dataMappingstate == true) {
								dataMappinglength = mydata.resourceSet.datasets[i].dataMappings.length;
								for (var x = 0; x < dataMappinglength; x++) {
									var checkname = mydata.resourceSet.datasets[i].dataMappings[x].name;
									var checkrequired = mydata.resourceSet.datasets[i].dataMappings[x].required;
									if (checkrequired === 0) {
										$(this).find('#mybody').append('<input type="checkbox" name="' + checkname + '" value=' + x + '><b><i>' + checkname + '</i></b></input><br>');
									}
								}
							}
						}
						$(this).find('#mybodypurpose').append('<p><br>These data will be used for: ');
						$(this).find('#mybodypurpose').append('<br><b><i>'+mydata.usageRule.purposeName+' </i><a href=\"'+mydata.usageRule.policyRef+'\" target=_blank>(more info)</a></b><br>');
						$(this).find('#mybodypurpose').append('<br>Processing type: ');
						$(this).find('#mybodypurpose').append('<br><b><i>'+mydata.usageRule.processingCategories+' </i></b><br></p>');
						
						$(this).find('#mybodythirdparty').append('<br>Below third parties will receive information about you.<br>Check the ones you agree to grant consent for the declared purpose<br>');
						
						for (var i = 0; i < mydata.usageRule.shareWith.length; i++) {
							
								    var checkrequired = mydata.usageRule.shareWith[i].required;
									var checkname = mydata.usageRule.shareWith[i].orgName;
									var orgUrl = mydata.usageRule.shareWith[i].orgUrl;
									 if (checkrequired){
										$(this).find('#mybodythirdparty').append('<input type="checkbox" checked disabled name=' + checkname + ' value=' + i + '>' + checkname + '</input><br>');
									
									 } else{
										 $(this).find('#mybodythirdparty').append('<input type="checkbox"  name=' + checkname + ' value=' + i + '>' + checkname + '</input><br>');
									
									 }
						
						}
						
						$(this).find('#mybodycontact').append('<br><p><b><i> Contact Information</i></b></p>');	
                        $(this).find('#mybodycontact').append('<br><p><b><i>Controller: </i></b>'+mydata.dataController.piiController+'</p>');
                        $(this).find('#mybodycontact').append('<br><p><b><i>Organization: </i></b>'+mydata.dataController.org+'</p>');						
						$(this).find('#mybodycontact').append('<br><p><b><i>Address: </i></b>'+mydata.dataController.address+'</p>');
                        $(this).find('#mybodycontact').append('<br><p><b><i>Email: </i></b>'+mydata.dataController.email+'</p>');
                        $(this).find('#mybodycontact').append('<br><p><b><i>Phone: </i></b>'+mydata.dataController.phone+'</p>');						
						
						$(this).find('#mybody2').append('<br>You have the right to withdraw consent at any time, but that will not affect the lawfulness of processing based on consent before its withdrawal.<br>You have the right of rectification or erasure, to restrict processing or to object to processing for the stated purpose<br><br>The data will be retained until your account on this service is active or your explicit request of erasure.');

					});

					$dialog.dialog({
						autoOpen: false,
						title: "Data Consent",
						width: 800,
						height: 450,
						modal: true,
						buttons: [{
								text: labels.consentButton,
								close: function() {console.log("close");$("#overlay").hide(); },
								click: function () {
                                  if (/*($(this).find('#anonymous_yes').is(':checked'))||($(this).find('#anonymous_no').is(':checked'))*/ true){
									  
										if ($(this).find('#anonymous_yes').is(':checked')) {
												mydata.resourceSet.anonymousUsage = "true";
											} else if ($(this).find('#anonymous_no').is(':checked')) {
												mydata.resourceSet.anonymousUsage = "false";
											}
											var dataToRemove = [];
											$( "#mybody" ).find('input[type=checkbox]:not(:checked)').each(function (index) {
												dataToRemove.push($(this).val());
											});
											var thirdPartyToRemove = [];
											$( "#mybodythirdparty" ).find('input[type=checkbox]:not(:checked)').each(function (index) {
												thirdPartyToRemove.push($(this).val());
											});
											for (var i = dataToRemove.length - 1; i >= 0; i--)
												mydata.resourceSet.datasets[checkdataset].dataMappings.splice(dataToRemove[i], 1);
											for (var k = thirdPartyToRemove.length - 1; k >= 0; k--)
												mydata.usageRule.shareWith.splice(thirdPartyToRemove[k], 1)
											console.log("jsonforconsentform: " + JSON.stringify(mydata));
											this.consent_given = true;
											
											if(localStorage.accountData){
												var activate = activateSLR(mydata);
												activate(true);
											}
											else{
												var confirm = activateSLR(mydata);
												cdvCORE.getInstance().createAccount(confirm);
											}
											
											dialog_cdv.dialog("destroy");
											$(this).dialog("close");
										
								  } else{
									  var $dialog_check = $('<div>'+labels.statusMessageAnonymousSelection+'</div>');
									  $dialog_check.dialog();
									  
								  }
								
								}
							}
						]
					});
					
					$dialog.dialog('open');

				}
			}
		}

		//################################


		return {
			// Public definitions
			init: initComponent, // Called only one time
			enable: enableComponentFeatures, // Called when the Component button is enabled
			disable: disableComponentFeatures, // Called when the Component button is disabled or another one enabled
			isEnabled: function () {
				return featureEnabled;
			}, // Returns if the feature is enabled

			//paragraphEvent: paragraphEvent
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

function confirmRemoveAccount() {

	var dialog_saved = $(
			'<div id="dialog-message" title="' + 'Remove Account' + '">' +
			'			<p>' + 'Are you sure to remove your account?' + '</p>' +
			'			<p>' + 'Your data will be permanently deleted and cannot be recovered. Remember to save your data first.' + '</p>' +
			'			<p>' + 'Press \'OK\' to continue... ' + '</p>' +
			'</div>').dialog({
			modal: true,
			buttons: {
				Ok: function () {

					cdvCORE.getInstance().removeCDV();
					cdvUI.getInstance().disable();
					$('#dialog_cdv').dialog("destroy");
					$('#dialog_cdv').dialog("open");

					$(this).dialog("close");

				},
				Cancel: function () {
					$(this).dialog("close");

				}

			}

		});
	dialog_saved.dialog("open");

}


function confirmConsent() {

var $dialog_consent_confirm = $('<div>Confirm your Consent?</div>');
$dialog_consent_confirm.dialog({
	  resizable: false,
	  width: 400,
	  modal: true,
	  buttons: {
		"Confirm": function() {
		 return true;
		},
		Cancel: function() {
		  return false;
		}
	  }
	});
	
	
	
}







function toggleDialog() {

	if ($('#dialog-cdv').dialog('isOpen') === true) {
		$('#dialog-cdv').dialog("close");
		
	} else {
		dataReload=false;
		$('#dialog-cdv').dialog("open");
	}
}

function showPrivacyPolicy(url, title) {
	var $dialog = $('<div></div>')
		.load(cdvUI.getInstance().informedConsentLink).dialog({
			autoOpen: false,
			title: "Privacy Policy",
			width: 700,
			height: 350,
			modal: true,
			buttons: [{
					text: "Consent",
					click: function () {
						cdvUI.getInstance().consent_given = true;
						$(this).dialog("close");

					}
				}
			]
		});
	$dialog.dialog('open');

}