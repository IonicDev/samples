/*
 * (c) 2018-2020 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

// This URL specifies the Machina enrollment endpoint URL. There are 2 formats below:
// Ionic Authentication, and multiple authentication selection.  For free tenants, it
// is highly recommended to use the Ionic Authentication enrollment endpoint URL.
// This code enrolls a browser device by using the keyspace specified in the
// text input. The keyspace is used to obtain the tenant ID and teh enrollment URL.
// The enrollment URL is for different ways to authenticate.  For free tenants, it
// is recommended to select 'Ionic Authentication'.
//

import {getAgentConfig} from '../jssdkConfig.js';

// Handles key press event for text input.
// Process 'Enter' key like clicking submit button.
function keyPress (event) {

  if (event.key === 'Enter') {
    keyspaceSubmit(event);
  }
}

// Handles the keyspace submit button click.
// Obtains keyspace from text input and process.
async function keyspaceSubmit () {

  const keyspace = document.getElementById('keyspaceText').value;
  document.getElementById('keyspaceText').value = '';

  // Validate keyspace.
  const knsJson = await getKeyspaceInfo(keyspace);
  if (knsJson.code !== undefined) {
    console.log('Invalid keyspace: ' + keyspace);
    return;
  }

  // Keyspace valid, pull out tenant ID and and enrollment URL.
  const tenantId = knsJson.answers.tenantid[0];
  const enrollUrl = knsJson.answers.enroll[0];
  console.log('Keyspace ' + keyspace + ' and tenant ' + tenantId + ' enrolls at: ' + enrollUrl + '.');

  enrollDevice(enrollUrl, keyspace, tenantId);
}

// Check with the Key Naming Server (KNS) to see if the keyspace is valid.
async function getKeyspaceInfo (keyspace) {

  const response = await fetch('https://api.ionic.com/v2.4/kns/keyspaces/' + keyspace);
  const json = await response.json();
  return json;
}

// Enroll the device, but first check if we're already enrolled.
async function enrollDevice (enrollUrl, keyspace, tenantId) {

  // Get the standard appData and add enrollmentURL.
  let appData = getAgentConfig('Javascript Enroll Device');
  appData = Object.assign(appData, {enrollmentUrl: enrollUrl});

  // Initialize Ionic Agent.
  const agent = new window.IonicSdk.ISAgent();

  // Load the user.  If an error, then enroll the user.
  try {
    const profilesResult = await agent.loadUser(appData);
    const browserProfiles = profilesResult.profiles;

    const profile = browserProfiles.find((browserProfile) => (browserProfile.keyspace === keyspace));
    if (profile !== undefined) {
      console.log('Already enrolled for keyspace ' + keyspace + ' and tenant ' + tenantId + ' with app ' + appData.appId + ' and user ' + appData.userId + '.');
      return;
    }
  } catch (errorResp) {
    // Check for error 40022, "No active device profile is set".
    if (errorResp.sdkResponseCode !== 40022) {
      console.log('Error with loadUser(): ' + errorResp.error);
      return;
    }
  }

  // Since it has been determined that we are not enrolled, so enroll the device.
  try {
    // enrollUser() returns a redirect URL.
    let resp = await agent.enrollUser(appData);
    if (resp.redirect) {
      const enrollWindow = window.open(resp.redirect);

      // Now wait for the enrollment to finish.
      try {
        await resp.notifier;
        console.log('Enrolled for keyspace ' + keyspace + ' and tenant ' + tenantId + ' with app ' + appData.appId + ' and user ' + appData.userId + '.');
        enrollWindow.close();
      } catch (errorResp) {
        console.log('Error with enrollment response: ' + errorResp.error);
      }
      
    }
  } catch (errorResp) {
    console.log('Error with enrollUser(): ' + errorResp.error);
    return;
  }
}

// Add the event handlers for submit button and text input..
let submitButton = document.getElementById('submitButton');
submitButton.addEventListener('click', keyspaceSubmit);
let keyspaceInput = document.getElementById('keyspaceText');
keyspaceInput.addEventListener('keypress', keyPress);

