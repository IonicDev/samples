/*
 * (c) 2017 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://www.ionic.com/terms-of-use/)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

#include <ISAgentSDKC.h>
#include <ISAgentSDKCFileCrypto.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

bool makeLinuxHomeDirPath(char *pathFromHomeDir, char *plainSepPath);

int main()
{
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

    // Can be done with other file types, too, like .docx, etc.
    char inFilePath[] = "example_file.txt";
    char outFilePath[] = "example_file_encrypted.txt";

    // Create and apply classification
    ionic_attributesmap_t *attrsMap = ionic_attributesmap_create();
    int nAttrError = ionic_attributesmap_set(attrsMap, "classification", "Restricted");
    if (nAttrError != ISC_OK) {
        printf("Error setting attribute map: %s\n", ionic_get_error_str(nAttrError));
        exit(-3);
    }
    
    // Encrypt the file using the classification
    ionic_filecipher_t *fileCipher = ionic_filecipher_create_auto(agent);
    int nErrorCode = ionic_filecipher_encrypt2(fileCipher, inFilePath, outFilePath, attrsMap, NULL, NULL, NULL, NULL);
    
    // Validate the response
    if (nErrorCode != ISC_OK) {
        printf("Error encrypting: %s\n", ionic_get_error_str(nErrorCode));
        exit(-4);
    }

    printf("Encrypted file %s to %s\n", inFilePath, outFilePath);
    return 0;
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
