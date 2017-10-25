/*
 * (c) 2017 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://www.ionic.com/terms-of-use/)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

package com.ionic.examples;

import com.ionicsecurity.sdk.DeviceProfilePersistorBase;
// NOTE: Import the types of persistors based on what is chosen for your use-case:
import com.ionicsecurity.sdk.DeviceProfilePersistorPlainText;
//import com.ionicsecurity.sdk.DeviceProfilePersistorPassword;
//import com.ionicsecurity.sdk.DeviceProfilePersistorAesGcm;

/**
* A class that wraps up secure enrollment profile (SEP) persistor specifics and returns the
* profile persistor that we we have configured.
*/
public class ProfilePersistorFactory {
	
	protected ProfilePersistorFactory() {
	
	}
	
	public  static DeviceProfilePersistorBase<?> getPersistor() {
		DeviceProfilePersistorBase<?> profileLoader;
		
		// NOTE: Typically the file path for a persistor should be set to the locations as recommended
		// in the documentation on dev.ionic.com. See details there about persistors for more information.
		
		// NOTE: Uncomment the lines below if you are using the passwordProfilePersistor,
		// and be sure to comment out the plainTextProfilePersistor lines.
		
		//DeviceProfilePersistorPassword passwordProfilePersistor = new DeviceProfilePersistorPassword();
		//passwordProfilePersistor.setFilePath("profiles.pw");
		//passwordProfilePersistor.setPassword("DoNotHardcodeYourPasswordHere");
		//profileLoader = passwordProfilePersistor;
		//return profileLoader;
					
		// The next two lines are used because we're trying to make it easier
		// during development to manage/debug enrollment. Do NOT use this in production.
		// This will cause the Agent to persist the SEPs to a plain text file called "profiles.pt"
		// in the current directory.
		// To wipe out all SEPs and return to an unregistered state, just delete profiles.pt.
		DeviceProfilePersistorPlainText plainTextProfilePersistor = new DeviceProfilePersistorPlainText();
		plainTextProfilePersistor.setFilePath("profiles.pt");
		profileLoader = plainTextProfilePersistor;
	
		return profileLoader;
	}

}
