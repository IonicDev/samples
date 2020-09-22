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
  const appData = getAgentConfig('JavaScript Set Application Metadata');

  // Initialize agent.
  try {
    const resp = await new window.IonicSdk.ISAgent(appData);
    const agent = resp.agent;

    try {
      // Set the app metadata.
      await agent.setMetadata({
        'ionic-application-name': 'JavaScript Set App Metadata',
        'ionic-application-verison': '1.3.0',
      });
    } catch (sdkErrorResponse) {
      console.log('Error setting app metadata: ' + sdkErrorResponse.error);
    }
  } catch (sdkErrorResponse) {
    console.log('Initializing agent error: ' + sdkErrorResponse.error);
  }
}

main();
