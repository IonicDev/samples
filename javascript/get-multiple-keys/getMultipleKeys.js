/*
 * (c) 2018-2020 Ionic Security Inc.
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
  // Modify keys to keys you have obtained.
  const keyId1 = 'HVzG5uKl3yE'
  const keyId2 = 'HVzG3AJoHQU'
  const keyId3 = 'HVzG52Kj3to'

  const appData = getAgentConfig('Javascript Get Multiple Keys');

  // initialize agent
  try {
    const resp = await new window.IonicSdk.ISAgent(appData);
    const agent = resp.agent;

    let response;
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
    console.log('');
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
