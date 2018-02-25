/*
 * (c) 2017 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

#include <iostream>
#include <ISAgent.h>
#include <ISFileCrypto.h>
#include <ISAgentSDKError.h>

int main()
{
    std::string inFilePath("example_file.docx");
    std::string outFilePath("example_file_encrypted.docx");
    
    // Setup an agent object to talk to Ionic
    ISAgent agent;
    agent.initialize();
    
    // Create a classification
    std::map< std::string, std::vector< std::string > > map;
    map["classification"].push_back("Restricted");
    
    // Apply the classification
    ISFileCryptoEncryptAttributes attribs;
    attribs.setKeyAttributes(map);
    
    // Encrypt the file using the classification
    ISFileCryptoCipherAuto cipher(agent);
	int nErrorCode = cipher.encrypt(inFilePath, outFilePath, &attribs);
	
	// Validate the response
	if (nErrorCode != ISCRYPTO_OK) {
		std::cerr << "Error encrypting " << inFilePath << ": " << ISAgentSDKError::getErrorCodeString(nErrorCode) << std::endl;
		std::cout << "Press return to exit.";
		std::getchar();
		return -2;
	}

	std::cout << "Encrypted file " << inFilePath << " to " << outFilePath << "." << std::endl;
	std::cout << "Press return to exit.";
	std::getchar();
	return 0;
}
