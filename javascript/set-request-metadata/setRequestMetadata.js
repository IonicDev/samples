/*
 * (c) 2018-2020 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

/*
 * WARNING *
 * Calling agent.enrollUser() successfully is a pre-requisite before using this code.
 * This is done enrollDevice.js.
 */

import {getAgentConfig} from '../jssdkConfig.js';

const main = async () => {

  const appData = getAgentConfig('Javascript Set Request Metadata');

  // initialize agent
  try {
    const resp = await new window.IonicSdk.ISAgent(appData);
    const agent = resp.agent;

    // define request metadata
    const requestMetadata = {
      'application-state': 'active'
    };
  
    // get key with request metadata
    const keyId = 'HVzG34L2MVI'
    try {
      const response = await agent.getKeys({
        keyIds: [keyId],
        metadata: requestMetadata
      });

      const key = response.keys[0];
    
      // display fetched key
      console.log('');
      console.log('KeyId             : ' + key.keyId);
      console.log('KeyBytes          : ' + key.key);
      console.log('FixedAttributes   : ' + JSON.stringify(key.attributes, null, 0));
      console.log('MutableAttributes : ' + JSON.stringify(key.mutableAttributes, null, 0));
    } catch (sdkErrorResponse) {
        console.log('Error Creating Key: ' + sdkErrorResponse.error);
    }
  } catch (sdkErrorResponse) {
    console.log('Obtaining agent error: ' + sdkErrorResponse.error);
  }
}

main()
