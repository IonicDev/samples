/*
 * (c) 2017 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://www.ionic.com/terms-of-use/)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

#include <ISAgentSDKC.h>
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

    // Request keys
    // Create and apply classification
    ionic_attributesmap_t *attrsMap = ionic_attributesmap_create();
    int nAttrError = ionic_attributesmap_set(attrsMap, "classification", "Restricted");
    if (nAttrError != ISC_OK) {
        printf("Error setting attribute map: %s\n", ionic_get_error_str(nAttrError));
        exit(-3);
    }

    // Forming the key request
    // Declare an array object to put the keys in:
    ionic_key_data_array_t *keyDataArrayOut;
    // Now ask the server to make a number of keys... 2 in this case:
    int createKeyCode = ionic_agent_create_keys(agent, attrsMap, 2, NULL, &keyDataArrayOut, NULL);
    if (createKeyCode != ISC_OK) {
        printf("Error creating keys: %s\n", ionic_get_error_str(createKeyCode));
        exit(-4);
    }

    // Show us what keys we got (you can always get a key right when you create it):
    char *keyIds[keyDataArrayOut->nSize];
    for(int i = 0; (i < (int)keyDataArrayOut->nSize); i++) {
        char *id = keyDataArrayOut->ppKeyArray[i]->pszKeyId;
        keyIds[i] = id;
        printf("We created a key with the Key Tag: %s\n", keyIds[i]);
    }


    // The rest of this program would typically happen at a different time,
    //  not right after creating the keys, but when you were going to access
    //  the data protected by those keys.

    // Now, using the Key Tags, ask the server for those keys again:
    // NOTE: We populated the 'keyIds' array of keytags in the above loop.
    ionic_key_data_array_t *keyDataArrayOutSecondary;
    int getKeyCode = ionic_agent_get_keys(agent, keyIds, 2, NULL, &keyDataArrayOutSecondary, NULL);
    if (getKeyCode != ISC_OK) {
        printf("Error getting keys: %s\n", ionic_get_error_str(getKeyCode));
        exit(-5);
    }

    // Show what we got access to after a request for keys:
    char *keyIdsSecondary[keyDataArrayOutSecondary->nSize];
    for(int j = 0; (j < (int)keyDataArrayOutSecondary->nSize); j++) {
        char *idSecondary = keyDataArrayOutSecondary->ppKeyArray[j]->pszKeyId;
        keyIdsSecondary[j] = idSecondary;
        printf("We fetched a key with the Key Tag: %s\n", keyIdsSecondary[j]);
    }

    // Tell us if we got less keys when we fetched than we created.
    //  This would happen if policy didn't give us access to all the keys.
    if (sizeof(keyIdsSecondary) < sizeof(keyIds)) {
        printf("We didn't get given all of the requested keys.\n");
        exit(-6);
    }

    // Release memory allocated by Ionic SDK
    int releaseError = ionic_release(keyDataArrayOut);
    if (releaseError != ISC_OK) {
        printf("Error freeing memory: %s\n", ionic_get_error_str(releaseError));
        exit(-7);
    }
    // Release memory allocated by Ionic SDK
    releaseError = ionic_release(keyDataArrayOutSecondary);
    if (releaseError != ISC_OK) {
        printf("Error freeing memory: %s\n", ionic_get_error_str(releaseError));
        exit(-7);
    }

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
