/*
 * (c) 2019 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

// This URL specifies the Machina enrollment endpoint URL. There are 2 formats below:
// Ionic Authentication, and multiple authentication selection.  For free tenants, it
// is highly recommended to use the Ionic Authentication enrollment endpoint URL.
//
// Ionic Authentication format:
// https://enrollment.ionic.com/keyspace/<keyspace>/idc/<tenant_id>/default/register
// Example:
// https://: https://enrollment.ionic.com/keyspace/BKAl/idc/6d8d832785f3a66824ae2c23/default/register
//
// Multiple authentication format:
// https://<domain>/keyspace/<keyspace>/register
// Example:
// https://preview-enrollment.ionic.com/keyspace/HVzG/register

// Please modify keyspace and tenant ID to your keyspace and tennant ID.
const keyspace = ''
const tenantId = ''
const enrollmentUrl = 'https://enrollment.ionic.com/keyspace/' + keyspace + '/idc/' + tenantId + '/default/register'

// The appData is used for look-up, encryption, and decryption of the device profile information.
// Currently, the appId, userId, and userAuth are set to defaults for all the Javascript samples.
// However, in production, these values should be modified accordingly.
// See https://api.ionic.com/jssdk/latest/Docs/global.html#ProfileInfo for parameter description.
const appData = {
  appId: 'ionic-js-samples',
  userId: 'developer',
  userAuth: 'password123',
  enrollmentUrl: enrollmentUrl,
  metadata: {
    'ionic-application-name': 'Enroll Device',
    'ionic-application-version': '1.1.0'
  }
}

const main = async () => {

  // initialize agent
  const agent = new window.IonicSdk.ISAgent();
  console.log("Enrolling at: " + enrollmentUrl)

  await agent.loadUser(appData).catch(async (error) => {
    if ( error &&
         error.sdkResponseCode &&
         (error.sdkResponseCode === 40022 || error.sdkResponseCode === 40002)
        ) {
      const resp = await agent.enrollUser(appData)

      if(resp) {
        if (resp.redirect) {
          window.open(resp.redirect);
          return resp.Notifier;
        }
      }
      else {
        console.log('Error loading profile: ', error)
        return Promise.reject('Error enrolling');
      }
    }
    else {
      console.log('Error loading profile: ', error)
      return
    }
  })
  console.log("Enrolled!")
}

main();
