# (c) 2018 Ionic Security Inc.
# By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
# and the Privacy Policy (https://www.ionic.com/privacy-notice/).

import os
import sys
import ionicsdk

plaintext_file = '../../sample-data/files/Message.pdf'
protected_file = '../../sample-data/files/protected-sample.txt'
source_dir = 'github-samples/python/get-file-info'
this_dir = os.getcwd()

# run only from source directory
if not this_dir.endswith(source_dir):
    print ("[!] Please run this sample from inside" + source_dir)
    sys.exit(1)

print ("plaintext file: " + plaintext_file)
print ("protected file: " + protected_file)

# get file info
try:
    for filepath in [plaintext_file, protected_file]:
        fileinfo = ionicsdk.FileCrypto.getinfo(filepath)

        # display file info
        if fileinfo.isencrypted:
            print ("The file \"{}\" is encrypted using key {}.".format(filepath, fileinfo.keyid))
        else:
            print ("The file \"{}\" is not encrypted.".format(filepath))
except ionicsdk.exceptions.IonicException as e:
    print ("Error reading file: {}".format(e.message))
    sys.exit(1)
