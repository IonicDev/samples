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
  // initialize agent with default persistor
  const agent = new window.IonicSdk.ISAgent('https://dev-api.ionic.com/jssdk/latest/')
  const response = await agent.loadUser(appData).catch((error) => {
    console.log("Error loading profile: ", error)
  })

  // display all profiles in persistor
  const profiles = response.profiles
  profiles.forEach((profile) => {
    console.log("Id       : ", profile.deviceId)
    console.log("Keyspace : ", profile.keyspace)
    console.log("ApiUrl   : ", profile.server)
  })
}

main()
