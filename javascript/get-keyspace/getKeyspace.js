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

  const appData = getAgentConfig('JavaScript Get Keyspace');

  // initialize agent
  try {
    const resp = await new window.IonicSdk.ISAgent(appData);
    const agent = resp.agent;
    console.log('');

    try {
      // get active keyspace id
      const this_profile = await agent.getActiveProfile();

      // get active keyspace
      const response = await agent.getKeyspace({keyspace: this_profile.keyspace});

      console.log('keyspace          : ' + response.keyspace);
      console.log('ttl seconds       : ' + response.ttlSeconds);
      console.log('fqdn              : ' + response.fqdn);
      console.log('enrollment url(s) : ' + response.answers.enroll);
      console.log('tenant id(s)      : ' + response.answers.tenantid); 
      console.log('url(s)            : ' + response.answers.url);
    } catch (sdkErrorResponse) {
      console.log('Error Getting Keyspace Data: ' + sdkErrorResponse.error);
    }
  } catch (sdkErrorResponse) {
    console.log('Initializing agent error: ' + sdkErrorResponse.error);
  }
}

main()
