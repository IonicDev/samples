/*
 * (c) 2018-2022 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.iubenda.com/privacy-policy/27287244).
 */

/*
 * WARNING *
 * Calling agent.enrollUser() successfully is a prerequisite before using this code.
 * This is done using enrollDevice.js.
 */

import {getAgentConfig} from '../jssdkConfig.js';

const main = async () => {

  const appData = getAgentConfig('JavaScript Get Active Profile');

  // initialize agent
  try {
    const resp = await new window.IonicSdk.ISAgent(appData);
    const agent = resp.agent;

    // get active profile
    console.log('');
    try {
      const response = await agent.getActiveProfile();

      console.log('Active Profile : ' + response.active);
      console.log('Created        : ' + response.created);
      console.log('Device Id      : ' + response.deviceId);
      console.log('keyspace       : ' + response.keyspace);
      console.log('server         : ' + response.server);
 
    } catch (sdkErrorResponse) {
      console.log('Error Getting Active Profile: ' + sdkErrorResponse.error);
    }

  } catch (sdkErrorResponse) {
    console.log('Initializing agent error: ' + sdkErrorResponse.error);
  }
}

main()
