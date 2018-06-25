"""
(c) 2018 Ionic Security Inc.

By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html) and the
Privacy Policy (https://www.ionic.com/privacy-notice/).

"""

import ionicsdk

try:
    filepath = input("Please enter the path to the file: ")
    fileinfo = ionicsdk.FileCrypto.getinfo(filepath)

    if fileinfo.isencrypted:
        print ("The file \"{}\" is encrypted using key {}.".format(filepath, fileinfo.keyid))
    else:
        print ("The file \"{}\" is in the clear.".format(filepath))
except ionicsdk.exceptions.IonicException as e:
    print ("Error reading file: {}".format(e.message))

input("\nPress Enter to continue...")
