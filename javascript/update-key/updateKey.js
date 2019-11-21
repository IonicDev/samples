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
  // keyId can be changed to any key you have already created.
  const keyId = 'HVzG5uKl3yE'

  const appData = getAgentConfig('Javascript Update Key');

  // initialize agent
  try {
    const resp = await new window.IonicSdk.ISAgent(appData);
    const agent = resp.agent;

    // get key
    let key;
    try {
      const response = await agent.getKeys({keyIds: [keyId]})
      key = response.keys[0];

      // Display original key attributes.
      console.log('');
      console.log('Original key:');
      console.log('KeyId             : ' + key.keyId);
      console.log('KeyBytes          : ' + key.key);
      console.log('FixedAttributes   : ' + JSON.stringify(key.attributes, null, 0));
      console.log('MutableAttributes : ' + JSON.stringify(key.mutableAttributes, null, 0));
    } catch (sdkErrorResponse) {
      console.log('Error getting key: ' + sdkErrorResponse.error);
    }
  
    // update key
    try {
      const update_resp = await agent.updateKeys({
        keyRequests: [{
          keyId: key.keyId,
          force: true,
          mutableAttributes: {
            "analysis": ["Highly Restricted"]
          }
        }]
      });
  
      const updated_key = update_resp.keys[0];

      // Display updated key attributes.
      console.log('Updated key:');
      console.log('KeyId             : ' + updated_key.keyId);
      console.log('KeyBytes          : ' + updated_key.key);
      console.log('FixedAttributes   : ' + JSON.stringify(updated_key.attributes, null, 0));
      console.log('MutableAttributes : ' + JSON.stringify(updated_key.mutableAttributes, null, 0));
    } catch (sdkErrorResponse) {
      console.log('Error updating key: ' + sdkErrorResponse.error);
    }

  } catch (sdkErrorResponse) {
    console.log('Initializing agent error: ' + sdkErrorResponse.error);
  }
}

main()
