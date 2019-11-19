/*
 * (c) 2018-2020 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

/*
 * WARNING *
 * Calling agent.enrollUser() successfully is a pre-requisite before using this code.
 * This is done enrollDevice.js.
 */

import {getAgentConfig} from '../jssdkConfig.js';

const main = async () => {

  // initialize agent
  try {
    const appData = getAgentConfig('Javascript Set Active Profile');

    const resp = await new window.IonicSdk.ISAgent(appData);
    const agent = resp.agent;

    console.log('');
    let profiles;

    // Get all the profiles.
    try {
      const response = await agent.queryProfiles(appData);
      profiles = response.profiles;
    } catch (sdkErrorResponse) {
        console.log('Error querying profiles: ' + sdkErrorResponse.error);
    }
  
    // Verify there is at least one profile.
    if (profiles.length === 0) {
        console.log(`No profiles for this persistor`);
        return;
    }
  
    // List all profiles.
    let index, len, passive_device_id;
    console.log('Available Profiles: ');
    for (index = 0, len = profiles.length; index < len; ++index) {
      console.log('ID       : ' + profiles[index].deviceId);
      console.log('Created  : ' + profiles[index].created);
      console.log('Keyspace : ' + profiles[index].keyspace);
      console.log('API URL  : ' + profiles[index].server);
      console.log('-----------------------------------------------------------');
  
      // Save unactive (passive) profile device ID for later.
      if (! profiles[index].active) {
        passive_device_id = profiles[index].deviceId
      }
    }
  
    // If the number of profiles is equal to one, then there is nothing to set.
    if (profiles.length === 1) {
        console.log('Only one profile, nothing to change');
        return
    }
  
    // Define the profile to make active.
    const profile_to_set =
      { appId: appData.appId,
        userId: appData.userId,
        userAuth: appData.userAuth,
        deviceId: passive_device_id
      }
  
    console.log('Setting ' + passive_device_id + ' as active profile')
  
    // Set the active profile.
    let set_active_profile_resp;
    try {
      set_active_profile_resp = await agent.setActiveProfile(profile_to_set);
    } catch (sdkErrorResponse) {
        console.log('Set active profile error: ' + sdkErrorResponse.error);
    }
  
    // Loop through the list of profiles looking for active profile,
    // and output information about the active profile.
    const updated_profiles = set_active_profile_resp.profiles
    console.log('Active Profile: ');
    for (index = 0, len = updated_profiles.length; index < len; ++index) {
      if (updated_profiles[index].active) {
        console.log('ID       : ' + updated_profiles[index].deviceId);
        console.log('Created  : ' + updated_profiles[index].created);
        console.log('Keyspace : ' + updated_profiles[index].keyspace);
        console.log('API URL  : ' + updated_profiles[index].server);
      }
    }
  } catch (sdkErrorResponse) {
    console.log('Obtaining agent error: ' + sdkErrorResponse.error);
  }
}

main();
