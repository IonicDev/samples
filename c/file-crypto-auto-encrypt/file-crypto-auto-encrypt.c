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

int main()
{
    // Can be done with other file types, too, like .docx, etc.
	char inFilePath[] = "example_file.txt";
	char outFilePath[] = "example_file_encrypted.txt";
    
    // Setup an agent object to talk to Ionic
    ionic_profile_persistor_t *profileLoaderOpt = ionic_profile_persistor_create_default();
    ionic_agent_t *agent = ionic_agent_create(profileLoaderOpt, NULL);
    
    /*
     * NOTE: Ensure you have an active profile set. You can do this by downloading the
     * Ionic Manager application and following the steps for enrolling your device.
     */

    // Create and apply classification
    ionic_attributesmap_t *attrsMap = ionic_attributesmap_create();
    int nAttrError = ionic_attributesmap_set(attrsMap, "classification", "Restricted");
    if (nAttrError != ISC_OK) {
        printf("Error setting attribute map: %s\n", ionic_get_error_str(nAttrError));
		exit(-1);
    }
    
    // Encrypt the file using the classification
	ionic_filecipher_t *fileCipher = ionic_filecipher_create_auto(agent);
	int nErrorCode = ionic_filecipher_encrypt2(fileCipher, inFilePath, outFilePath, attrsMap, NULL, NULL, NULL, NULL);
	
	// Validate the response
	if (nErrorCode != ISC_OK) {
		printf("Error encrypting: %s\n", ionic_get_error_str(nErrorCode));
		exit(-2);
	}

	printf("Encrypted file %s to %s\n", inFilePath, outFilePath);
	return 0;
}
