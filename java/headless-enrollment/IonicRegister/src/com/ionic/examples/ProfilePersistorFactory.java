package com.ionic.examples;

import com.ionicsecurity.sdk.DeviceProfilePersistorBase;
//import com.ionicsecurity.sdk.DeviceProfilePersistorPassword;
import com.ionicsecurity.sdk.DeviceProfilePersistorPlainText;

/**
* A class that wraps up secure enrollment profile (SEP) persistor specifics and returns the profile persistor that we we have configured.
* @author Ionic Security
*
*/
public class ProfilePersistorFactory {
	
	protected ProfilePersistorFactory() {
	
	}
	
	public  static DeviceProfilePersistorBase<?> getPersistor() {
		DeviceProfilePersistorBase<?> profileLoader;
		// Uncomment the lines below if you are using the passwordProfilePersistor
		// Make sure to comment out the plainTextProfilePersistor lines
		//DeviceProfilePersistorPassword passwordProfilePersistor = new DeviceProfilePersistorPassword();
		//passwordProfilePersistor.setFilePath("SEPsPasswd.txt");
		//passwordProfilePersistor.setPassword("IonicServletPassword");
		//profileLoader = passwordProfilePersistor;
		//return profileLoader;
					
		// The next two lines are used because we're trying to make it easier
		// during development to manage/debug enrollment. This will cause the Agent
		// to persist the SEPs to a plain text file called "SEPS.txt" in the current
		// directory. So to wipe out all SEPs and return to an unregistered state:
		// just delete SEPS.txt.	
		DeviceProfilePersistorPlainText plainTextProfilePersistor = new DeviceProfilePersistorPlainText();
		plainTextProfilePersistor.setFilePath("SEPs.txt");
		profileLoader = plainTextProfilePersistor;
	
		return profileLoader;
	}

}
