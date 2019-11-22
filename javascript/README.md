# Machina Javascript SDK Samples

You will need a Machina instance to execute these samples.  You can create a Machina instance from our [Machina Developers Portal](https://dev.ionic.com) by clicking on the *"Start For Free*" link. You will receive an e-mail which contains your keyspace, tenant ID, and user name (e-mail).  Please save these for future use.

If you already have a Machina instance, you will need the keyspace, tenant ID, and user name (e-mail).  These can be found in the original welcome e-mail or from your Machina instance administrator.

## Start-up
To run the Machina Javascript samples, you will need to do:

**Step 1**: Clone Ionic samples repo from Github.

~~~bash
git clone https://github.com/IonicDev/samples
cd samples/javascript/
~~~

**Step 2**: Open jssdkConfig.js in your favorite editor and modify:

1. `const keyspace = ''` with your keyspace
2. `const tenant = ''` with your tenant ID. 

**Note:** All the Javascript samples use the jssdkConfig.js for configuration.

**Step 3**: Start a local webserver (example: [http-server](https://www.npmjs.com/package/http-server)) in the `samples/javascript` directory.

~~~bash
node http-server
~~~

## Prerequisites

* An existing enrollment profile stored in browser localStorage during a previous call to agent.enrollUser().
* Specified appId, userId, and userAuth are used for look up and decryption of device profile information.

You can create an enrollment profile by following the instructions below in "[Create Device Credentials](#Create Device Credentials)". The Javascript samples currently use the following appId, userId, and userAuth from `jssdkConfig.js`:

~~~Javascript
const agentConfig = {
    appId: 'ionic-js-samples',
    userId: 'developer',
    userAuth: 'password123',
  };
~~~
**Note:** Failure to find any profiles may indicate that the device credentials were not created; or that the specified parameters aren't a match to those used during enrollment.

## Create Device Credentials

After the http server has been started, use a Chrome or Firefox browser.

1. Go to URL: `http: 127.0.0.1:8080`. This should bring up an index page. 
2. Under the "*Machina Javascript Create Device Credentials*" heading, you can click either:

	* enroll-device
	* jssdk-helloworld

Enroll-device will only create device credentials, while jssdk-helloworld will create device credentials and encrypt/decrypt strings.  Jssdk-hellowold is adapted from the Javascript SDK [code sample](https://api.ionic.com/jssdk/latest/Docs/tutorial-helloWorld_index.js.html).  You can read more about enrollment in the [Javascript documentation](https://api.ionic.com/jssdk/latest/Docs/tutorial-device_enrollment.html) and in the [Machina Developers Portal](https://dev.ionic.com/platform/enrollment).  You will need your e-mail address to complete the enrollment form.

## Sample Execution
After you have created the device credentials, you can run some of the samples without modification, like `ionic-helloworld`, and `create-key`. Some of the others, like `get-key`, need to be modified with a created key in your Machina instance. **Note:** For development, it is recommended to copy created keys into a text file for later use.  This is **not** good security practice for production environments.

## Documentation
Most of the samples are documented in the [Machina Developers Portal](https://dev.ionic.com/sdk/features).  The Javascript SDK documentation is located [here](https://api.ionic.com/jssdk/latest/Docs/index.html).

