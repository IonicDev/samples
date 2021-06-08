/*
 * (c) 2018-2021 Ionic Security Inc.
 *
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html) and the
 * Privacy Policy (https://www.ionic.com/privacy-notice/).
 *
*/

#include <iostream>
#include <ISFileCrypto.h>
#include <ISAgentSDKError.h>

int main()
{
    int nErrorCode;

    // prompt user for path to file
    std::string filepath;
    std::cout << "Please enter the path to the file: ";
    std::getline(std::cin, filepath);

    // get file info
    ISFileCryptoFileInfo fileinfo;
    nErrorCode = ISFileCrypto::getFileInfo(filepath, fileinfo);
    if (nErrorCode != ISCRYPTO_OK) {
        std::cerr << "Failed to get file info: " << ISAgentSDKError::getErrorCodeString(nErrorCode) << std::endl;
        exit(1);
    }
    if (fileinfo.isEncrypted()) {
        std::cout <<  "The file \"" << filepath  << "\" is encrypted using key " << fileinfo.getKeyId() << "." << std::endl;
    }
    else {
        std::cout <<  "The file \"" << filepath  << "\" is not encrypted." << std::endl;
    }

    std::cout << "\nPress Enter to continue...";
    getchar();
    return 0;
}
