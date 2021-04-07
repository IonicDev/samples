/*
 * (c) 2018-2021 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

/*
 * WARNING *
 * Calling agent.enrollUser() successfully is a prerequisite before using this code.
 * This is done using enrollDevice.js.
 */

import {getAgentConfig} from '../jssdkConfig.js';

const main = async () => {
  const appData = getAgentConfig('JavaScript Get Key by External ID');

  // initialize agent
  try {
    const resp = await new window.IonicSdk.ISAgent(appData);
    const agent = resp.agent;

    // create a random external ID
    const externalId = Math.random().toString(16).replace(/\./, '');

    // create single key with external ID
    console.log('');
    try {
      const fixedAttributes = {
        'ionic-external-id': [externalId]
      }

      const response = await agent.createKeys({
        quantity: 1,
        attributes: fixedAttributes
      });
      const key = response.keys[0];
    
      // display new key
      console.log('New Key with key ID: ' + key.keyId);
      console.log("with : " + JSON.stringify(key.attributes, null, 0));
    } catch (sdkErrorResponse) {
        console.log('Error Creating Key: ' + sdkErrorResponse.error);
    }
    console.log(' ');

    // Get keys with external ID.
    try {
      const response = await agent.getKeys({
        externalIds: [externalId]
      });
      const key = response.keys[0];
    
      // Display fetched key.
      console.log('Fetching key by external ID: ' + externalId);
      console.log('KeyId             : ' + key.keyId);
      console.log('KeyBytes          : ' + key.key);
      console.log('FixedAttributes   : ' + JSON.stringify(key.attributes, null, 0));
      console.log('MutableAttributes : ' + JSON.stringify(key.mutableAttributes, null, 0));
    } catch (sdkErrorResponse) {
      console.log('Error getting Key: ' + sdkErrorResponse.error);
    }
  } catch (SdkErrorResponse) {
    console.log('Initializing agent error: ' + SdkErrorResponse.error);
  }

};

main();
