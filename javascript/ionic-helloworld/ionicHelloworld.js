/*
 * (c) 2019-2020 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

/*
 * WARNING *
 * Calling agent.enrollUser() successfully is a pre-requisite before using this code.
 * This is done enrollDevice.js.
 */

// AppData for all Javascript samples: appId, userId, and userAuth needs to be the same
// as the appData that was used for enrollment.
const appData = {
  appId: 'ionic-js-samples',
  userId: 'developer',
  userAuth: 'password123',
  metadata: {
    'ionic-application-name': 'Javascript Hello, World!',
    'ionic-application-version': '1.3.0'
  }
};

const main = async () => {

  const message = 'Hello, World!'

  // define data markings
  const dataMarkings = {
    'clearance-level': ['secret']
  }

  // initialize agent
  try {
    const resp = await new window.IonicSdk.ISAgent(appData);
    const agent = resp.agent;

    // encrypt message
    try {
      const encryptResponse = await agent.encryptStringChunkCipher({stringData: message, attributes: dataMarkings});
      const ciphertext = encryptResponse.stringChunk;
  
      // Note: Decryption only works if the policy allows it.
      const decryptedText = await agent.decryptStringChunkCipher({stringData: ciphertext});
  
      // display data
      console.log('');
      console.log('Plain Text: ' + message);
      console.log('Ionic Chunk Encrypted Text: ' + ciphertext);
      console.log('Decrypted Text: ' + decryptedText.stringChunk);
    } catch (SdkErrorResponse) {
      console.log('Encryption/Decrption error: ' + SdkErrorResponse.error);
    }

  } catch (SdkErrorResponse) {
    console.log('Obtianing agent error: ' + SdkErrorResponse.error);
  }

}

main();
