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
  const message = 'secret message'

  const appData = getAgentConfig('Javascript Crypto Chunk Cipher');

  // initialize agent
  try {
    const resp = await new window.IonicSdk.ISAgent(appData);
    const agent = resp.agent;

    // encrypt message
    let ciphertext;
    try {
      const encryptResponse = await agent.encryptStringChunkCipher({stringData: message})
      ciphertext = encryptResponse.stringChunk
    } catch (SdkErrorResponse) {
      console.log('Encryption error: ' + SdkErrorResponse.error);
    }

    // decrypt message
    try {
      const decryptResponse = await agent.decryptStringChunkCipher({stringData: ciphertext})
      const plaintext = decryptResponse.stringChunk

      // display data
      console.log('');
      console.log('Ciphertext : ' + ciphertext)
      console.log('Plaintext  : ' + plaintext)
    } catch (SdkErrorResponse) {
      console.log('Decryption error: ' + SdkErrorResponse.error);
    }
  } catch (sdkErrorResponse) {
    console.log('Initializing agent error: ' + sdkErrorResponse.error);
  }
}

main()
