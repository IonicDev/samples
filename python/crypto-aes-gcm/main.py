# (c) 2018-2020 Ionic Security Inc.
# By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
# and the Privacy Policy (https://www.ionic.com/privacy-notice/).

from __future__ import print_function

import os
import sys
import ionicsdk
import binascii

message = b"secret message"
auth_data = b"data to authenticate"

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

# create new key 
try:
    key = agent.createkey()
    key_bytes = key.bytes
except ionicsdk.exceptions.IonicException as e:
    print("Error creating new key: {0}".format(e.message))
    sys.exit(-2)

# initialize aes cipher object
cipher = ionicsdk.AesGcmCipher(key_bytes, auth_data)

# encrypt
ciphertext = cipher.encryptbytes(message)

# decrypt
plaintext = cipher.decryptbytes(ciphertext)

# display
print("Ciphertext : " + binascii.hexlify(ciphertext).decode("ascii"))
print("Plaintext  : " + plaintext.decode())
