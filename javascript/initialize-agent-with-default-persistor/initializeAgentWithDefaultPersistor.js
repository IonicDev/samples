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
  const appData = getAgentConfig('JavaScript Initialize Agent with Default Persistor');

  // initialize agent with default persistor
  try {
    const resp = await new window.IonicSdk.ISAgent(appData);

    // display all profiles in persistor
    const profiles = resp.profiles;
    console.log('');
    profiles.forEach((profile) => {
      console.log('---');
      console.log('Id       : ' + profile.deviceId);
      console.log('Keyspace : ' + profile.keyspace);
      console.log('ApiUrl   : ' + profile.server);
    });
  } catch (sdkErrorResponse) {
    console.log('Initializing agent error: ' + sdkErrorResponse.error);
  }
}

main()
