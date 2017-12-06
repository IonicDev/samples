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
#include <errno.h>

int main()
{
	// Can be done with other file types, too, like .docx, etc.
	char inFilePath[] = "example_file_encrypted.txt";
	char outFilePath[] = "example_file_decrypted.txt";

	// Setup an agent object to talk to Ionic
    ionic_profile_persistor_t *profileLoaderOpt = ionic_profile_persistor_create_default();
    ionic_agent_t *agent = ionic_agent_create(profileLoaderOpt, NULL);

	//Encrypt the file using the classification
	ionic_filecipher_t *fileCipher = ionic_filecipher_create_auto(agent);
	int nErrorCode = ionic_filecipher_decrypt(fileCipher, inFilePath, outFilePath);

	// Validate the response
	if (nErrorCode != ISC_OK) {
		printf("Error decrypting: %s\n", ionic_get_error_str(nErrorCode));
		exit(-2);
	}

	printf("Decrypted file %s to %s\n", inFilePath, outFilePath);

	return 0;
}