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

  const message = 'Hello, World!';

  // define data markings
  const dataMarkings = {
    'clearance-level': ['secret']
  }

  const appData = getAgentConfig('Javascript Hello, World!');

  // initialize agent
  try {
    const resp = await new window.IonicSdk.ISAgent(appData);
    const agent = resp.agent;

    // encrypt message
    let ciphertext;
    try {
      const encryptResponse = await agent.encryptStringChunkCipher({stringData: message, attributes: dataMarkings});
      ciphertext = encryptResponse.stringChunk;
    } catch (SdkErrorResponse) {
      console.log('Encryption error: ' + SdkErrorResponse.error);
      return;
    }
  
    // Note: Decryption only works if the policy allows it.
    try {
      const decryptedText = await agent.decryptStringChunkCipher({stringData: ciphertext});
  
      // display data
      console.log('');
      console.log('Plain Text: ' + message);
      console.log('Ionic Chunk Encrypted Text: ' + ciphertext);
      console.log('Decrypted Text: ' + decryptedText.stringChunk);
    } catch (SdkErrorResponse) {
      console.log('Error decrypting cipertext: ' + SdkErrorResponse.error);
      console.log('Insufficient clearance to access this data.');
    }

  } catch (SdkErrorResponse) {
    console.log('Initializing agent error: ' + SdkErrorResponse.error);
  }
}

main();
