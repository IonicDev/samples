/*
 * (c) 2018-2020 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 * 
 * Adapted from: https://api.ionic.com/jssdk/latest/Docs/tutorial-helloWorld_index.js.html
 */

/**
 * This Ionic JSSDK Hello World sample features an example JavaScript Promise-based workflow.
 * The code is written in ECMAScript6/ES6/ES2015+.
 * 
 * The process of 'enrolling a user (device)' establishes the active browser instance 
 * as a trusted device for the applicable Ionic keyspace. 
 * 
 * Learn more about device enrollment here: 
 * https://api.ionic.com/jssdk/latest/Docs/tutorial-device_enrollment.html
 * 
 * Upon successful enrollment, the application enables the user to encrypt and decrypt text data.
 *  
 * NOTE:
 * The application opens a new browser tab for the enrollment process.
 * The backend JSSDK version is automatically derived from the following 
 * `<script/>` tag found in the accompanying index.html: 
 * `<script src="https://api.ionic.com/jssdk/latest/libs/sdk.bundle.js"></script>`
 * In order to use a different JSSDK version, simply update the script src URL accordingly. 
 *
 * NOTE:
 * There is another enrollement example at enrollDevice.js which only enrolls a device.
 */

import {getAgentConfig} from '../jssdkConfig.js';
import {getKeyspace} from '../jssdkConfig.js';
import {getTenant} from '../jssdkConfig.js';

// NameSpace
const NS = {
    mssgsBloc: document.getElementById('messagesBloc'),
    eBloc: document.getElementById('encryptedBloc'),
    dBloc: document.getElementById('decryptedBloc'),

    // here, the Ionic JSSDK environment is enabled by the ISAgent to create an sdk instance,
    // stored here in the NS namespace
    // the credentials form and messages block handlers are also added
    initEnv: function() {
        // attaches an Ionic JSSDK instance to this NameSpace
        Object.defineProperty(this, 'sdk', {
            value: new window.IonicSdk.ISAgent()
        });

        // attaches an event listener for the click event, onto the messages DOM block
        this.mssgsBloc && this.mssgsBloc.addEventListener('click', this.onMssgsBlocClick.bind(this));

        ///////////////////////////////////////
        // Verify that configAgent.js is correct.
        ///////////////////////////////////////
        const keyspace = getKeyspace();
        const tenantId = getTenant();

        // This URL below specifies the Ionic IDC enrollment endpoint url
        Object.defineProperty(this, 'enrollmentUrl', {
            value: 'https://enrollment.ionic.com/keyspace/' + keyspace + '/idc/' + tenantId + '/default/register'
        });
        // Another enrollment endpoint is: https://enrollment.ionic.com/your-keyspace/your-tenant-id/register'
        // which allows enrollment options, like e-mail and SAML if your tenant is configured for those options..

        this.mssgsBloc.innerHTML =
            'Please specify credentials used to securely store the resulting enrollment profile.';

        this.enableCredsForm();
        return this;
    },

    enableCredsForm: function() {
        const form = (this.credsForm = document.getElementById('credsForm'));
        if (form) {
            // prevent form from submitting on return key
            form.addEventListener('submit', (e) => e.preventDefault());
            form.addEventListener('click', this.onCredsFormAction.bind(this));
        }
    },

    removeCredsForm: function() {
        const form = this.credsForm;
        if (form) {
            form.classList.add('hide');
            form.removeEventListener('submit', (e) => e.preventDefault());
            form.removeEventListener('click', this.onCredsFormAction.bind(this));
        }
    },

    // attempts to  open a new browser window at the Ionic enrollment url and prompts the user for
    // further action with messages using the mssgsBloc
    promptAttendPopup: function() {
        const url = this.appData.enrollmentUrl;
        if (!window.open(url, 'Ionic Client Device Enrollment')) {
            this.mssgsBloc.innerHTML =
                'You will need to enroll this device in a new browser tab in order ' + 
                'to use Ionic Security to protect your data.<br><br>' +
                'You can <button name="Resume">Try now</button> or enable popups ' + 
                'for this page and then click this <button name="Resume">Try again</button> button.';
        } else {
            this.mssgsBloc.innerHTML =
                'Please look for the new Ionic Device Enrollment tab, complete enrollment, ' + 
                'then click this <button name="Resume">Resume</button> button.';
        }
    },

    // the messgBloc click event handler
    onMssgsBlocClick: function(e) {
        if (e.target.matches('button[name="Resume"]')) {
            const enrollmentNotifier = this.enroll();
            // the notifier property (a Promise) is resolved
            // when enrollment is complete and the encrypted
            // profile is stored in localStorage
            enrollmentNotifier &&
                enrollmentNotifier.then(
                    function() {
                        this.removeCredsForm();
                        this.initDataForm();
                    }.bind(this)
                );
        }
    },

    // sets up the appData object when we have profile credentials loaded
    loadCredentials: function(userId, userAuth) {
        if (!userId || !userAuth) {
            return Promise.reject('Awaiting credentials.');
        }

        // Get the appData and add enrollment URL.
        // Pins appData to this NameSpace for convenience.
        this.appData = getAgentConfig('Javascript SDK Hello World');
        this.appData = Object.assign(this.appData, {enrollmentUrl: this.enrollmentUrl});

        return Promise.resolve('Using provided credentials.');
    },

    // attempts to use the sdk to enroll a user and receive a Secure Enrollment Profile
    // returns the enrollment notifier Promise object
    enroll: function() {
        if (!this.sdk) {
            return;
        }
        if (this.enrollmentNotifier) {
            this.promptAttendPopup();
            return;
        }
        this.enrollmentNotifier = this.sdk.enrollUser(this.appData).then(
            function(res) {
                if (!res) {
                    return Promise.reject('Error Enrolling');
                }

                this.promptAttendPopup();

                // the notifier property (a Promise object) is resolved when enrollment is complete and
                // the encrypted profile is stored in localStorage
                return res.notifier;
            }.bind(this),
            (rej) => {
                return Promise.reject(rej);
            }
        );
        return this.enrollmentNotifier;
    },

    // returns rejected Promise with the error code returned by sdk.loadUser
    // OR
    // result from calling sdk.enrollUser()
    onLoadProfileError: function(errCode) {
        if (errCode === 40022 || errCode === 40002) {
            return this.enroll();
        }
        return Promise.reject(errCode);
    },

    // calls JSSDK loadUser() with profile parameters specified in 'appData' above
    loadProfile: function() {
        if (!this.sdk) {
            return Promise.reject(
                'Missing an sdk? hint: Was it loaded onto the page in a script tag?'
            );
        }
        return this.sdk
            .loadUser(this.appData)
            .then(() => Promise.resolve(), (rej) => Promise.reject(rej.sdkResponseCode))
            .catch(this.onLoadProfileError.bind(this));
    },

    // attaches events to the encrypt/decrypt form to handle outward user actions
    initDataForm: function() {
        this.mssgsBloc.innerHTML =
            'You are all set up to begin encrypting and decrypting simple text input.';
        const form = document.getElementById('dataForm');
        if (form) {
            form.addEventListener('submit', (e) => e.preventDefault());
            form.addEventListener('click', this.onDataFormAction.bind(this));
            form.classList.remove('hide');
        }
    },

    // the userId/userAuth form events handler
    onCredsFormAction: function(e) {
        if (e.target.tagName !== 'BUTTON' || !e.target.value) {
            // reject the click action
            return true;
        }

        const userId = e.currentTarget.elements['userId'].value;
        const userAuth = e.currentTarget.elements['userAuth'].value;
        if (!userId || !userAuth) {
            this.mssgsBloc.innerHTML =
                'User ID and Password are required to enroll a new "device", ' + 
                'or to use and existing enrolled device profile.';
        }

        // kicks off a promise chain to load up an Ionic User Profile and enable the data form
        this.loadCredentials(userId, userAuth)
            .then(this.loadProfile.bind(this))
            .then(
                function() {
                    this.removeCredsForm();
                    this.initDataForm();
                }.bind(this),
                (rej) => {}
            );
    },

    // the encrypt/decrypt text form events handler
    onDataFormAction: function(e) {
        if (e.target.tagName !== 'BUTTON' || !e.target.value) {
            return true;
        }

        const eBloc = this.eBloc;
        const dBloc = this.dBloc;
        const eTarget = eBloc.getElementsByClassName('display-text');
        if (e.target.matches('[value="ENCRYPT"]')) {
            const input = e.currentTarget.elements['plainText'];
            input &&
                input.value.length &&
                this.encryptText(input.value).then((res) => {
                    if (eTarget && eTarget.length) {
                        eTarget[0].innerText = res.stringChunk;
                        eBloc.classList.remove('hide');
                        dBloc.classList.add('hide');
                    }
                });
        } else if (e.target.matches('[value="DECRYPT"]')) {
            if (eTarget && eTarget.length && eTarget[0].innerText.length) {
                const dTarget = dBloc.getElementsByClassName('display-text');
                dTarget &&
                    this.decryptText(eTarget[0].innerText).then((res) => {
                        dTarget[0].innerText = res.stringChunk;
                        dBloc.classList.remove('hide');
                    });
            }
        }
        return true;
    },

    // calls the Ionic JSSDK instance to encrypt data passed from the encrypt/decrypt form
    encryptText: function(text) {
        return this.sdk
            .encryptStringChunkCipher({ stringData: text })
            .catch((err) => Promise.reject(err));
    },

    // calls the Ionic JSSDK instance to decrypt data passed from the encrypt/decrypt form
    decryptText: function(text) {
        return this.sdk
            .decryptStringChunkCipher({ stringData: text })
            .catch((err) => Promise.reject(err));
    }
};

// launches the application
NS.initEnv();

