# (c) 2018-2019 Ionic Security Inc.
# By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
# and the Privacy Policy (https://www.ionic.com/privacy-notice/).

from __future__ import print_function

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

# prompt user for path to file  wrap input in quotes for python2.7
try:
    file_path = input("Please enter the path to the file: ")
except NameError as e:
    print ("Oops! Please wrap that path in quotes.")
    sys.exit(1)

# get file info
try:
    file_info = ionicsdk.FileCrypto.getinfo(file_path)
except ionicsdk.exceptions.IonicException as e:
    print ("Error reading file: {}".format(e.message))
    sys.exit(1)

# display file info
if file_info.isencrypted:
    print ("The file \"{}\" is encrypted using key {}.".format(file_path, file_info.keyid))
else:
    print ("The file \"{}\" is not encrypted.".format(file_path))
