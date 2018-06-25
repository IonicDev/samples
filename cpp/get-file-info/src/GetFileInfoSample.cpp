/*
 * (c) 2018 Ionic Security Inc.
 *
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html) and the
 * Privacy Policy (https://www.ionic.com/privacy-notice/).
 *
*/

#include <iostream>
#include <ISFileCrypto.h>

int main()
{
    std::string filepath;
    ISFileCryptoFileInfo fileinfo;

    std::cout << "Please enter the path to the file: ";
    std::getline(std::cin, filepath);

    ISFileCrypto::getFileInfo(filepath, fileinfo);
    if (fileinfo.isEncrypted())
    {
        std::cout <<  "The file \"" << filepath  << "\" is encrypted using key " << fileinfo.getKeyId() << "." << std::endl;
    }
    else
    {
        std::cout <<  "The file \"" << filepath  << "\" is not encrypted." << std::endl;
    }

    std::cout << "\nPress Enter to continue...";
    getchar();
    return 0;
}