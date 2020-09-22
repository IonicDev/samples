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
  // Modify keyId and delagatedUserEmail to what was created.
  const keyId = 'HVzG4XpcAMA';
  const delegatedUserEmail = 'test@ionic.com';

  const appData = getAgentConfig('JavaScript Get Key on Behalf of');

  // initialize agent
  try {
    const resp = await new window.IonicSdk.ISAgent(appData);
    const agent = resp.agent;

    // define on-behalf-of as request metadata
    const requestMetadata = {
      'ionic-delegated-email': delegatedUserEmail
    };

    try {
      // get key
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
      console.log('Getting key on behalf of error: ' + sdkErrorResponse.error);
    }
  } catch (sdkErrorResponse) {
    console.log('Initializing agent error: ' + sdkErrorResponse.error);
  }
}

main()
