/*
 * (c) 2018 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

const appData = {
  appId: 'ionic-js-samples',
  userId: 'developer',
  userAuth: 'password123',
  enrollmentUrl: 'https://dev-enrollment.ionic.com/keyspace/HVzG/register'
}

const main = async () => {
  const keyId = 'HVzG5uKl3yE'

  // initialize agent
  const agent = new window.IonicSdk.ISAgent('https://dev-api.ionic.com/jssdk/latest/')
  await agent.loadUser(appData).catch((error) => {
    console.log(`Error loading profile: ${error}`)
  })

  // get key
  const response = await agent.getKeys({keyIds: [keyId]}).catch((error) => {
    console.log(`Error Creating Key: ${error}`)
  })
  const key = response.keys[0]

  // display fetched key
  console.log(`KeyId             : ${key.keyId}`)
  console.log(`KeyBytes          : ${key.key}`)
  console.log(`FixedAttributes   : ${JSON.stringify(key.attributes, null, 0)}`)
  console.log(`MutableAttributes : ${JSON.stringify(key.mutableAttributes, null, 0)}`)
}

main()
