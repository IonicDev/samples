# (c) 2018-2019 Ionic Security Inc.
# By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
# and the Privacy Policy (https://www.ionic.com/privacy-notice/).

from __future__ import print_function

import os
import sys
import ionicsdk

file_original = '../../sample-data/files/Message.pdf'
file_ciphertext = './Message-Protected.pdf'
file_plaintext = './Message.pdf'
source_dir = 'python/crypto-file-cipher-pdf'
this_dir = os.getcwd()

# run only from source directory
if not this_dir.endswith(source_dir):
    print("[!] Please run this sample from inside " + source_dir)
    sys.exit(1)

# read persistor password from environment variable
persistor_password = os.environ.get('IONIC_PERSISTOR_PASSWORD')
if (persistor_password == None):
    print("[!] Please provide the persistor password as env variable: IONIC_PERSISTOR_PASSWORD")
    sys.exit(1)

# initialize agent with password persistor
try:
    persistor_path = os.path.expanduser("~/.ionicsecurity/profiles.pw")
    persistor = ionicsdk.DeviceProfilePersistorPasswordFile(persistor_path, persistor_password)
    agent = ionicsdk.Agent(None, persistor)
except ionicsdk.exceptions.IonicException as e:
    print("Error initializing agent: {0}".format(e.message))
    sys.exit(-2)

# define attributes (optional)
mutable_attributes = {
    "classification": "Restricted"
}

# initialize aes cipher object
cipher = ionicsdk.FileCipherPdf(agent)

# encrypt
print("Encrypting message and saving to Ciphertext File: {}".format(file_ciphertext))
cipher.encrypt(file_original, file_ciphertext, mutableAttributes=mutable_attributes)

# decrypt
print("Decrypting ciphertext and saving to Plaintext File: {}".format(file_plaintext))
cipher.decrypt(file_ciphertext, file_plaintext)
