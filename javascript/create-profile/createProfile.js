/*
 * (c) 2019-2020 Ionic Security Inc.
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

  // create new profile
  const response = await agent.enrollUser(appData).catch((error) => {
    console.log('Error loading profile: ', error)
  })

  // open new page to complete enrollment
  window.open(response.redirect)
}

main()
