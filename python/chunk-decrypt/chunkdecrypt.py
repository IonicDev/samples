#!/usr/bin/env python

# (c) 2017 Ionic Security Inc.
# By using this code, I agree to the Terms & Conditions (https://www.ionic.com/terms-of-use/)
# and the Privacy Policy (https://www.ionic.com/privacy-notice/).

import ionicsdk

import sys
import platform
import os.path

# TODO: Update the below string to your encrypted value from IonicHelloWorld, e.g. "~!2!ABcd...AA!Nk...Wp!"
ENCRYPTED_TEXT = "{SET_ENCRYPTED_STRING_HERE}"

def get_agent():
  # Load a plaintext Device Profile, or SEP, from disk if on Linux (e.g., no default persistor).
  persistor = None
  if platform.system == 'Linux':
    SEP_FILE_PATH = os.path.expanduser("~/.ionicsecurity/profiles.pt")
    persistor = ionicsdk.DeviceProfilePersistorPlaintextFile(SEP_FILE_PATH)
    print("A plaintext SEP will be loaded from {0}".format(SEP_FILE_PATH))

  # Initialize the Ionic Agent (must be done before most Ionic operations).
  agent = ionicsdk.Agent(None, persistor)

  # Check if there are profiles and that there is one set as active.
  if not agent.hasanyprofiles() or not agent.hasactiveprofile():
    if not agent.hasanyprofiles():
      print("There are no device profiles on this device.")
    if not agent.hasactiveprofile():
      print("There is not an active device profile selected on this device.")
    print("Register (and select an active profile) this device before continuing.")
    sys.exit(-1)

  return agent

if __name__ == "__main__":
  if ENCRYPTED_TEXT == "{SET_ENCRYPTED_STRING_HERE}":
    print("You MUST set the output from ionichelloworld.py (or similar) to decrypt.")
    sys.exit(-1)

  agent = get_agent()
  if agent is None:
    sys.exit(-1)

  # Add metadata to describe the decryption context
  req_metadata = {"example-metadata-key": "example-metadata-value"}

  # Initialize a Chunk Cipher for doing string decryption
  cipher = ionicsdk.ChunkCipherAuto(agent)

  plaintext = None
  try:
    plaintext = cipher.decryptstr(ENCRYPTED_TEXT, metadata=req_metadata)
  except ionicsdk.exceptions.IonicException as e:
    print("Error decrypting: {0}".format(e.message))
    sys.exit(-2)

  print("Chunk-Encrypted String: " + ENCRYPTED_TEXT)
  print("Decrypted String: " + plaintext)
