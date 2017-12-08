/*
 * (c) 2017 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://www.ionic.com/terms-of-use/)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

#include <stdio.h>
#include <ISAgent.h>

bool makeLinuxHomeDirPath(std::string pathFromHomeDir, std::string & fullPath);

int main()
{
    // Setup an agent object to talk to Ionic
    ISAgent agent;
    int nErrorCode;
#if __linux__
    //NOTE: On Linux, you must add additional code here, see "Getting Started" for C++ on Linux.
    std::string plainSepPath;
    if (!makeLinuxHomeDirPath(".ionicsecurity/profiles.pt", plainSepPath)) { // Makes the absolute path for ~/.ionic/profiles.pt
        std::cerr << "Error getting home directory path." << std::endl;
        return -1;
    }
    ISAgentDeviceProfilePersistorPlaintext plainPersistor;
    plainPersistor.setFilePath(plainSepPath);
    std::cout << "Initializing agent..." << std::endl;
    nErrorCode = agent.initialize(plainPersistor);
    if (nErrorCode != ISAGENT_OK) {
        std::cerr << "Error initializing agent: " << ISAgentSDKError::getErrorCodeString(nErrorCode) << std::endl;
    } else {
        std::cout << "A plaintext SEP was loaded from " << plainSepPath << std::endl;
    }
#else
    nErrorCode = agent.initialize();
    if (nErrorCode != ISAGENT_OK) {
        std::cerr << "Error initializing agent: " << ISAgentSDKError::getErrorCodeString(nErrorCode) << std::endl;
    }
#endif

	// Check if there are profiles.
	if (!agent.hasAnyProfiles()) {
		std::cout << "There are no device profiles on this device." << std::endl;
		std::cout << "Register a device before continuing." << std::endl;
		return -1;
	}

    // Request keys
    // Forming the key request object
    ISAgentCreateKeysRequest keyRequest;
    // Here update request.getKeys() as the list of what it should create.
    AttributesMap mapKeyAttrs;
    mapKeyAttrs["classification"].push_back("restricted");
    ISAgentCreateKeysRequest::Key requestKey("example", 2, mapKeyAttrs);
    keyRequest.getKeys().push_back(requestKey);
    // Now ask the server to make those keys:
    ISAgentCreateKeysResponse keyResponse;
    agent.createKeys(keyRequest, keyResponse);

    // Show us what keys we got (you can always get a key right when you create it):
    std::vector<ISAgentCreateKeysResponse::Key> responseKeys = keyResponse.getKeys();
	ISAgentGetKeysRequest fetchRequest; //we will use this to track the keys we want to fetch later
	for each (ISAgentCreateKeysResponse::Key responseKey in responseKeys)
	{
		std::cout << "We created a key with the Key Tag: " << responseKey.getId() << std::endl;
		fetchRequest.getKeyIds().push_back(responseKey.getId());
	}

    // The rest of this program would typically happen at a different time,
    //  not right after creating the keys, but when you were going to access
    //  the data protected by those keys.

	// Now, using the Key Tags, ask the server for those keys again:
	// NOTE: We populated fetchRequest's list of keytags in the above loop.
	ISAgentGetKeysResponse fetchResponse;
	int nErrorCode = agent.getKeys(fetchRequest, fetchResponse);
	if (ISAGENT_OK != nErrorCode) {
		std::cout << "Error fetching keys: " << nErrorCode << std::endl;
		return -3;
	}
	// Show what we got access to after a request for keys:
	for each (ISAgentGetKeysResponse::Key responseKey in fetchResponse.getKeys())
	{
		std::cout << "We fetched a key with the Key Tag: " << responseKey.getId() << std::endl;
	}

	// Tell us if we got less keys when we fetched than we created.
	//  This would happen if policy didn't give us access to all the keys.
	if (fetchResponse.getKeys().size() < fetchRequest.getKeyIds().size()) {
		std::cout << "We didn't get given all of the requested keys." << std::endl;
		return -4;
	}

	getchar();
    return 0;
}

bool makeLinuxHomeDirPath(std::string pathFromHomeDir, std::string & fullPath) {
    char const* tmp = getenv("HOME");
    if (tmp == NULL) {
        return false;
    } else {
        fullPath = std::string(tmp) + "/" + pathFromHomeDir;
        return true;
    }
}
