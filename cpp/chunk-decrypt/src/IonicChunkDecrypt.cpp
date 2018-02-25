/*
 * (c) 2017 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

#include "ISAgent.h"
#include "ISChunkCrypto.h"
#include "ISAgentSDKError.h"

int main()
{
	// TODO: Update the below string to your encrypted value from IonicHelloWorld, e.g. "~!2!ABcd...AA!Nk...Wp!"
    std::string encryptedText = "{SET_ENCRYPTED_STRING_HERE}";
	if (encryptedText == "{SET_ENCRYPTED_STRING_HERE}") {
		std::cerr << "You MUST set the output from IonicHelloWorld (or similar) to decrypt." << std::endl;
		std::cout << "Press return to exit.";
		std::getchar();
		return -1;
	}
    
    // Setup an agent object to talk to Ionic
    ISAgent agent;
    agent.initialize();

    // Setup a Chunk Crypto object to handle Ionic encryption
    ISChunkCryptoCipherAuto chunkCrypto(agent);

    // Decrypt the string using an Ionic-managed Key
	std::string decryptedText = "";
    int nErrorCode = chunkCrypto.decrypt(encryptedText, decryptedText);

    // Validate the response
    if (nErrorCode != ISCRYPTO_OK) {
        std::cerr << "Error decrypting: " << ISAgentSDKError::getErrorCodeString(nErrorCode) << std::endl;
		std::cout << "Press return to exit.";
		std::getchar();
		return -2;
    }

    std::cout << "Chunk-Encrypted String: " << encryptedText << std::endl;
    std::cout << "Decrypted String: " << decryptedText << std::endl;

	std::cout << "Press return to exit.";
	std::getchar();
    return 0;
}