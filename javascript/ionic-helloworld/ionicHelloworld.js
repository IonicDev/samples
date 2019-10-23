/*
 * (c) 2019-2020 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

// AppData for all Javascript samples.
const appData = {
    appId: 'ionic-js-samples',
    userId: 'developer',
    userAuth: 'password123',
}

const main = async () => {

  const message = 'Hello, World!'

  // define data markings
  const dataMarkings = {
    'clearance-level': ['secret']
  }

  // initialize agent
  const agent = new window.IonicSdk.ISAgent()
  await agent.loadUser(appData).catch((error) => {
    console.log('Error loading profile: ' + error)
  })

  // Set the app metadata.
  await agent.setMetadata({
    'ionic-application-name': 'JavaScript helloWorld',
    'ionic-application-verison': '1.1.0',
  }).catch((error) => {
    console.log('Error setting metadata: ' + error)
  })

  // encrypt message
  const encryptResponse = await agent.encryptStringChunkCipher({stringData: message, attributes: dataMarkings})

  // Note: Decryption only works if the policy allows it.
  const ciphertext = encryptResponse.stringChunk
  const decryptedText = await agent.decryptStringChunkCipher({stringData: ciphertext})

  // display data
  console.log('Plain Text: ' + message)
  console.log('Ionic Chunk Encrypted Text: ' + ciphertext)
  console.log('Decrypted Text: ' + decryptedText.stringChunk)
}

main();
