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
  const keyCount = 3;

  const appData = getAgentConfig('Javascript Create Key');

  // initialize agent
  try {
    const resp = await new window.IonicSdk.ISAgent(appData);
    const agent = resp.agent;

    try {
      // create multiple keys
      const response = await agent.createKeys({
        quantity: keyCount
      });

      // display new keys
      console.log('');
      response.keys.forEach((key) => {
        console.log('---');
        console.log('KeyId    : ' + key.keyId);
        console.log('KeyBytes : ' + key.key);
        console.log('FixedAttributes   : ' + JSON.stringify(key.attributes, null, 0));
        console.log('MutableAttributes : ' + JSON.stringify(key.mutableAttributes, null, 0));
      });
    } catch (sdkErrorResponse) {
      console.log('Creating multiple keys error: ' + sdkErrorResponse.error);
    }
  } catch (sdkErrorResponse) {
    console.log('Obtaining agent error: ' + sdkErrorResponse.error);
  }
}

main()
