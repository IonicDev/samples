/*
 * (c) 2017 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://www.ionic.com/terms-of-use/)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

#include <ISAgentSDKC.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

int main() 
{    
	// TODO: Update the below string to your encrypted value from IonicHelloWorld, e.g. "~!2!ABcd...AA!Nk...Wp!"
    char *encryptedText = "{SET_ENCRYPTED_STRING_HERE}";
	if (strcmp(encryptedText, "{SET_ENCRYPTED_STRING_HERE}") == 0) {
		printf("You MUST set the output from ionic-helloworld (or similar) to decrypt.\n");
		exit(-1);
	}

    // Setup an agent object to talk to Ionic
    ionic_profile_persistor_t *profileLoaderOpt = ionic_profile_persistor_create_default();
    ionic_agent_t *agent = ionic_agent_create(profileLoaderOpt, NULL);

    // Setup a Chunk Crypto object to handle Ionic encryption
    ionic_chunkcipher_t *chunkCrypto = ionic_chunkcipher_create_auto(agent);

    // Decrypt the string using an Ionic-managed Key
    char *plaintext;
    int nErrorCode = ionic_chunkcipher_decrypt_str(chunkCrypto, encryptedText, &plaintext);

    // Validate the response
    if (nErrorCode != ISC_OK) {
        printf("Error decrypting: %s\n", ionic_get_error_str(nErrorCode));
		exit(-2);
    }

    // Show your plaintext and ciphertext
    printf("Chunk-Encrypted String: %s\n", encryptedText);
    printf("Decrypted String: %s\n", plaintext);
    
    return 0;
}
