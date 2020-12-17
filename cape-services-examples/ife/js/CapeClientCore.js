/*******************************************************************************
 * CaPe - a Consent Based Personal Data Suite
 *   Copyright (C) 2019 Engineering Ingegneria Informatica S.p.A.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/

/** CaPe SDK - Core Client
* -----------------------------------------------------------------------------
* This JavaScript contains the client side of the CaPe SDK
* related to form fill features. The main functionality is to
* create the calls to the server side of the CaPe SDK.
* Used by cape-ui-popup.js
* -----------------------------------------------------------------------------
**/

import PData from './PData.js';

export default class CapeClientCore {


    static instance;
    static no_account_message = "Sorry! No CaPe Account Associated to You! Create?";


    constructor() {
        if (CapeClientCore.instance)
            return CapeClientCore.instance;

        CapeClientCore.instance = this;

        // Properties initialization
        this.environment = {};

        this.idmHost = '';
        this.capeHost = '';
        this.dataFields = [];

        this.serviceId = '';
        this.serviceName = '';
        this.serviceUrl = '';
        this.serviceApiUrl = '';

        //this.serviceLink = '';
        //this.serviceLinkToken = '';
        //this.serviceLinkStatus = '';

        var username = '';
        var capeDashboardURL = "/cape-dashboard/";

        var serviceLinkUrl = "localhost:8080";

        return CapeClientCore.instance;
    }

    static getInstance = () => {
        return new CapeClientCore();
    }

    init = () => {

        console.log("Initializing CaPe Client Core")

        /* Load configuration file*/
        return jQuery.ajax({
            url: './config.json',
            type: 'GET',
            dataType: 'json',
            success: (data) => {

                /* CaPe Parameters initialization **/
                this.environment.system = data.system;
                this.environment.service = data.service;
                this.environment.purposes = [];

                if (this.environment.system.idmHost)
                    this.idmHost = this.environment.system.idmHost;

                if (this.environment.system.capeHost)
                    this.capeHost = this.environment.system.capeHost;


                /* Service Parameters initialization **/
                if (this.environment.service.serviceUrl)
                    this.serviceUrl = this.environment.service.serviceUrl;

                if (this.environment.service.serviceApiUrl)
                    this.serviceApiUrl = this.environment.service.serviceApiUrl;

                if (this.environment.service.serviceId)
                    this.serviceId = this.environment.service.serviceId;

                if (this.environment.service.serviceName)
                    this.serviceName = this.environment.service.serviceName;

                /* SubServices Parameters initialization **/
                if (this.environment.service.purposes)
                    this.environment.purposes = this.environment.service.purposes;

                /* Misc ? */
                if (this.environment.service.dataFields)
                    dataFields = this.environment.service.dataFields;


            },
            error: (err) => {
                console.log(err);
            }
        });
    }


    openCape = (cdvDashUrl) => {
        window.open(cdvDashUrl, "_blank");
    }


    getPData = (updatePDataFields, errorCallback) => {

        console.log(`SERVICEID: ${this.serviceId}`);

        var url = `${this.capeHost}/pdata-manager/api/v1/getPData`;
        var userData = JSON.parse(localStorage.userData || 'null');
        var token = localStorage.tokenData;

        var pdata = new PData(userData.userId, serviceLink, serviceLinkToken);

        if (userData && token)
            $.ajax({
                url: url,
                type: 'POST',
                data: pdata.toJsonString(),
                contentType: "application/json; charset=utf-8",
                dataType: 'json',
                success: ((response) => {
                    console.log(JSON.stringify(response));
                    updatePDataFields(response);

                }),
                error: (jqxhr) => {
                    console.log(`${jqxhr.responseJSON.statusCode}: ${jqxhr.responseJSON.title}, ${jqxhr.responseJSON.message}`);

                },
                beforeSend: (xhr) => {
                    xhr.setRequestHeader('Authorization', 'Bearer ' + token);
                }
            });
    }


    postPData = () => {

        var userData = JSON.parse(localStorage.userData || 'null');
        var token = localStorage.tokenData;


        var pData = formFieldsToPData(serviceLink, serviceLinkToken, userData.userId, dataFields);
        if (userData && token)
            $.ajax({
                url: `${this.capeHost}/pdata-manager/api/v1/postPData?mode=append`,
                type: 'POST',
                data: pData.toJsonString(),
                contentType: "application/json; charset=utf-8",
                success: (resp) => {
                    console.log("PData saved!");
                    //callback(true);

                },
                error: (jqxhr) => {
                    console.log(`${jqxhr.responseJSON.statusCode}: ${jqxhr.responseJSON.title}, ${jqxhr.responseJSON.message}`);
                },
                beforeSend: (xhr) => {
                    xhr.setRequestHeader('Authorization', 'Bearer ' + token);
                }
            });
    }


    createSRG = async (service, sendDescriptionToCape) => {

        try {
            var embeddedServiceDescr = await jQuery.ajax({
                url: service.serviceDescPath,
                type: 'GET',
                dataType: 'json'
            });

            if (sendDescriptionToCape) {
                var resp = await $.ajax({
                    url: `${this.capeHost}${this.environment.system.serviceRegistryEndpoint}`,
                    type: 'POST',
                    data: JSON.stringify(embeddedServiceDescr),
                    contentType: 'application/json'
                });

                console.log("Service Description saved!");
                service.serviceId = resp.serviceId;
                service.serviceUrl = resp.identifier;
                service.serviceName = resp.name;

            } else {
                service.serviceId = embeddedServiceDescr.serviceId;
                service.serviceUrl = embeddedServiceDescr.identifier;
                service.serviceName = embeddedServiceDescr.name;
            }

        } catch (err) {
            console.log(`${err.responseJSON.statusCode}: ${err.responseJSON.title}, ${err.responseJSON.message}`);
        }
    }


    getSRG = async (serviceId) => {

        return await $.ajax({
            url: `${this.capeHost}${this.environment.system.serviceRegistryEndpoint}${serviceId}`,
            type: 'GET',
            error: function (jqxhr) {
                console.log(`${jqxhr.responseJSON.statusCode}: ${jqxhr.responseJSON.title}, ${jqxhr.responseJSON.message}`);
            },
            beforeSend: (xhr) => {
                xhr.setRequestHeader('Authorization', 'Bearer ' + localStorage.tokenData);
            }
        });
    }

    initializeSRG = async (service) => {

        try {

            var serviceDescr = {};

            if (service.getDescriptionFromCape)
                serviceDescr = await this.getSRG(service.serviceId);

            if (serviceDescr.serviceId == null) {
                console.log("Service Description does not exist, adding it to repo");
                await this.createSRG(service, service.sendDescriptionToCape);

            } else {
                console.log(`Service Description received for service Id: ${service.serviceId}`);
                console.log("Initialize SRG: Service description exists in registry, let's initialize SLR");
                service.serviceId = serviceDescr.serviceId;
                service.serviceUrl = serviceDescr.identifier;
                service.serviceName = serviceDescr.name;
            }

            await this.initializeSLR(service);

        } catch (err) {
            console.log(`${err.responseJSON.statusCode}: ${err.responseJSON.title}, ${err.responseJSON.message}`);
        }

    }


    initializeSLR = async (service) => {

        var userData = JSON.parse(localStorage.userData || 'null');
        var token = localStorage.tokenData;

        if (userData && token)
            try {
                var slr = await $.ajax({
                    url: `${this.capeHost}/account-manager/api/v1/users/${userData.userId}/services/${service.serviceId}/serviceLink`,
                    type: 'GET',
                    contentType: "application/json; charset=utf-8",
                    dataType: 'json',
                    beforeSend: (xhr) => {
                        xhr.setRequestHeader('Authorization', 'Bearer ' + token);
                    }
                });

                console.log(`SLR exists for Service ID: ${service.serviceId}, with id: ${slr._id}`);
                localStorage.slrData = JSON.stringify(slr);

                service.serviceLink = slr._id;
                service.serviceLinkToken = slr.slrToken;

            } catch (jqxhr) {
                console.log(`${jqxhr.responseJSON.statusCode}: ${jqxhr.responseJSON.title}, ${jqxhr.responseJSON.message}`);
                console.log("SLR does not exist");
                await this.createSLR(service);
            }

        else return Promise.resolve();
    }




    /**
     * Initialize a CaPe account. Check if there is an existing account with the user Id (at the service, is retrieved from the login with the IdM)
     * If the Account exists, start to creare the Service Description
     **/
    initializeAccount = async () => {

        var userData = JSON.parse(localStorage.userData || 'null');
        var token = localStorage.tokenData;

        if (userData && token) {

            try {
                var exists = await $.ajax({
                    url: `${this.capeHost}${this.environment.system.accountEndpoint}accounts/${userData.userId}/exists`,
                    type: 'GET',
                    contentType: "application/json; charset=utf-8",
                    beforeSend: (xhr) => xhr.setRequestHeader('Authorization', 'Bearer ' + token)
                });

                if (exists.result == true) {
                    console.log(`CaPe Account exists for user: ${userData.userId}`);
                    localStorage.accountData = userData.userId;

                    await this.initializeSRG(this.environment.service);

                    await this.initializeConsent(this);
                }

            } catch (jqxhr) {
                if (jqxhr.status == 404) {

                    console.log(`${jqxhr.responseJSON.statusCode}: ${jqxhr.responseJSON.title}, ${jqxhr.responseJSON.message}`);
                    if (confirm(CapeClientCore.no_account_message)) {
                        await this.createAccount();

                        await this.initializeSRG(this.environment.service);

                        await this.initializeConsent(this);

                    } else {
                        //nothing todo for now
                    }
                } else if (jqxhr.status == 401) {
                    window.location.href = this.environment.service.serviceUrl;
                } else {
                    console.log(`${jqxhr.responseJSON.statusCode}: ${jqxhr.responseJSON.title}, ${jqxhr.responseJSON.message}`);
                }
            }

        } else return Promise.resolve();
    }


    /*if notpresent ask consent*/
    initializeConsent = async (capeClientInstance) => {

        var purposeId = this.environment.service.defaultPurposeId;

        if (window.location.href.indexOf("services_activity.html") > -1) {
            purposeId = "1";
            localStorage.setItem("serviceactivity", "value");
            localStorage.removeItem("servicediet");
        }

        if (window.location.href.indexOf("services_diet.html") > -1) {
            purposeId = "2";
            localStorage.setItem("servicediet", "value");
            localStorage.removeItem("serviceactivity");
        }

        localStorage.setItem("purposeId", purposeId);
        var userData = JSON.parse(localStorage.userData || 'null');

        /* ***********************************************
         * 
         * Fetch Consent form for dinamic modal content 
         * 
         * ************************************************/

        let purposes = this.environment.service.purposes;

        try {
            var consentForm = await $.ajax({
                type: "GET",
                url: `${this.capeHost}${this.environment.system.consentEndpoint}fetchConsentForm/${userData.userId}/${this.environment.service.serviceId}/${this.environment.service.sourceServiceId}/${purposeId}/${this.environment.service.sourceDatasetId}`,
                contentType: "application/json; charset=utf-8",
                beforeSend: function (xhr) {
                    xhr.setRequestHeader('Authorization', 'Bearer ' + localStorage.tokenData);
                }
            });


            localStorage.setItem('sinkId', consentForm.sinkId);
            let count = 1;
            $('input:radio').each(function () {
                if ($(this).prop('checked')) {
                    //var $this = $(this);
                    //id = $this.attr('id');
                    //value = $this.attr('value');
                    count++;
                }
            });

            if (count == 0) {
            } else {

                console.log(consentForm);
                var checkdataset;
                $("#exampleModal").on("shown.bs.modal", function () {

                    $(this).find('#mybody').empty();
                    $(this).find('#mybody').append('The Health@Home application requires consent to access the following personal data:<br><b>-Required data:</b> data that are necessary to execute the service.<br>');

                    for (let [index, dataset] of consentForm.resourceSet.dataset.entries()) {

                        if (dataset.status == true) {
                            checkdataset = index;
                            for (let [index, dataMapping] of dataset.dataMapping.entries()) {

                                let checkname = dataMapping.name;
                                if (dataMapping.required === 1)
                                    $('#mybody').append('<input type="checkbox" checked disabled name=' + checkname + ' value=' + index + '>' + checkname + '</input><br>');
                            }
                        }
                    }

                    if (purposeId == purposes[0].purposeId) {
                        $(this).find('#mybody').append('This data will be used to provide personalized physical activity advice. <br> The data will be visible to a personal trainer.<br>The data will be automatically processed to be graphically plotted to the recipient, in order to facilitate readability and service provision.<br>')
                    }
                    if (purposeId == purposes[1].purposeId) {
                        $(this).find('#mybody').append('This data will be used to provide personalized diet advice. <br> The data will be visible to a nutritionist.<br>The data will be automatically processed to be graphically plotted to the recipient, in order to facilitate readability and service provision.<br>')
                    }

                    $(this).find('#mybody').append('<br><b>-Optional data:</b> data that are not strictly necessary to execute the service. Check the ones you agree to grant consent for the declared purpose.<br>')
                    for (let dataset of consentForm.resourceSet.dataset) {

                        if (dataset.status == true) {
                            for (let [index, dataMapping] of dataset.dataMapping.entries()) {
                                let checkname = dataMapping.name;
                                if (dataMapping.required === 0) {
                                    $('#mybody').append('<input type="checkbox" name=' + checkname + ' value=' + index + '>' + checkname + '</input><br>');
                                }
                            }
                        }
                    }

                    $(this).find('#mybody2').html('<br>You have the right to withdraw consent at any time, but that will not affect the lawfulness of processing based on consent before its withdrawal.<br>You have the right of rectification or erasure, to restrict processing or to object to processing and to lodge a complaint to a supervisory authority.<br>The data will be retained until your account on this service is active.<br>');

                }).modal('show');

                /* *************************************************
                 * Handler for clicking YES in the Consent Form 
                 * 
                 * ************************************************/

                if ($('#btnYes').click(async () => {
                    $('.loader').css('display', 'block').css('z-index', '9999');
                    $('input[type=checkbox]:checked').each(function (index) {
                        var count = $(this).val();
                        if (count == "yes") {
                            //return false;	
                        } else {
                            //mydata.resourceSet.dataset[checkdataset].dataMapping[count].required=1;
                        }
                    });

                    //Controllo se la check anonymous è selezionata
                    if ($('#anonymous_yes').is(':checked')) {
                        consentForm.resourceSet.anonymousUsage = "true";
                    } else if ($('#anonymous_no').is(':checked')) {
                        consentForm.resourceSet.anonymousUsage = "false";
                    }
                    $('input[type=checkbox]:not(:checked)').each(function (index) {
                        if ($(this).val() == "yes") {
                        } else {
                                consentForm.resourceSet.dataset[checkdataset].dataMapping.splice($(this).val(), 1);
                        }
                    });


                    /******************************************************
                     * Give Consent after User accepted the Consent Form
                     *  and then Redirect in the page corresponding to the selected Purpose
                     * ****************************************************/
                    try {
                        var giveConsentResponse = await $.ajax({
                            type: "POST",
                            url: `${capeClientInstance.capeHost}/consent-manager/api/v1/giveConsent/${userData.userId}`,
                            contentType: "application/json; charset=utf-8",
                            data: JSON.stringify(consentForm),
                            beforeSend: (xhr) => {
                                xhr.setRequestHeader('Authorization', 'Bearer ' + localStorage.tokenData);
                            }
                        });

                        $(".loader").css("display", "none");

                        await capeClientInstance.storeCSR(giveConsentResponse.source_consent_record, capeClientInstance.environment.service.sourceServiceId, capeClientInstance.environment.service.sourceDatasetId);

                        let url = '';
                        if (purposeId == "1")
                            url = capeClientInstance.environment.service.purposes[0].serviceUrl;

                        if (purposeId == "2")
                            url = capeClientInstance.environment.service.purposes[1].serviceUrl;

                        window.opener.location.href = url;
                        window.close();

                    } catch (jqxhr) {
                        console.log(`${jqxhr.responseJSON.statusCode}: ${jqxhr.responseJSON.title}, ${jqxhr.responseJSON.message}`);
                        alert(`${jqxhr.responseJSON.statusCode}: ${jqxhr.responseJSON.title}, ${jqxhr.responseJSON.message}`);
                    }

                }));
                /* *************************************************
              * Handler for clicking NO in the Consent Form 
              * 
              * ************************************************/

                if ($('#btnNo').click(function () {
                    $('#exampleModal').find('#mybody').empty();
                }));

            }
        } catch (jqxhr) {
            console.log(`${jqxhr.responseJSON.statusCode}: ${jqxhr.responseJSON.title}, ${jqxhr.responseJSON.message}`);
        }
    }



    approveConsent = async () => {

        console.log("Consent ok! create source")
        var sink_consent_record = createSinkConsentRecord(JSON.parse(sink))
        var source_consent_record = createSourceConsentRecord(JSON.parse(source))
        var token = localStorage.tokenData;
        var data = JSON.parse(localStorage.userData || 'null');

        try {

            var sinkResponse = await $.ajax({
                url: `${this.capeHost}/account-manager/api/v1/accounts/${data.userId}/consentRecord`,
                type: 'POST',
                contentType: "application/json; charset=utf-8",
                data: JSON.stringify(sink_consent_record),
                beforeSend: function (xhr) {
                    xhr.setRequestHeader('Authorization', 'Bearer ' + token);
                }
            });

            console.log("sink record saved");
            console.log(JSON.stringify(sinkResponse));

            var sourceResponse = await $.ajax({
                url: url,
                type: 'POST',
                contentType: "application/json; charset=utf-8",
                data: JSON.stringify(source_consent_record),
                beforeSend: function (xhr) {
                    xhr.setRequestHeader('Authorization', 'Bearer ' + token);
                }
            });

            console.log("source record saved");
            console.log(JSON.stringify(sourceResponse))
            this.dataConnection();

        } catch (jqxhr) {
            console.log(`${jqxhr.responseJSON.statusCode}: ${jqxhr.responseJSON.title}, ${jqxhr.responseJSON.message}`);
        }

    }

    dataConnection = () => {

        var data = JSON.parse(localStorage.userData || 'null');

        //var tokenData = JSON.parse(localStorage.tokenData || 'null');

        var params = {}

        params.slr_id = JSON.parse(localStorage.slrData)._id;
        params.user_id = JSON.parse(localStorage.slrData).surrogateId;
        params.slr_token = JSON.parse(localStorage.slrData).slrToken;
        params.properties = [];

        var dataMapping = JSON.parse(localStorage.serviceData).serviceDataDescription.dataset.dataMapping;
        jQuery.each(dataMapping, function (i, item) {
            params.properties[i] = item.property;
        });

        console.log(params);

        $.ajax({
            url: `${this.capeHost}/pdata-manager/api/internal/getPData`,
            type: 'POST',
            data: JSON.stringify(params),
            contentType: "application/json; charset=utf-8",
            success: function (resp) {
                console.log("data connection ok");
                console.log(resp);
                //var inputs = jQuery("form#gymForm :input");
                var data = resp.properties;
                jQuery.each(data, function (i, item) {
                    console.log(item.key + "  " + item.values)
                    item.key = item.key.replace(/\./g, '\\.')
                    jQuery("#" + item.key).val(item.values[0])
                });
                jQuery('#approveModal').modal('hide');
            },
            error: function (jqxhr) {
                console.log(`${jqxhr.responseJSON.statusCode}: ${jqxhr.responseJSON.title}, ${jqxhr.responseJSON.message}`);
                //						callback(false);
            }
            /*,
            beforeSend: function (xhr) {
                //xhr.setRequestHeader('Authorization', 'Bearer ' + tokenData.access_token);
     
            }*/

        });

    }


    handleToken = () => {

        var params = processAuthParams(decodeURIComponent(document.location.href));
        var code = params['code'];
        console.log(`code: ${code}`);
        jQuery.ajax({
            url: `${this.capeHost}/account-manager/api/v1/login`,
            contentType: 'application/x-www-form-urlencoded',
            type: 'POST',
            data: `code=${code}`,
            success: (data) => {

                localStorage.setItem('tokenData', data.token);
                delete data['token'];
                console.log("token: " + localStorage.tokenData);

                data["surname"] = "";
                data["socialId"] = "";

                localStorage.setItem('userData', JSON.stringify(data));
                console.log(data);
                $(".wrapper").show();
                $(".loader").hide();

                if (callback)
                    callback();
            },
            error: (err) => {
                console.log(err);
            },
            beforeSend: (xhr) => {
                //xhr.setRequestHeader ("Access-Control-Allow-Origin", "*");
            }
        });
    }

    //getProfile = () => {

    //    jQuery.ajax({
    //        url: `${this.idmHost}/user?access_token=${localStorage.tokenData}`,
    //        contentType: "application/json; charset=utf-8",
    //        dataType: 'json',
    //        success: function (data) {
    //            data["userId"] = data.id;
    //            data["name"] = data.displayName;
    //            data["surname"] = "";
    //            data["socialId"] = "";
    //            console.log("profile check");
    //            localStorage.setItem('userData', JSON.stringify(data));
    //            console.log("get profile ok, verifyng account");
    //            this.initializeAccount();//verifico se l'account esiste
    //        },
    //        error: function (err) {
    //            console.log(err);
    //        },
    //    });

    //}


    createAccount = async () => {

        var userData = JSON.parse(localStorage.userData || 'null');
        var account = this.accountToJSON(userData.userId, userData.name, userData.surname);
        var token = localStorage.tokenData;

        if (userData && token)
            $.ajax({
                url: `${this.capeHost}${this.environment.system.accountEndpoint}accounts`,
                type: 'POST',
                data: account,
                contentType: "application/json; charset=utf-8",
                success: (resp) => {
                    console.log(`CaPe Account for User Id: ${userData.userId} created`);
                    localStorage.accountData = resp.username;

                },
                error: (jqxhr) => {
                    if (jqxhr.status == 409) {
                        console.log(`CaPe Account for User Id: ${userData.userId} is already present`);
                        localStorage.accountData = userData.userId;

                    } else
                        console.log(`${jqxhr.responseJSON.statusCode}: ${jqxhr.responseJSON.title}, ${jqxhr.responseJSON.message}`);
                    //callback(false);
                },
                beforeSend: (xhr) => {
                    xhr.setRequestHeader('Authorization', 'Bearer ' + token);
                }
            });

    }



    createSLR = async (service) => {

        var userData = JSON.parse(localStorage.userData || 'null');
        var token = localStorage.tokenData;

        var slr = this.slrToJSON(`${userData.userId}`, service.serviceId, service.serviceUrl, service.serviceName);
        console.log(slr);

        if (userData && token)
            return await $.ajax({
                url: `${this.capeHost}/account-manager/api/v1/accounts/${userData.userId}/serviceLinks`,
                type: 'POST',
                data: slr,
                contentType: "application/json; charset=utf-8",
                success: (resp, textStatus, jqxhr) => {

                    service.serviceLink = resp._id;
                    service.serviceLinkToken = resp.slrToken;
                    service.serviceLinkStatus = resp.serviceLinkStatusRecords;

                    if (jqxhr.status == 201) {
                        console.log("SLR Created in CaPe, storing in the Service Storage");
                        this.storeSLR(service.serviceLinkToken, service.serviceLinkStatus);
                    } else {
                        console.log("SLR retrieved from CaPe, it was already present");
                    }

                    console.log(JSON.stringify(resp));
                    localStorage.slrData = JSON.stringify(resp);

                },
                error: (jqxhr) => {
                    console.log(`${jqxhr.responseJSON.statusCode}: ${jqxhr.responseJSON.title}, ${jqxhr.responseJSON.message}`);
                },
                beforeSend: (xhr) => {
                    xhr.setRequestHeader('Authorization', 'Bearer ' + token);
                }
            });

        else return Promise.reject();

    }

    storeSLR = (slr, ssr) => {

        $.ajax({
            url: `${this.serviceApiUrl}/api/v1/slr/`,
            type: 'POST',
            data: JSON.stringify({ "slr": slr, "ssr": ssr }),
            contentType: "application/json; charset=utf-8",
            success: (resp) => {

                console.log(JSON.stringify(resp));
                if (resp['result'] == 'ok') {
                    console.log("SLR stored!");
                    //serviceLink = resp._id;
                    //serviceLinkToken = resp.slrToken;
                    //serviceLinkStatus= resp.serviceLinkStatusRecords;

                    //instance.storeSLR(serviceLinkToken,serviceLinkStatus);
                    //this.initializeConsent(this.capeHost);
                } else {
                    alert('Error while storing SLR in the Service Storage');
                }

            },
            error: (jqxhr) => {
                console.log(`${jqxhr.status}: ${jqxhr.statusText}`);
                alert(`Error while storing SLR in the Service Storage: ${jqxhr.status}: ${jqxhr.statusText} - ${jqxhr.responseText}`);
            }//,
            //beforeSend: (xhr) => {
            //	xhr.setRequestHeader('Authorization', 'Bearer ' + tokenData.access_token);
            //}
        });

    }


    storeCSR = async (csr, sourceId, datasetId) => {

        try {
            var requestResult = await $.ajax({
                url: `${this.serviceApiUrl}/api/v1/csr/`,
                type: 'POST',
                data: JSON.stringify({
                    "csr": csr,
                    "source_id": sourceId,
                    "dataset_id": datasetId
                }),
                contentType: "application/json; charset=utf-8"
            });


            console.log(JSON.stringify(requestResult));
            if (requestResult.result == 'ok') {
                console.log("CSR stored!");
            } else {
                alert('Error while storing CSR in the Service Storage');
            }

        } catch (jqxhr) {
            console.log(`${jqxhr.status}: ${jqxhr.statusText}`);
            alert(`Error while storing CSR in the Service Storage: ${jqxhr.status}: ${jqxhr.statusText} - ${jqxhr.responseText}`);
        }

    }




    openForm = () => {

        console.log("Account created");
        window.opener.document.location.href = `${this.serviceUrl}/registration_form.php`;
        window.close();
    }


    exportData = () => {

        var userData = JSON.parse(localStorage.userData || 'null');

        $.ajax({
            url: `${this.capeHost}/pdata-manager/api/v1/pData/download?fileFormat=CSV`,
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
            error: function (jqxh) {
                console.log(`${jqxhr.responseJSON.statusCode}: ${jqxhr.responseJSON.title}, ${jqxhr.responseJSON.message}`);
            },
            beforeSend: function (xhr) {
                xhr.setRequestHeader('Authorization', 'Bearer ' + localStorage.tokenData);
                xhr.setRequestHeader('accountId', userData.userId);
            }
        });
    }


    removeCDV = () => {

        var userData = JSON.parse(localStorage.userData || 'null');

        $.ajax({
            url: `${this.capeHost}/account-manager/api/v1/accounts/${userData.userId}`,
            type: 'DELETE',
            contentType: "application/json; charset=utf-8",
            success: function (json) {
                console.log(json);
            },
            error: function (jqxhr) {
                console.log(`${jqxhr.responseJSON.statusCode}: ${jqxhr.responseJSON.title}, ${jqxhr.responseJSON.message}`);
            },
            beforeSend: function (xhr) {
                xhr.setRequestHeader('Authorization', 'Bearer ' + localStorage.tokenData);
            }
        });

    }

    // Helper function to serialize all the form fields into a JSON string
    formFieldsToPData = (slrId, slrToken, userId, fields) => {

        var properties = [];

        for (let field of fields) {

            let propertyField = field.replace(/(:|\.|\[|\]|,|=|@)/g, "\\$1");
            let propertyValue = $('#' + propertyField).val();

            console.log(`${propertyField} -${propertyValue}`);

            if ($('#' + propertyField).val())
                properties.push({
                    "key": field,
                    "values": [propertyValue]
                });
        }

        return new PData(userId, slrId, slrToken, properties);

    }

    // Helper function to serialize all account fields into a JSON string
    accountToJSON = (userId, firstname, lastname) => {

        return JSON.stringify({
            //"username": firstname + "." + lastname + userId + serviceID
            "username": userId,
            "particular": {
                "firstname": firstname,
                "lastname": lastname
            }
        });
    }

    // Helper function to serialize all slr fields into a JSON string
    slrToJSON = (userId, serviceId, serviceURL, serviceName) => {

        return JSON.stringify({
            "serviceId": serviceId,
            "serviceUri": serviceURL,
            "userId": userId,
            "serviceName": serviceName
        });
    }


    setFieldValue = (target, value) => {
        $('#' + target).val(value).focus();
        $('#' + target).css({
            'border-width': '2px'
        });
    }


    createSinkConsentRecord = (record) => {

        var slrData = JSON.parse(localStorage.slrData || 'null');
        var serviceData = JSON.parse(localStorage.serviceData || 'null');

        record.common_part.version = "";
        record.common_part.cr_id = "";
        record.common_part.surrogate_id = slrData.surrogateId;

        record.common_part.rs_description.resource_set.rs_id = serviceData.serviceId;

        record.common_part.rs_description.resource_set.dataset[0] = createDataSet(serviceData);


        record.common_part.slr_id = slrData._id;
        record.common_part.iat = Date.now();
        record.common_part.operator = "_cape";
        record.common_part.subject_id = "";
        record.common_part.role = "sink";
        record.consent_receipt_part.ki_cr = {};
        record.extension_part.extensions = {};

        console.log(JSON.stringify(record));

        return record;

    }

    createSourceConsentRecord = (record) => {

        var slrData = JSON.parse(localStorage.slrData || 'null');
        var serviceData = JSON.parse(localStorage.serviceData || 'null');

        record.common_part.version = "";
        record.common_part.cr_id = "";
        record.common_part.surrogate_id = slrData.surrogateId;
        record.common_part.slr_id = slrData._id;
        record.common_part.rs_description.resource_set.rs_id = serviceData.serviceId;
        record.common_part.rs_description.resource_set.dataset = [];


        record.common_part.rs_description.resource_set.dataset[0] = createDataSet(serviceData);

        record.common_part.iat = Date.now();
        record.common_part.operator = "_cape";
        record.common_part.subject_id = "";
        record.common_part.role = "source";
        record.role_specific_part.pop_key.jwk = "aiJkR8xWSH37EWae0kkuYK4GOxv0h4OVEI0JKFUMWcU75CQQwiQMkbjbJQ46Itd";
        record.role_specific_part.token_issuer_key.jwk = "aiJkR8xWSH37EWae0kkuYK4GOxv0h4OVEI0JKFUMWcU75CQQwiQMkbjbJQ46Itd";
        record.consent_receipt_part.ki_cr = {};
        record.extension_part.extensions = {};

        console.log(JSON.stringify(record));
        return record;

    }


    createDataSet = (serviceData) => {

        var ds = {};
        ds.contactPoint = serviceData.serviceDataDescription.dataset.contactPoint;
        ds.description = serviceData.serviceDataDescription.dataset.description;
        ds.issued = serviceData.serviceDataDescription.dataset.issued;
        ds.keyword = serviceData.serviceDataDescription.dataset.keyword;
        ds.purpose = serviceData.serviceDataDescription.dataset.purpose;
        ds.language = serviceData.serviceDataDescription.dataset.language;
        ds.modified = serviceData.serviceDataDescription.dataset.modified;
        ds.publisher = serviceData.serviceDataDescription.dataset.publisher;
        ds.serviceDataType = serviceData.serviceDataDescription.dataset.serviceDataType;
        ds.spatial = serviceData.serviceDataDescription.dataset.spatial;
        ds.title = serviceData.serviceDataDescription.dataset.title;
        ds.dataStructureSpecification = serviceData.serviceDataDescription.dataset.dataStructureSpecification;

        ds.dataMapping = [];
        var mapping = serviceData.serviceDataDescription.dataset.dataMapping;

        jQuery.each(mapping, function (i, item) {
            console.log(item);
            console.log(i);
            ds.dataMapping[i] = item;
        });

        return ds;
    }


}

