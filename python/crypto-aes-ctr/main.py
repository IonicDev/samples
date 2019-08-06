# (c) 2018-2019 Ionic Security Inc.
# By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
# and the Privacy Policy (https://www.ionic.com/privacy-notice/).

from __future__ import print_function

import os
import sys
import ionicsdk
import binascii

message = "secret message"

# read persistor password from environment variable
persistorPassword = os.environ.get('IONIC_PERSISTOR_PASSWORD')
if (persistorPassword == None):
    print("[!] Please provide the persistor password as env variable: IONIC_PERSISTOR_PASSWORD")
    sys.exit(1)

# initialize agent with password persistor
try:
    persistorPath = os.path.expanduser("~/.ionicsecurity/profiles.pw")
    persistor = ionicsdk.DeviceProfilePersistorPasswordFile(persistorPath, persistorPassword)
    agent = ionicsdk.Agent(None, persistor)
except ionicsdk.exceptions.IonicException as e:
    print("Error initializing agent: {0}".format(e.message))
    sys.exit(-2)

# create new key 
try:
    key = agent.createkey()
    key_bytes = key.bytes
except ionicsdk.exceptions.IonicException as e:
    print("Error creating new key: {0}".format(e.message))
    sys.exit(-2)

# initialize aes cipher object
cipher = ionicsdk.AesCtrCipher(key_bytes)

# encrypt
ciphertext = cipher.encryptbytes(message)

# decrypt
plaintext = cipher.decryptbytes(ciphertext)

# display data
print("Ciphertext : " + binascii.hexlify(ciphertext).decode("ascii"))
print("Plaintext  : " + plaintext)
