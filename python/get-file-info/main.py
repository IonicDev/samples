# (c) 2018 Ionic Security Inc.
# By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
# and the Privacy Policy (https://www.ionic.com/privacy-notice/).

import ionicsdk

# prompt user for path to file
filepath = input("Please enter the path to the file: ")

# get file info
try:
    fileinfo = ionicsdk.FileCrypto.getinfo(filepath)
except ionicsdk.exceptions.IonicException as e:
    print ("Error reading file: {}".format(e.message))
    sys.exit(1)

# display file info
if fileinfo.isencrypted:
    print ("The file \"{}\" is encrypted using key {}.".format(filepath, fileinfo.keyid))
else:
    print ("The file \"{}\" is not encrypted.".format(filepath))

raw_input("\nPress Enter to continue...")
