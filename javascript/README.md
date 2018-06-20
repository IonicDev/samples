# Ionic Javascript SDK Samples

To build and run an Ionic Javascript SDK Sample, navigate into the directory of a specific sample task, serve the HTML index page (using a simple web server like http-server from NPM, and navigate to it on the browser. Open the browser's console to view the output. 

Note: the samples expect a profile to exist in the browser with the following credentials:

```
appId: 'ionic-js-samples',
userId: 'developer',
userAuth: 'password123',
```

If you have an existing profile, update the `appData` variable in the sample app before runnig it. Alternatively, run the `initialize-agent-with-default-persistor` sample app which will create a profile with the settings above. 