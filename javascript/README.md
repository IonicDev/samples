# Ionic Javascript SDK Samples

To build and run an Ionic Javascript SDK Sample, navigate into the directory of a specific sample task, serve the HTML index page (using a simple web server like http-server from NPM, and navigate to it on the browser. Open the browser's console to view the output. 

Note: the samples expect a profile to exist in the browser with the following credentials:

```
appId: 'ionic-js-samples',
userId: 'developer',
userAuth: 'password123',
```

If you have an existing profile, update the `appData` variable in the sample app before running it. Alternatively, run the `create-profile` sample app which will create a profile with the settings above. 

**Step 1**: Clone Ionic samples repo from github, and navigate to the JavaScript HelloWorld directory:
~~~bash
git clone https://github.com/IonicDev/samples
cd samples/javascript/ionic-helloworld
~~~

**Step 2**: Start a local webserver (example: [http-server](https://www.npmjs.com/package/http-server))
~~~bash
http-server
~~~

**Step 3**: Open Chrome and navigate to the HelloWorld webpage

`http://localhost:8080`

