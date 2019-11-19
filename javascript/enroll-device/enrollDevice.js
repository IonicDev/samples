/*
 * (c) 2018-2020 Ionic Security Inc.
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
// https://enrollment.ionic.com/keyspace/HvzG/idc/6d8d832785f3a66824ae2c23/default/register
//
// Multiple authentication format:
// https://<domain>/keyspace/<keyspace>/register
// Example:
// https://preview-enrollment.ionic.com/keyspace/HVzG/register

// Another code enrollment code example is at: https://api.ionic.com/jssdk/latest/Docs/tutorial-helloWorld_index.js.html

import {getAgentConfig} from '../jssdkConfig.js';
import {getKeyspace} from '../jssdkConfig.js';
import {getTenant} from '../jssdkConfig.js';

// Please modify keyspace and tenant to your keyspace and tennant in jssdkConfig.js.
const keyspace = getKeyspace();
const tenantId = getTenant();
const enrollmentUrl = 'https://enrollment.ionic.com/keyspace/' + keyspace + '/idc/' + tenantId + '/default/register';

const main = async () => {

  // initialize agent
  const agent = new window.IonicSdk.ISAgent();

  // Get the appData and add enrollment URL.
  let appData = getAgentConfig('Javascript Enroll Device');
  appData = Object.assign(this.appData, {enrollmentUrl: this.enrollmentUrl});

  // Load the user.  If an error, then enroll the user.
  try {
    await agent.loadUser(appData);
    console.log('Already enrolled for app "' + appData.appId + '" and  user "' + appData.userId + '" in keyspace "' + keyspace + '" and tenant "' + tenantId + '".');
  } catch (error) {
    try {
      // enrollUser() returns a redirect URL.
      let resp = await agent.enrollUser(appData);
      if (resp.redirect) {
        const enrollWindow = window.open(resp.redirect);

        // Now wait for the enrollment to finish.
        try {
          await resp.notifier;
          console.log('Enrolled for app "' + appData.appId + '" and  user "' + appData.userId + '" in keyspace "' + keyspace + '" and tenant "' + tenantId + '".');
          enrollWindow.close();
        } catch (error) {
          console.log('Error with enrollment response: ' + error)
        }
        
      }
    } catch (error) {
      console.log('Error loading profile: ' + error);
      return;
    }
  }
}

main();
