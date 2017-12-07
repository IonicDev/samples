/*
 * (c) 2017 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://www.ionic.com/terms-of-use/)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

#include <ISAgentSDKC.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

void setupLogging();
bool makeLinuxHomeDirPath(char *pathFromHomeDir, char *plainSepPath);

int main() 
{
    setupLogging();

    // Setup an agent object to talk to Ionic
#if __linux__
        //NOTE: On Linux, you must add additional code here, see "Getting Started" for C++ on Linux.
        char *plainSepPath;
        if (!makeLinuxHomeDirPath(".ionicsecurity/profiles.pt", plainSepPath)) { // Makes the absolute path for ~/.ionic/profiles.pt
            printf("Error getting home directory path.\n");
            exit(-1);
        }
        ionic_profile_persistor_t *plainPersistor = ionic_profile_persistor_create_plaintext_file(plainSepPath);
        printf("Initializing agent...\n");
        ionic_agent_t *agent = ionic_agent_create(plainPersistor, NULL);

        printf("A plaintext SEP was loaded from %s\n", plainSepPath);
#else
        // Setup an agent object to talk to Ionic
        printf("Initializing agent...\n");
        ionic_profile_persistor_t *profileLoaderOpt = ionic_profile_persistor_create_default();
        ionic_agent_t *agent = ionic_agent_create(profileLoaderOpt, NULL);

#endif

	// Check if there are profiles.
	if (ionic_agent_has_any_profiles(agent) == false) {
		printf("There are no device profiles on this device.\n");
        printf("Register a device before continuing.\n");
		exit(-2);
    }

    // Setup a Chunk Crypto object to handle Ionic encryption
    ionic_chunkcipher_t *chunkCrypto = ionic_chunkcipher_create_auto(agent);

    // Set input string
    const char *input = "Hello World!";
    
    // Encrypt the string using an Ionic-managed Key
    char *encryptedText;
    int nErrorCode = ionic_chunkcipher_encrypt_str(chunkCrypto, input, &encryptedText); 
	
    // Validate the response
    if (nErrorCode != ISC_OK) {
        printf("Error encrypting: %s\n", ionic_get_error_str(nErrorCode));
		exit(-3);
    }
    
    // Show your plaintext and ciphertext
    printf("Plain Text: %s\n", input);
    printf("Ionic Chunk Encrypted Text: %s\n", encryptedText);

    // Release memory allocated by Ionic SDK
    int releaseError = ionic_release(encryptedText);
    if (releaseError != ISC_OK) {
        printf("Error freeing memory: %s\n", ionic_get_error_str(releaseError));
		exit(-4);
    }

    return 0;
}

// UTILITY FUNCTIONS

void setupLogging() 
{
    // Set up logging
    // Create file to write log to
    const char *pszOutputLogFile = "log.txt";
    int logError = ionic_log_setup_simple(pszOutputLogFile, false, ISLOG_SEV_DEBUG); 
    if (logError != ISC_OK) {
        printf("Error setting up log: %s\n", ionic_get_error_str(logError));
		exit(-1);
    }
}

bool makeLinuxHomeDirPath(char *pathFromHomeDir, char *plainSepPath) 
{
    char *tmp = getenv("HOME");
    if (tmp == NULL) {
        return false;
    } else {
        plainSepPath = strcat(tmp, "/");
        plainSepPath = strcat(plainSepPath, pathFromHomeDir);
        return true;
    }
}
