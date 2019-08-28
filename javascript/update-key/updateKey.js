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
  // keyId can be changed to any key you have already created.
  const keyId = 'HVzG5uKl3yE'

  // initialize agent
  const agent = new window.IonicSdk.ISAgent()
  await agent.loadUser(appData).catch((error) => {
    console.log('Error loading profile: ', error)
  })

  // get key
  const response = await agent.getKeys({keyIds: [keyId]}).catch((error) => {
    console.log('Error Getting Key: ', error)
  })
  const key = response.keys[0]

  const update_resp = await agent.updateKeys({
    keyRequests: [{
      keyId: key.keyId,
      force: true,
      mutableAttributes: {
        "analysis": ["Highly Restricted"]
      }
    }]
  }).catch((error) => {
    console.log('Error Updating Key: ', error)
  })
  const updated_key = update_resp.keys[0]

  // display updated key
  console.log('Updated key')
  console.log('KeyId             : ', updated_key.keyId)
  console.log('KeyBytes          : ', updated_key.key)
  console.log('FixedAttributes   : ', JSON.stringify(updated_key.attributes, null, 0))
  console.log('MutableAttributes : ', JSON.stringify(updated_key.mutableAttributes, null, 0))
}

main()
