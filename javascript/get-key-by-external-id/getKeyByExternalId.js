/*
 * (c) 2019-2020 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

const appData = {
  appId: 'ionic-js-samples',
  userId: 'developer',
  userAuth: 'password123',
};

const main = async () => {
  // Initialize agent.
  // The URL is optional.
  const agent = new window.IonicSdk.ISAgent();
  await agent.loadUser(appData).catch((error) => {
    console.log('Error loading profile: ', error);
  });

  // Set the app metadata.
  let response = await agent.setMetadata({
    'ionic-application-name': 'JavaScript getKeyByExternalId',
    'ionic-application-verison': '1.0',
  });

  // Define external ID in an array.
  const externalIds = ['02e09520-e52c-42aa-b21c-a60698cf31a2'];

  // Get keys with external ID.
  response = await agent.getKeys({
    externalIds: externalIds,
  }).catch((error) => {
    console.log('Error Creating Key: ', error);
  });
  const key = response.keys[0];

  // Display fetched key.
  console.log('');
  console.log('KeyId             : ', key.keyId);
  console.log('KeyBytes          : ', key.key);
  console.log('FixedAttributes   : ', JSON.stringify(key.attributes, null, 0));
  console.log('MutableAttributes : ', JSON.stringify(key.mutableAttributes, null, 0));
};

main();
