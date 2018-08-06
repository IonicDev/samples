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
    
    const message = "secret message"

    // initialize agent
    const agent = new window.IonicSdk.ISAgent('https://dev-api.ionic.com/jssdk/latest/');

    // Get all the profiles.
    const response = await agent.queryProfiles(appData).catch((error) => {
        console.log(`Query profiles error: ${error}`)
    })
    const profiles = response.profiles 

    // Verify there is at least one profile.
    if (profiles.length == 0) {
        console.log(`No profiles for this persistor`);
        return
    }
        
    // List all profiles.
    var index, len, passive_device_id
    console.log( `Available Profiles:`);
    for (index = 0, len = profiles.length; index < len; ++index) {
        console.log(`${profiles[index].deviceId}`);

        // Save unactive (passive) profile device ID for later.
        if (! profiles[index].active) {
            passive_device_id = profiles[index].deviceId
        }
    }

    // If the number of profiles is equal to one, then there is nothing to set.
    if (profiles.length == 1) {
        console.log(`Only one profile, nothing to change`);
        return
    }
        
    // Define the profile to make active.
    const profile_to_set =
        { appId: appData.appId,
          userId: appData.userId,
          userAuth: appData.userAuth,
          deviceId: passive_device_id
        }
    
    console.log(`\nSetting ${passive_device_id} as active profile`)

    // Set the active profile.
    const set_active_profile_resp = await agent.setActiveProfile(profile_to_set).catch((error) => {
        console.log(`Set active profile error: ${error}`)
    })

    // Loop through the list of profiles looking for active profile,
    // and output information about the active profile.
    const updated_profiles = set_active_profile_resp.profiles 
    console.log(`\nActive Profile:`);
    for (index = 0, len = updated_profiles.length; index < len; ++index) {
        if (updated_profiles[index].active) {
            console.log(`ID       : ${updated_profiles[index].deviceId}`);
            console.log(`Created  : ${updated_profiles[index].created}`);
            console.log(`Keyspace : ${updated_profiles[index].keyspace}`);
            console.log(`API URL  : ${updated_profiles[index].server}`);
        }
    }
}

main();
