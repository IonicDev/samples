#!/usr/bin/env python

# (c) 2017 Ionic Security Inc.
# By using this code, I agree to the Terms & Conditions (https://www.ionic.com/terms-of-use/)
# and the Privacy Policy (https://www.ionic.com/privacy-notice/).

import ionicsdk

import sys
import platform
import os.path

INPUT_STRING = "Hello World!"

# Load a plaintext Device Profile, or SEP, from disk if on Linux (e.g., no default persistor).
persistor = None
if platform.system == 'Linux':
  SEP_FILE_PATH = os.path.expanduser("~/.ionicsecurity/profiles.pt")
  persistor = ionicsdk.DeviceProfilePersistorPlaintextFile(SEP_FILE_PATH)
  print("A plaintext SEP will be loaded from {0}".format(SEP_FILE_PATH))

# Initialize the Ionic Agent (must be done before most Ionic operations).
try:
  agent = ionicsdk.Agent(None, persistor)
except ionicsdk.exceptions.IonicException as e:
  print("Error initializing agent: {0}".format(e.message))
  sys.exit(-3)

# Check if there are profiles.
if not agent.hasanyprofiles() or not agent.hasactiveprofile():
  if not agent.hasanyprofiles():
    print("There are no device profiles on this device.")
  if not agent.hasactiveprofile():
    print("There is not an active device profile selected on this device.")
  print("Register (and select an active profile) this device before continuing.")
  sys.exit(-1)

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
