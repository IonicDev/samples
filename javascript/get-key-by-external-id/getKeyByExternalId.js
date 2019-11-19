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
  const appData = getAgentConfig('Javascript Get Key by External ID');

  // initialize agent
  try {
    const resp = await new window.IonicSdk.ISAgent(appData);
    const agent = resp.agent;


    // Define external ID in an array.
    const externalIds = ['02e09520-e52c-42aa-b21c-a60698cf31a2'];
  
    // Get keys with external ID.
    try {
      response = await agent.getKeys({
        externalIds: externalIds,
      });
      const key = response.keys[0];
    
      // Display fetched key.
      console.log('');
      console.log('KeyId             : ' + key.keyId);
      console.log('KeyBytes          : ' + key.key);
      console.log('FixedAttributes   : ' + JSON.stringify(key.attributes, null, 0));
      console.log('MutableAttributes : ' + JSON.stringify(key.mutableAttributes, null, 0));
    } catch (sdkErrorResponse) {
      console.log('Error getting Key: ' + sdkErrorResponse.error);
    }
  } catch (SdkErrorResponse) {
    console.log('Obtaining agent error: ' + SdkErrorResponse.error);
  }

};

main();
