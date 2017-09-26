#!/usr/bin/env python

# (c) 2017 Ionic Security Inc.
# By using this code, I agree to the Terms & Conditions (https://www.ionic.com/terms-of-use/)
# and the Privacy Policy (https://www.ionic.com/privacy-notice/).

import ionicsdk

import sys
import platform
import os.path

#NOTE: You MUST have the input file present before running this:
INPUT_FILE_PATH = "example_file_encrypted.docx"
OUTPUT_FILE_PATH = "example_file.docx"

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
  agent = get_agent()
  if agent is None:
    sys.exit(-1)

  # Add metadata to describe the decryption context
  req_metadata = {"example-metadata-key": "example-metadata-value"}

  # Encrypt the file using the classification
  cipher = ionicsdk.FileCipherAuto(agent)
  try:
    cipher.decrypt(INPUT_FILE_PATH, OUTPUT_FILE_PATH, metadata=req_metadata)
  except ionicsdk.exceptions.IonicException as e:
    print("Error decrypting: {0}".format(e.message))
    sys.exit(-2)

  print("Decrypted file {0} to {1}.".format(INPUT_FILE_PATH, OUTPUT_FILE_PATH))
