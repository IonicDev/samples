#!/usr/bin/env python

# (c) 2017 Ionic Security Inc.
# By using this code, I agree to the Terms & Conditions (https://www.ionic.com/terms-of-use/)
# and the Privacy Policy (https://www.ionic.com/privacy-notice/).

import ionicsdk

import sys
import platform
import os.path

INPUT_STRING = "Hello World!"

PROFILE_PATH = os.path.expanduser("~/.ionicsecurity/profiles.pt")
persistor = ionicsdk.DeviceProfilePersistorPlaintextFile(PROFILE_PATH)

# Initialize the Ionic Agent (must be done before most Ionic operations).
try:
  agent = ionicsdk.Agent(None, persistor)
except ionicsdk.exceptions.IonicException as e:
  print("Error initializing agent: {0}".format(e.message))
  sys.exit(-3)

# Initialize a Chunk Cipher for doing string encryption
cipher = ionicsdk.ChunkCipherAuto(agent)

# Encrypt the string using an Ionic-managed Key
try:
  ciphertext = cipher.encryptstr(INPUT_STRING)
except ionicsdk.exceptions.IonicException as e:
  print("Error encrypting: {0}".format(e.message))
  sys.exit(-2)

print("Plain Text: " + INPUT_STRING)
print("Ionic Chunk Encrypted Text: " + ciphertext)
