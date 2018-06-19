/*
 * (c) 2017 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

#include "ISAgent.h"
#include "ISAgentSDKError.h"
#include <iostream>

int convertSepDefaultToPassword(std::string passwordSepPath, std::string passwordSepPassword)
{
	// Temporary variables to hold profiles during conversion.
	std::vector<ISAgentDeviceProfile> vecProfiles;
	std::string sActiveDeviceId;

	// First create an default persistor that will read in a SEP that you have with your platform's default persistor,
	// for example, one that you created using the Ionic Manager enrollment tool.
	ISAgentDeviceProfilePersistorDefault defaultPersistor;
	int nErrorCode = defaultPersistor.loadAllProfiles(vecProfiles, sActiveDeviceId);
	if (nErrorCode != ISAGENT_OK)
	{
		std::cerr << "Error loading profiles from default SEP persistor: " << ISAgentSDKError::getErrorCodeString(nErrorCode) << std::endl;
		return -1;
	}

	std::cout << vecProfiles.size() << " profiles loaded by the default persistor." << std::endl;

	// Now we create another persistor that outputs the profiles we loaded in a password protected SEP
	ISAgentDeviceProfilePersistorPassword passwordPersistor;
	passwordPersistor.setFilePath(passwordSepPath);
	passwordPersistor.setPassword(passwordSepPassword);

	// Save the profiles using the password persistor.
	nErrorCode = passwordPersistor.saveAllProfiles(vecProfiles, sActiveDeviceId);
	if (nErrorCode != ISAGENT_OK) {
		std::cerr << "Error saving profiles with the password-protected SEP: " << ISAgentSDKError::getErrorCodeString(nErrorCode) << std::endl;
		return -2;
	}

	std::cout << "Profiles saved to password persistor, in encrypted file " << passwordSepPath << std::endl;
	return 0;
}

int main(int argc, char* argv[])
{
	// Set constants to use for the ISAgentDeviceProfilePersistorPassword
	std::string passwordSepPath = "profiles.pw"; //NOTE: On Linux, this file should typically be placed in ~/.ionicsecurity/profiles.pw
	std::string passwordSepPassword = "theSecretPasswordShouldNotBeHardcoded!";

    // First create an agent that will try to read in the SEP stored with a password persistor.
	ISAgentDeviceProfilePersistorPassword passwordPersistor;
	passwordPersistor.setFilePath(passwordSepPath);
	passwordPersistor.setPassword(passwordSepPassword);

	// Try reading with this persistor, to see if we have a password persisted SEP already:
	std::vector<ISAgentDeviceProfile> vecProfiles;
	std::string sActiveDeviceId;
	int nErrorCode = passwordPersistor.loadAllProfiles(vecProfiles, sActiveDeviceId);
	if (nErrorCode != ISAGENT_OK)
	{
		std::cerr << "Error loading from password persistor: " << ISAgentSDKError::getErrorCodeString(nErrorCode) << std::endl;
		if (nErrorCode == ISAGENT_RESOURCE_NOT_FOUND)
		{
			std::cerr << "The file for the password persistor was not found at " << passwordSepPath << "." << std::endl;
		}
		std::cout << "We will now try to convert a default persistor SEP to the password persisted SEP for you." << std::endl;
		if (0 != convertSepDefaultToPassword(passwordSepPath, passwordSepPassword))
		{
			std::getchar();
			return -2;
		}
		std::cout << "A password protected SEP should now exist." << std::endl;
	}

	// Now that we have a password protected SEP, we can load it as we normally would when intitializing an agent:
    ISAgent agent;
	nErrorCode = agent.initialize(passwordPersistor);
	if (nErrorCode != ISAGENT_OK)
	{
		std::cerr << "Error initalizing agent: " << ISAgentSDKError::getErrorCodeString(nErrorCode) << std::endl;
	}
	else
	{
		std::cout << "A password protected SEP was loaded from " << passwordSepPath << std::endl;
		std::cout << agent.getAllProfiles().size() << " were loaded." << std::endl;
	}

    std::getchar();
	return 0;
}
