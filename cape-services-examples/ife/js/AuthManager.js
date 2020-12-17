
const processAuthParams = (input) => {
    var params = {},
        queryString = input;
    var regex = /([^?&=]+)=([^&#]*)/g;
    while (m = regex.exec(queryString)) {
        params[m[1]] = m[2];
    }
    return params;
}

class AuthManager {

    static instance;
    static userdataElementID = 'simp-usr-data';

    constructor() {
        if (AuthManager.instance)
            return AuthManager.instance;

        AuthManager.instance = this;

        // Properties initialization
        this.environment = {};
        this.featureEnabled = true;
        this.greeting = '';
        this.no_account_message = "Sorry! No CDV Account Associated to You! Create?";

        return AuthManager.instance;
    }

    static getInstance = () => {
        return new AuthManager();
    }

    // It uses the log component to register the produced events
    logger = (event, details) => {
        var nop = function () { };
        if (logCORE != null) return logCORE.getInstance().ifeLogger;
        else return {
            sessionStart: nop,
            sessionEnd: nop,
            formStart: nop,
            formEnd: nop
        };
    };


    init = () => {

        console.log("Initializing IDM Auth Component")

        /* Load configuration file*/
        return jQuery.ajax({
            url: 'config.json',
            type: 'GET',
            dataType: 'json',
            success: (data) => {
                this.environment = data.system;
                this.environment.service = data.service;
            },
            error: (err) => {
                console.log(err);
            }
        });
    };


    enableWithState = (state) => {

        if (!state)
            state = this.environment.state;

        var basePath = window.location.href.split("/");

        var url = `${this.environment.idmHost}/oauth2/authorize?response_type=code&client_id=${this.environment.clientId}&state=${state}&redirect_uri=${basePath[0]}//${basePath[2]}${this.environment.loginPopupUrl}`;

        var win = window.open(url, 'AuthPopup', 'width=1024,height=768,resizable=true,scrollbars=true,status=true');
        window.addEventListener('message', function (event) {
            console.log(JSON.stringify(event.data));
            console.log("logged");
        }, false);
    };

    enable = () => {
        this.enableWithState();
    }


    token = async () => {

        try {
            var data = await this.postLogin();

            localStorage.setItem('tokenData', data.token);
            delete data['token'];
            console.log("token: " + localStorage.tokenData);

            data["surname"] = "";
            data["socialId"] = "";

            localStorage.setItem('userData', JSON.stringify(data));
            console.log(data);
            $(".wrapper").show();
            $(".loader").hide();
            return Promise.resolve();
        } catch (err) {
            console.log(err);
            return Promise.reject();
        }
    }

    postLogin = async () => {

        var params = processAuthParams(decodeURIComponent(document.location.href));
        var code = params['code'];
        console.log(`code: ${code}`);
        return await jQuery.ajax({
            url: `${this.environment.service.serviceSdkUrl}/api/v1/login`,
            contentType: 'application/x-www-form-urlencoded',
            type: 'POST',
            data: `code=${code}`
        });
    }

    // attach login flow to the sign-in button
    disable = (event) => {
        localStorage.userData = '';
        localStorage.tokenData = '';
        updateUserData();
    }

    // It checks if the corresponding user is previously logged in and updates the view accordingly
    updateUserData = () => {
        console.log(">>> updateUserData()");
        var data = JSON.parse(localStorage.userData || 'null');
        if (data) {
            var tokenData = JSON.parse(localStorage.tokenData || 'null');
            if (!tokenData || tokenData.expires_on < new Date().getTime()) {
                localStorage.userData = '';
                localStorage.tokenData = '';
                updateUserData();
                return;
            }
            document.getElementById(userdataElementID).innerHTML = data.name + ' ' + data.surname;
            document.getElementById(userdataElementID).style = "display:block";
            enablePrivateFeatures();
            featureEnabled = true;
            // session started successfully, log
            //logger().sessionStart(simpaticoEservice);
            //// if the e-service page is associated to the form, log the form start event
            //if (window.simpaticoForm) {
            //    // log end of session
            //    logger().formStart(simpaticoEservice, simpaticoForm);
            //}

        } else {
            document.getElementById(userdataElementID).innerHTML = greeting;
            document.getElementById(userdataElementID).style = "display:block";
            //disablePrivateFeatures();
            featureEnabled = false;
        }
        console.log("<<< updateUserData()");
    }

    isEnabled = () => {
        return this.featureEnabled;
    }

    getUserId = () => {
        var data = JSON.parse(localStorage.userData || 'null');
        return !!data ? data.userId : null;
    }

    getToken = () => {
        return localStorage.tokenData
    }

}
