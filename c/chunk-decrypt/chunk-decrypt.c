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
	// TODO: Update the below string to your encrypted value from IonicHelloWorld, e.g. "~!2!ABcd...AA!Nk...Wp!"
    char *encryptedText = "{SET_ENCRYPTED_STRING_HERE}";
	if (strcmp(encryptedText, "{SET_ENCRYPTED_STRING_HERE}") == 0) {
		printf("You MUST set the output from ionic-helloworld (or similar) to decrypt.\n");
		exit(-1);
	}

    // Setup an agent object to talk to Ionic
#if __linux__
        //NOTE: On Linux, you must add additional code here, see "Getting Started" for C++ on Linux.
        char *plainSepPath;
        if (!makeLinuxHomeDirPath(".ionicsecurity/profiles.pt", plainSepPath)) { // Makes the absolute path for ~/.ionic/profiles.pt
            printf("Error getting home directory path.\n");
            exit(-2);
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

    // Setup a Chunk Crypto object to handle Ionic encryption
    ionic_chunkcipher_t *chunkCrypto = ionic_chunkcipher_create_auto(agent);

    // Decrypt the string using an Ionic-managed Key
    char *plaintext;
    int nErrorCode = ionic_chunkcipher_decrypt_str(chunkCrypto, encryptedText, &plaintext);

    // Validate the response
    if (nErrorCode != ISC_OK) {
        printf("Error decrypting: %s\n", ionic_get_error_str(nErrorCode));
		exit(-3);
    }

    // Show your plaintext and ciphertext
    printf("Chunk-Encrypted String: %s\n", encryptedText);
    printf("Decrypted String: %s\n", plaintext);
    
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
