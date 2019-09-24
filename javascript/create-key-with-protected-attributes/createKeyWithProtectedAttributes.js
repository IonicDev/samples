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
  // initialize agent
  const agent = new window.IonicSdk.ISAgent()
  await agent.loadUser(appData).catch((error) => {
    console.log('Error loading profile: ', error)
  })

  // define protected attributes
  const fixedAttributes = {
    'ionic-protected-project': ['ORION']
  }

  // create single key with fixed attributes
  const response = await agent.createKeys({
    quantity: 1,
    attributes: fixedAttributes
  }).catch((error) => {
    console.log('Error Creating Key: ', error)
  })
  const key = response.keys[0]

  // display new key
  console.log('KeyId    : ', key.keyId)
  console.log('KeyBytes : ', key.key)
  console.log('FixedAttributes   : ', JSON.stringify(key.attributes, null, 0))
  console.log('MutableAttributes : ', JSON.stringify(key.mutableAttributes, null, 0))
}

main()
