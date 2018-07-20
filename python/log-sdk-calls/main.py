# (c) 2018 Ionic Security Inc.
# By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
# and the Privacy Policy (https://www.ionic.com/privacy-notice/).

import os
import sys
import json
import ionicsdk
import binascii

logFilePath = "sample.log"
appendFile = False

# Setup SDK logging to a file.
ionicsdk.log.setup_simple(logFilePath, appendFile, ionicsdk.log.SEV_DEBUG)

# The message to encrypt.
message = "top secret message"

# Read persistor password from environment variable
persistorPassword = os.environ.get('IONIC_PERSISTOR_PASSWORD')
if persistorPassword == None:
    print("[!] Please provide the persistor password as env variable: IONIC_PERSISTOR_PASSWORD")
    sys.exit(1)

# Initialize agent with password persistor
try:
    persistorPath = os.path.expanduser("~/.ionicsecurity/profiles.pw")
    persistor = ionicsdk.DeviceProfilePersistorPasswordFile(persistorPath, persistorPassword)
    agent = ionicsdk.Agent(None, persistor)
except ionicsdk.exceptions.IonicException as e:
    print("Error initializing agent: {0}".format(e.message))
    sys.exit(-2)

# Initialize chunk cipher object
cipher = ionicsdk.ChunkCipherAuto(agent)

# Encrypt
ciphertext = cipher.encryptstr(message)

# Decrypt
plaintext = cipher.decryptstr(ciphertext)

# Verify encrypt and decrypt worked.
if message != plaintext:
    print("Encryption/Decryption does not match!")
    print("Message: {0} - PlainText: {1}".format(plaintext, message))
    sys.exit(-2)

# Display data
print("Ciphertext : {0}".format(ciphertext))
print("Plaintext  : {0}".format(plaintext))
