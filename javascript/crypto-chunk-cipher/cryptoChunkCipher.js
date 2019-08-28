/*
 * (c) 2018-2019 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

const appData = {
  appId: 'ionic-js-samples',
  userId: 'developer',
  userAuth: 'password123',
  enrollmentUrl: 'https://preview-enrollment.ionic.com/keyspace/HVzG/register'
}

const main = async () => {
  const message = 'secret message'

  // initialize agent
  const agent = new window.IonicSdk.ISAgent()
  await agent.loadUser(appData).catch((error) => {
    console.log('Error loading profile: ', error)
  })

  // encrypt message
  const encryptResponse = await agent.encryptStringChunkCipher({stringData: message})
  const ciphertext = encryptResponse.stringChunk

  // decrypt message
  const decryptResponse = await agent.decryptStringChunkCipher({stringData: ciphertext})
  const plaintext = decryptResponse.stringChunk

  // display data
  console.log('Ciphertext : ', ciphertext)
  console.log('Plaintext  : ', plaintext)
}

main()
