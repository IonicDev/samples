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

  const appData = getAgentConfig('JavaScript Create Key on Behalf of');

  // initialize agent
  try {
    const resp = await new window.IonicSdk.ISAgent(appData);
    const agent = resp.agent;

    // Define on-behalf-of as request metadata.
    // Replace email with user's email that is enrolled in the same keyspace.
    const delegatedUserEmail = 'testy@ionic.com';
    const requestMetadata = {
        'ionic-delegated-email': delegatedUserEmail,
    }

    // create key
    try {
      const response = await agent.createKeys({
        quantity: 1,
        metadata: requestMetadata
      });

      const key = response.keys[0];
  
      // display created key
      console.log('');
      console.log('Created key on behalf of: ' + delegatedUserEmail);
      console.log('KeyId             : ' + key.keyId);
      console.log('KeyBytes          : ' + key.key);
      console.log('FixedAttributes   : ' + JSON.stringify(key.attributes,null,0));
      console.log('MutableAttributes : ' + JSON.stringify(key.mutableAttributes,null,0));
    } catch (sdkErrorResponse) {
        console.log('Error Creating Key: ' + sdkErrorResponse.error);
    }
  } catch (sdkErrorResponse) {
    console.log('Initializing agent error: ' + sdkErrorResponse.error);
  }
}

main();
