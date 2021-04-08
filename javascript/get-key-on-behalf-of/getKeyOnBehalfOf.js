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

  const appData = getAgentConfig('JavaScript Get Key on Behalf of');

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
        
    console.log('');
    let keyId = '';

    // create key on behalf of
    try {
      const response = await agent.createKeys({
        quantity: 1,
        metadata: requestMetadata
      }); 
    
      const key = response.keys[0];
 
      // display created key
      console.log('New Key with key ID: ' + key.keyId);
      console.log('  on behalf of: ' + delegatedUserEmail);
      console.log('KeyBytes          : ' + key.key);
      console.log('FixedAttributes   : ' + JSON.stringify(key.attributes,null,0));
      console.log('MutableAttributes : ' + JSON.stringify(key.mutableAttributes,null,0));

      keyId = key.keyId;
    } catch (sdkErrorResponse) {
        console.log('Error Creating Key: ' + sdkErrorResponse.error);
    }

    console.log(' ');
    try {
      // get key
      const response = await agent.getKeys({
        keyIds: [keyId],
        metadata: requestMetadata
      });
      const key = response.keys[0];
    
      // display fetched key
      console.log('Key fetched on behalf of: ' + delegatedUserEmail);
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
