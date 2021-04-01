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
  const keyCount = 3;

  const appData = getAgentConfig('JavaScript Get Multiple Keys');
  console.log('');

  // initialize agent
  try {
    const resp = await new window.IonicSdk.ISAgent(appData);
    const agent = resp.agent;

    console.log('');
    let response;
    try {
      // create multiple keys
      response = await agent.createKeys({
        quantity: keyCount
      });

      // display new keys
      response.keys.forEach((key) => {
        console.log('New Key with key ID: ' + key.keyId);
      });
    } catch (sdkErrorResponse) {
      console.log('Creating multiple keys error: ' + sdkErrorResponse.error);
    }

    // Put keys in separate variables
    const keyId1 = response.keys[0];
    const keyId2 = response.keys[1];
    const keyId3 = response.keys[2];

    console.log(' ');
    try {
      // get multiple keys
      response = await agent.getKeys({
        keyIds: [keyId1, keyId2, keyId3]
      });
    } catch (sdkErrorResponse) {
      console.log('Error getting multiple keys: ' + sdkErrorResponse.error);
    }
    const keys = response.keys;
  
    // display fetched keys
    keys.forEach((key) => {
      console.log('---');
      console.log('KeyId             : ' + key.keyId);
      console.log('KeyBytes          : ' + key.key);
      console.log('FixedAttributes   : ' + JSON.stringify(key.attributes, null, 0));
      console.log('MutableAttributes : ' + JSON.stringify(key.mutableAttributes, null, 0));
    });
  } catch (sdkErrorResponse) {
    console.log('Initializing agent error: ' + sdkErrorResponse.error);
  }
}

main()
