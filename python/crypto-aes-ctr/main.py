# (c) 2018 Ionic Security Inc.
# By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
# and the Privacy Policy (https://www.ionic.com/privacy-notice/).

import os
import sys
import ionicsdk
import binascii

message = "secret message"

# initialize agent with password persistor
try:
    persistorPath = os.path.expanduser("~/.ionicsecurity/profiles.pw")
    persistorPassword = os.environ.get('IONIC_PERSISTOR_PASSWORD')
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
print("Ciphertext : " + binascii.hexlify(ciphertext))
print("Plaintext  : " + plaintext)
