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
  const keyId = '';

  const appData = getAgentConfig('JavaScript Get Key');

  // initialize agent
  try {
    const resp = await new window.IonicSdk.ISAgent(appData);
    const agent = resp.agent;

    // create single key
    console.log('');
    try {
      const response = await agent.createKeys({quantity: 1});
      const key = response.keys[0];
    
      // display new key
      console.log('New Key with key ID: ' + key.keyId);
      keyId = key.keyId;
    } catch (sdkErrorResponse) {
        console.log('Error Creating Key: ' + sdkErrorResponse.error);
    }
    console.log(' ')

    // get key
    try {
      const response = await agent.getKeys({keyIds: [keyId]});
      const key = response.keys[0];
  
      // display fetched key
      console.log('');
      console.log('KeyId             : ' + key.keyId);
      console.log('KeyBytes          : ' + key.key);
      console.log('FixedAttributes   : ' + JSON.stringify(key.attributes, null, 0));
      console.log('MutableAttributes : ' + JSON.stringify(key.mutableAttributes, null, 0));
    } catch (sdkErrorResponse) {
      console.log('Error getting Key: ' + sdkErrorResponse.error);
    }
  } catch (sdkErrorResponse) {
    console.log('Initializing agent error: ' + sdkErrorResponse.error);
  }
}

main()
