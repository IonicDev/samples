/*
 * (c) 2019-2020 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

const appData = {
  appId: 'ionic-js-samples',
  userId: 'developer',
  userAuth: 'password123'
}

const main = async () => {
  // initialize agent with default persistor
  const agent = new window.IonicSdk.ISAgent()
  let response = await agent.loadUser(appData).catch((error) => {
    console.log('Error loading profile: ', error)
  })

  // Set the app metadata.
  response = await agent.setMetadata({
    'ionic-application-name': 'JavaScript initializeAgentWithDefaultPersistor',
    'ionic-application-verison': '1.1.0',
  })

  // display all profiles in persistor
  const profiles = response.profiles;
  console.log('');
  profiles.forEach((profile) => {
    console.log('---');
    console.log('Id       : ', profile.deviceId);
    console.log('Keyspace : ', profile.keyspace);
    console.log('ApiUrl   : ', profile.server);
  })
}

main()
